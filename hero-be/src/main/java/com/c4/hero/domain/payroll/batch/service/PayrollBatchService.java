package com.c4.hero.domain.payroll.batch.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.batch.dto.PayrollBatchTargetEmployeeResponseDTO;
import com.c4.hero.domain.payroll.batch.entity.Payroll;
import com.c4.hero.domain.payroll.batch.entity.PayrollBatch;
import com.c4.hero.domain.payroll.batch.mapper.PayrollBatchQueryMapper;
import com.c4.hero.domain.payroll.batch.repository.BatchRepository;
import com.c4.hero.domain.payroll.batch.repository.PayrollRepository;
import com.c4.hero.domain.payroll.common.type.PayrollBatchStatus;
import com.c4.hero.domain.payroll.common.type.PayrollStatus;
import com.c4.hero.domain.payroll.payment.entity.PaymentHistory;
import com.c4.hero.domain.payroll.payment.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

/**
 * <pre>
 * Class Name : PayrollBatchService
 * Description : 월별 급여 배치(Batch) 관리 서비스
 *
 * 역할
 *  - 급여 배치 생성 (월 단위 중복 방지)
 *  - 급여 배치 계산 실행 요청 (상태/락 검증 후 계산 서비스 위임)
 *  - 급여 배치 확정 처리 (상태 전이)
 *  - 확정된 배치 기준 급여 지급 처리(PaymentHistory 연동)
 *
 * 도메인 규칙
 *  - salaryMonth(YYYY-MM) 기준 배치는 1개만 존재 가능
 *  - CONFIRMED 상태 배치는 수정/재계산 불가 (Lock)
 *  - FAILED 상태 급여가 존재하는 배치는 확정/지급 불가
 *
 * History
 *  2025/12/15 - 동근 최초 작성
 *  2025/12/18 - 동근 급여 지급(pay) 로직 및 PaymentHistory 연동 추가
 *             - 클래스 레벨 트랜잭션 제거 및 상태 전이 트랜잭션 분리
 *  2026/01/03 - 동근 권한 인가 정책 추가
 * </pre>
 *
 *  @author 동근
 *  @version 1.2
 */
@PayrollAdminOnly
@Service
@RequiredArgsConstructor
public class PayrollBatchService {

    private final BatchRepository batchRepository;
    private final PayrollCalculationService calculationService;
    private final PayrollBatchStatusTxService batchStatusTxService;
    private final PayrollRepository payrollRepository;
    private final PayrollBatchQueryMapper batchQueryMapper;
    private final PaymentHistoryRepository paymentHistoryRepository;

    /**
     * 급여 배치 생성
     *
     * @param month 급여월 (YYYY-MM)
     * @return 생성된 배치 ID
     *
     * @throws BusinessException PAYROLL_BATCH_DUPLICATED
     *         동일 월의 배치가 이미 존재하는 경우
     */
    @Transactional
    public Integer createBatch(String month, Integer employeeId) {
        if (batchRepository.existsBySalaryMonth(month)) {
            throw new BusinessException(ErrorCode.PAYROLL_BATCH_DUPLICATED);
        }
        return batchRepository.save(PayrollBatch.create(month, employeeId)).getBatchId();
    }

    /**
     * 급여 배치 계산 실행
     *
     * @param batchId     급여 배치 ID
     * @param employeeIds 선택 사원 ID 목록
     */
    public void calculate(Integer batchId, List<Integer> employeeIds) {
        PayrollBatch batch = getBatchOrThrow(batchId);

        if (batch.getStatus() == PayrollBatchStatus.CONFIRMED || batch.getStatus() == PayrollBatchStatus.PAID) {
            throw new BusinessException(ErrorCode.PAYROLL_BATCH_LOCKED);
        }

        List<Integer> targets;
        if (employeeIds == null || employeeIds.isEmpty()) {
            targets = batchQueryMapper.selectBatchTargetEmployees()
                    .stream()
                    .map(PayrollBatchTargetEmployeeResponseDTO::employeeId)
                    .toList();
        } else {
            targets = employeeIds;
        }

        if (targets.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        calculationService.calculateEmployees(batch, targets);

        if (batch.getStatus() == PayrollBatchStatus.READY) {
            batchStatusTxService.markCalculatedInNewTx(batchId);
        }
    }

    /**
     * 급여 배치 확정 처리
     *
     * @param batchId 급여 배치 ID
     */
    @Transactional
    public void confirm(Integer batchId, Integer employeeId) {
        PayrollBatch batch = getBatchOrThrow(batchId);

        // 배치 상태 검증: CALCULATED 상태에서만 CONFIRMED로 전환 가능
        if (batch.getStatus() != PayrollBatchStatus.CALCULATED) {
            throw new BusinessException(ErrorCode.PAYROLL_BATCH_INVALID_STATUS_TRANSITION);
        }

        // FAILED 급여가 남아있으면 확정 불가
        if (payrollRepository.existsByBatchIdAndStatus(batchId, PayrollStatus.FAILED)) {
            throw new BusinessException(ErrorCode.PAYROLL_BATCH_HAS_FAILED);
        }

        // 배치에 속한 급여가 하나도 없는데 확정 누르는 경우 방어
        if (!payrollRepository.existsByBatchId(batchId)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "확정할 급여 데이터가 없습니다.");
        }

        // 배치 내 급여 상태 일괄 CONFIRMED로 변경 (벌크 업데이트)
        int updatedCount = payrollRepository.updateStatusByBatchId(batchId, PayrollStatus.CONFIRMED);
        // 필요하면 updatedCount로 로그 남겨도 됨

        // 배치 상태 CONFIRMED로 전환
        batch.confirm(employeeId);
    }

    /**
     * 배치 ID 기준 배치 조회 유틸리티
     *
     * @param batchId 배치 ID
     * @return 조회된 PayrollBatch 엔티티
     */
    private PayrollBatch getBatchOrThrow(Integer batchId) {
        return batchRepository.findById(batchId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYROLL_BATCH_NOT_FOUND));
    }

    /**
     * 급여 배치 지급 처리
     *
     * @param batchId 급여 배치 ID
     */
    @Transactional
    public void pay(Integer batchId, Integer employeeId) {
        PayrollBatch batch = getBatchOrThrow(batchId);

        // 상태 검증 => CONFIRMED만 지급 가능
        if (batch.getStatus() != PayrollBatchStatus.CONFIRMED) {
            throw new BusinessException(ErrorCode.PAYROLL_BATCH_INVALID_STATUS_TRANSITION);
        }

        // FAILED가 남아있으면 지급 막기 => confirm에서 걸러지지만 안정성 올리려고 추가해뒀습니당
        if (payrollRepository.existsByBatchIdAndStatus(batchId, PayrollStatus.FAILED)) {
            throw new BusinessException(ErrorCode.PAYROLL_BATCH_HAS_FAILED);
        }

        List<Payroll> payrolls = payrollRepository.findAllByBatchId(batchId);
        if (payrolls.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "지급할 급여 데이터가 없습니다.");
        }

        for (Payroll pr : payrolls) {
            if (paymentHistoryRepository.existsByPayrollId(pr.getPayrollId())) continue;

            paymentHistoryRepository.save(
                    PaymentHistory.completed(
                            pr.getPayrollId(),
                            pr.getTotalPay(),
                            null
                    )
            );
        }
        batch.markPaid(employeeId);
    }
}

