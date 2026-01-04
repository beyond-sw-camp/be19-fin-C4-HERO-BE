package com.c4.hero.domain.payroll.batch.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.adjustment.entity.PayrollRaise;
import com.c4.hero.domain.payroll.adjustment.mapper.PayrollAdjustmentQueryMapper;
import com.c4.hero.domain.payroll.adjustment.repository.PayrollRaiseRepository;
import com.c4.hero.domain.payroll.batch.entity.Payroll;
import com.c4.hero.domain.payroll.batch.entity.PayrollBatch;
import com.c4.hero.domain.payroll.batch.entity.PayrollItem;
import com.c4.hero.domain.payroll.batch.repository.PayrollItemRepository;
import com.c4.hero.domain.payroll.batch.repository.PayrollRepository;
import com.c4.hero.domain.payroll.integration.attendance.service.PayrollAttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <pre>
 * Class Name : PayrollEmployeeCalculateTxService
 * Description : 사원 단위 급여 계산 트랜잭션 서비스 (단일 사원 기준 급여 계산 담당)
 *
 * History
 *   2025/12/18 - 동근 최초 작성
 *   2026/01/03 - 동근 권한 인가 정책 추가
 * </pre>
 *
 * @author 동근
 * @version 1.1
 */
@PayrollAdminOnly
@Service
@RequiredArgsConstructor
public class PayrollEmployeeCalculateTxService {

    private final PayrollRepository payrollRepository;
    private final PayrollItemRepository payrollItemRepository;
    private final PayrollAttendanceService attendanceService;
    private final PayrollRaiseRepository payrollRaiseRepository;
    private final PayrollAdjustmentQueryMapper payrollAdjustmentQueryMapper;

    /**
     * 단일 사원 급여 계산 (REQUIRES_NEW 트랜잭션 사용)
     *
     * @param batch  급여 배치 엔티티
     * @param empId  사원 ID
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void calculateOne(PayrollBatch batch, Integer empId) {
        try {
            int baseSalary = attendanceService.getBaseSalary(empId); // 근태 연동(기본급 조회)

            baseSalary = payrollRaiseRepository
                    .findTopByEmployeeIdAndEffectiveMonthAndStatusOrderByRaiseIdDesc(
                            empId, batch.getSalaryMonth(), "APPROVED"
                    )
                    .map(PayrollRaise::getAfterSalary)
                    .orElse(baseSalary);

            int overtimePay = attendanceService.calculateOvertime(batch.getSalaryMonth(), empId); //연장근무 수당 계산

            Payroll payroll = payrollRepository
                    .findByEmployeeIdAndSalaryMonth(empId, batch.getSalaryMonth()) //기존 급여가 있으면 조회
                    .orElseGet(() -> Payroll.ready(empId, batch.getBatchId(), batch.getSalaryMonth()));
            //없으면 ready상태 엔티티 생성

            if (payroll.isLocked()) return; // 상태 검증용 (true상태면 계산 스킵)

            int allowanceTotal = payroll.getAllowanceTotal() == null ? 0 : payroll.getAllowanceTotal();
            int deductionTotal = payroll.getDeductionTotal() == null ? 0 : payroll.getDeductionTotal();

            int manualAdjustNet = payrollAdjustmentQueryMapper
                    .sumApprovedAdjustmentNet(empId, batch.getSalaryMonth());
            if (manualAdjustNet != 0) {
                // 조정은 "수당/공제" 어느 쪽이든 될 수 있는데,
                // 현재 네 Payroll 구조는 allowanceTotal/deductionTotal로 합산해서 totalPay 계산하니까
                // net이 +면 allowanceTotal에, -면 deductionTotal에 넣는 게 제일 무난함.
                if (manualAdjustNet > 0) allowanceTotal += manualAdjustNet;
                else deductionTotal += Math.abs(manualAdjustNet);
            }


            //급여 계산 적용하는 로직
            payroll.applyCalculated(batch.getBatchId(), baseSalary, overtimePay, allowanceTotal, deductionTotal); // 호출로 급여 관련 반영
            Payroll saved = payrollRepository.save(payroll);

            //연장근무 수당 항목 갱신
            payrollItemRepository.deleteByPayrollIdAndItemTypeAndItemCode(
                    saved.getPayrollId(), "ALLOWANCE", "OVERTIME"
            );
            if (overtimePay > 0) {
                payrollItemRepository.save(
                        PayrollItem.of(saved.getPayrollId(), "ALLOWANCE", "OVERTIME", "연장근무수당", overtimePay, "Y")
                );
            }
        } catch (BusinessException be) {
            saveFailed(batch, empId, be.getMessage());
        } catch (Exception e) {
            saveFailed(batch, empId, "시스템 오류로 계산에 실패했습니다.");
        }
    }

    /**
     * 급여 계산 실패 시 FAILED 상태로 저장하는 유틸리티
     *
     * @param batch   급여 배치 엔티티
     * @param empId   사원 ID
     * @param message 실패 사유 메시지
     */
    private void saveFailed(PayrollBatch batch, Integer empId, String message) {
        Payroll fail = payrollRepository
                .findByEmployeeIdAndSalaryMonth(empId, batch.getSalaryMonth())
                .orElseGet(() -> Payroll.failed(empId, batch.getBatchId(), batch.getSalaryMonth()));

        if (!fail.isLocked()) {
            fail.markFailed(batch.getBatchId(), message);
            payrollRepository.save(fail);

        }
    }
}
