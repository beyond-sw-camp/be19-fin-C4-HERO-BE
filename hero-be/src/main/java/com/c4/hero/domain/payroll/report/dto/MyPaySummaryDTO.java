package com.c4.hero.domain.payroll.report.dto;

import java.util.List;

// 내 급여 페이지에서 사용하는 DTO
//핵심 요약 정보 + 수당 목록 + 공제목
public record MyPaySummaryDTO
        (String salaryMonth,
         int basesalary,
         int netPay, // 실수령액
         int grossPay, // 지급총액 (기본급+수당+상여+연장수당)
         int totalDeduction, // 공제 총액
         int workDays,
         int workHours,
         int overtimeHours,
         String payDayLabel, // "매월 25일"
         String bankName,
         String bankAccountNumber,
         String accountHolder,
         List<PayItemDTO> allowances,
         List<PayItemDTO> deductions ) {}
