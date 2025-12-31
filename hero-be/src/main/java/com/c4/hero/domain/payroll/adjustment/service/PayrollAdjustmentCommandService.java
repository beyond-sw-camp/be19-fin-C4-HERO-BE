package com.c4.hero.domain.payroll.adjustment.service;

import com.c4.hero.domain.payroll.adjustment.entity.PayrollAdjustment;
import com.c4.hero.domain.payroll.adjustment.repository.PayrollAdjustmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PayrollAdjustmentCommandService {

    private final PayrollAdjustmentRepository repository;

    @Transactional
    public void applyApprovedAdjustment(
            Integer approvalDocId,
            Integer createdBy,
            Integer payrollId,
            String reason,
            String sign,
            Integer amount,
            String effectiveMonth
    ) {
        PayrollAdjustment adj = PayrollAdjustment.builder()
                .reason(reason)
                .sign(sign)
                .amount(amount == null ? 0 : amount)
                .effectiveMonth(effectiveMonth)
                .status("APPROVED")
                .payrollId(payrollId)
                .createdBy(createdBy)
                .build();

        repository.save(adj);
    }
}
