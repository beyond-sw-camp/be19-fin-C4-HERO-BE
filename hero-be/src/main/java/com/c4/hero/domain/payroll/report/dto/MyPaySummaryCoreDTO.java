package com.c4.hero.domain.payroll.report.dto;

/**
 *  월별 급여 요약 Core DTO.
 *   MyBatis로 월별 급여 핵심 데이터를 직접 조회, 서비스/컨트롤러 계층으로 전달할 때 사용하는 내부용 DTO.
 * <pre>
 * Class Name: MyPaySummaryCoreDTO
 * Description:월별 급여 요약 데이터 (Base Salary, 공제합계, 실수령, 근무시간 등)
 *
 * History
 * 2025/12/08 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
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