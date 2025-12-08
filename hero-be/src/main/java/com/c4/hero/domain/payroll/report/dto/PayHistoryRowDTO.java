package com.c4.hero.domain.payroll.report.dto;

// 급여 이력 테이블 행 단위 DTO
public record PayHistoryRowDTO(
        String salaryMonth,
        int baseSalary,
        int allowanceTotal,
        int deductionTotal,
        int netPay,
        String remark //비고
) {}
