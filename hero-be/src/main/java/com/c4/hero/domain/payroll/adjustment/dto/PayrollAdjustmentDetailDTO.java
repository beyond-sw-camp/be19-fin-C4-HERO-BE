package com.c4.hero.domain.payroll.adjustment.dto;

import lombok.Data;

@Data
public class PayrollAdjustmentDetailDTO {
    private Integer payrollId;
    private String reason;
    private String sign;          // "+" / "-"
    private Integer amount;
    private String effectiveMonth; // "YYYY-MM"
}
