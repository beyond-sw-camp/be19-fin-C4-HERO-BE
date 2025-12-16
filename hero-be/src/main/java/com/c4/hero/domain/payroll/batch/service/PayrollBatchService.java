package com.c4.hero.domain.payroll.batch.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.domain.payroll.batch.entity.PayrollBatch;
import com.c4.hero.domain.payroll.batch.repository.BatchRepository;
import com.c4.hero.domain.payroll.common.enums.PayrollBatchStatus;
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
 *
 * 도메인 규칙
 *  - salaryMonth(YYYY-MM) 기준 배치는 1개만 존재 가능
 *  - CONFIRMED 상태 배치는 수정/재계산 불가 (Lock)
 *
 * History
 *  2025/12/15 - 동근 최초 작성
 * </pre>
 *
 *  @author 동근
 *  @version 1.0
 */

@Service
@RequiredArgsConstructor
@Transactional
public class PayrollBatchService {

    private final BatchRepository batchRepository;
    private final PayrollCalculationService calculationService;

    /**
     * 급여 배치 생성
     *
     * @param month 급여월 (YYYY-MM)
     * @return 생성된 배치 ID
     *
     * @throws BusinessException PAYROLL_BATCH_DUPLICATED
     *         동일 월의 배치가 이미 존재하는 경우
     */
    public Integer createBatch(String month) {
        if (batchRepository.existsBySalaryMonth(month)) {
            throw new BusinessException(ErrorCode.PAYROLL_BATCH_DUPLICATED);
        }
        return batchRepository.save(PayrollBatch.create(month)).getBatchId();
    }

    /**
     * 급여 배치 계산 실행
     *
     * @param batchId      급여 배치 ID
     * @param employeeIds  계산 대상 사원 ID 목록
     */
    public void calculate(Integer batchId, List<Integer> employeeIds) {
        PayrollBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYROLL_BATCH_NOT_FOUND));

        if (batch.getStatus() == PayrollBatchStatus.CONFIRMED) {
            throw new BusinessException(ErrorCode.PAYROLL_BATCH_LOCKED);
        }

        calculationService.calculateBatch(batch, employeeIds);
    }

    /**
     * 급여 배치 확정 처리
     *
     * @param batchId 급여 배치 ID
     */
    public void confirm(Integer batchId) {
        PayrollBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYROLL_BATCH_NOT_FOUND));
        batch.confirm();
    }
}

