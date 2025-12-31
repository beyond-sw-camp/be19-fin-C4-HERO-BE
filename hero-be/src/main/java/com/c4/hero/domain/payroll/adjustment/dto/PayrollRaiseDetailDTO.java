package com.c4.hero.domain.payroll.adjustment.dto;

import lombok.Data;

@Data
public class PayrollRaiseDetailDTO {
    private Integer employeeId;
    private String reason;
    private Integer beforeSalary;
    private Integer afterSalary;
    private String effectiveMonth; // "YYYY-MM"
}
