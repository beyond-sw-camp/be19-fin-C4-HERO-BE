package com.c4.hero.domain.payroll.report.dto;

// DB에서 월별 급여 요약 정보를 직접 조회해서 가져오는 DTO
public record MyPaySummaryCoreDTO(
        String salaryMonth,
        int baseSalary,
        int netPay,
        int grossPay,
        int totalDeduction,
        int workDays,
        int workHours,
        int overtimeHours,
        String payDayLabel,
        String bankName,
        String bankAccountNumber,
        String accountHolder
) {}