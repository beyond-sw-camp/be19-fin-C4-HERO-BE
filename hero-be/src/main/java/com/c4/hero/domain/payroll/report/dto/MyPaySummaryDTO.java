package com.c4.hero.domain.payroll.report.dto;

import java.util.List;


/**
 * 내 급여(사원 페이지) 요약 DTO
 * 급여 페이지의 상단 요약데이터 + 수당 목록 + 공제 목록을 포함한다.
 * <pre>
 * Class Name: MyPaySummaryDTO
 * Description: 사원의 월별 급여 요약 및 지급/공제 항목 응답 DTO
 *
 * History
 * 2025/12/08 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
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
