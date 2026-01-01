package com.c4.hero.domain.payroll.adjustment.service;

import com.c4.hero.domain.payroll.adjustment.entity.PayrollRaise;
import com.c4.hero.domain.payroll.adjustment.mapper.PayrollRaiseCommandMapper;
import com.c4.hero.domain.payroll.adjustment.repository.PayrollRaiseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <pre>
 * Class Name : PayrollRaiseCommandService
 * Description : 급여 인상 승인 시 인상 내역 저장 및 사원 기준 급여 갱신을 수행하는 Command 서비스
 *
 * History
 *  2025/12/31 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayrollRaiseCommandService {

    private final PayrollRaiseRepository raiseRepository;
    private final PayrollRaiseCommandMapper raiseCommandMapper;

    /**
     * 승인된 급여 인상 반영
     *
     * 처리 흐름
     *  - 급여 인상 이력(PayrollRaise) 저장
     *  - 사원 기준 급여(base salary)를 afterSalary로 갱신
     *
     * @param approvalDocId 전자결재 문서 ID
     * @param requestedBy  인상 요청자 (employee_id)
     * @param employeeId   급여 인상 대상 사원 ID
     * @param beforeSalary 인상 전 급여
     * @param afterSalary  인상 후 급여
     * @param reason       인상 사유
     * @param raisePercent 급여 인상률 (%)
     * @param effectiveMonth 인상 적용 급여월 (YYYY-MM)
     *
     * @throws IllegalStateException 기준 급여 업데이트 결과가 1건이 아닐 경우
     */
    @Transactional
    public void applyApprovedRaise(
            Integer approvalDocId,
            Integer requestedBy,
            Integer employeeId,
            Integer beforeSalary,
            Integer afterSalary,
            String reason,
            Double raisePercent,
            String effectiveMonth
    ) {
        PayrollRaise raise = PayrollRaise.builder()
                .reason(reason)
                .employeeId(employeeId)
                .beforeSalary(beforeSalary)
                .afterSalary(afterSalary)
                .raisePercent(raisePercent == null ? null : java.math.BigDecimal.valueOf(raisePercent))
                .effectiveMonth(effectiveMonth)
                .status("APPROVED")
                .requestedBy(requestedBy)
                .approvalDocId(approvalDocId)
                .build();

        raiseRepository.save(raise);

                int updatedRows = raiseCommandMapper.updateEmployeeBaseSalary(employeeId, afterSalary);
                log.info("base_salary 업데이트 결과 updatedRows={}, employeeId={}, afterSalary={}",
                        updatedRows, employeeId, afterSalary);

                        if (updatedRows != 1) {
                                throw new IllegalStateException("base_salary 업데이트 실패: employeeId=" + employeeId
                                        + ", afterSalary=" + afterSalary + ", updatedRows=" + updatedRows);
                    }

    }
}