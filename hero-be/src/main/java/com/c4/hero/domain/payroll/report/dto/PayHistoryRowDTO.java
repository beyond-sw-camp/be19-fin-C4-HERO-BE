package com.c4.hero.domain.payroll.report.dto;


/**
 * 급여 이력 테이블 Row DTO
 * 급여 이력 페이지에서 월별 급여 항목을 테이블 행 단위로 보여줄 때 사용
 * <pre>
 * Class Name: PayHistoryRowDTO
 * Description: 사원의 월별 급여 항목 (기본급/수당/공제/실수령 등) 테이블 응답 DTO
 *
 * History
 * 2025/12/08 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record PayHistoryRowDTO(
        String salaryMonth,
        int baseSalary,
        int allowanceTotal,
        int deductionTotal,
        int netPay,
        String remark //비고
) {}
