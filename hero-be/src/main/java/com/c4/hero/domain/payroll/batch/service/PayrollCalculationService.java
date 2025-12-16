package com.c4.hero.domain.payroll.batch.service;

import com.c4.hero.domain.payroll.batch.entity.Payroll;
import com.c4.hero.domain.payroll.batch.entity.PayrollBatch;
import com.c4.hero.domain.payroll.batch.entity.PayrollItem;
import com.c4.hero.domain.payroll.batch.repository.PayrollItemRepository;
import com.c4.hero.domain.payroll.batch.repository.PayrollRepository;
import com.c4.hero.domain.payroll.integration.attendance.service.PayrollAttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <pre>
 * Class Name : PayrollCalculationService
 * Description : 월별 급여 배치 계산 서비스
 *
 * MVP 개발
 *  - 수당/공제/보너스 등은 추후 확장 예정입니다!!
 *
 * 트랜잭션 정책
 *  - 클래스 레벨 @Transactional 적용했습니다.
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
public class PayrollCalculationService {

    private final PayrollRepository payrollRepository;
    private final PayrollItemRepository payrollItemRepository;
    private final PayrollAttendanceService attendanceService;

    /**
     * 급여 배치 계산 실행
     *
     * @param batch       급여 배치 엔티티
     * @param employeeIds 계산 대상 사원 ID 목록
     */
    public void calculateBatch(PayrollBatch batch, List<Integer> employeeIds) {

        // 대상 사원 목록을 순회하며 사원 단위 급여 계산 수행
        for (Integer empId : employeeIds) {
            try {
                int baseSalary = attendanceService.getBaseSalary(empId);
                int overtimePay = attendanceService.calculateOvertime(batch.getSalaryMonth(), empId);

                // 계산 완료 급여 엔티티 생성 및 저장
                Payroll payroll = Payroll.calculated(
                        empId,
                        batch.getBatchId(),
                        batch.getSalaryMonth(),
                        baseSalary,
                        overtimePay,
                        0,
                        0
                );
                // 급여 항목 저장 (MVP => 연장근무수당만 저장)
                payrollRepository.save(payroll);

                payrollItemRepository.save(
                        PayrollItem.of(
                                payroll.getPayrollId(),
                                "ALLOWANCE",
                                "OVERTIME",
                                "연장근무수당",
                                overtimePay,
                                "Y"
                        )
                );

            } catch (Exception e) {
                // 사원 단위 계산 실패 처리(FAILED 상태로 저장, 배치 계산은 계속 진행)
                Payroll fail = Payroll.failed(
                        empId,
                        batch.getBatchId(),
                        batch.getSalaryMonth()
                );
                payrollRepository.save(fail);
            }
        }
        // 모든 사원 계산이 끝난 후 배치 상태를 CALCULATED로 전이
        batch.markCalculated();
    }
}

