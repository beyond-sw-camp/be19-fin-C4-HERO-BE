package com.c4.hero.domain.payroll.adjustment.service;

import com.c4.hero.domain.payroll.adjustment.entity.PayrollRaise;
import com.c4.hero.domain.payroll.adjustment.mapper.PayrollRaiseCommandMapper;
import com.c4.hero.domain.payroll.adjustment.repository.PayrollRaiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PayrollRaiseCommandService {

    private final PayrollRaiseRepository raiseRepository;
    private final PayrollRaiseCommandMapper raiseCommandMapper;

    @Transactional
    public void applyApprovedRaise(
            Integer approvalDocId,
            Integer requestedBy,
            Integer employeeId,
            Integer beforeSalary,
            Integer afterSalary,
            String reason,
            String effectiveMonth
    ) {
        PayrollRaise raise = PayrollRaise.builder()
                .reason(reason)
                .employeeId(employeeId)
                .beforeSalary(beforeSalary) // 프론트에서 내려줌
                .afterSalary(afterSalary)
                .effectiveMonth(effectiveMonth)
                .status("APPROVED")
                .requestedBy(requestedBy)
                .build();

        raiseRepository.save(raise);

        raiseCommandMapper.updateEmployeeBaseSalary(employeeId, afterSalary);

    }
}