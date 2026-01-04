package com.c4.hero.domain.payroll.analytics.dto;

/**
 * <pre>
 * DTO Name : PayrollAnalyticsCompositionRows
 * Description : 급여 구성(Composition) 분석 집계용 Row DTO 모음
 *
 * History
 *   2026/01/02 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public class PayrollAnalyticsCompositionRows {

    /**
     * 부서별 인건비 합계 집계 Row
     */
    public record DeptShareAggRow(
            Integer departmentId,
            String departmentName,
            Integer laborCostTotal
    ) {}

    /**
     * 수당/공제 항목별 금액 합계 집계 Row
     */
    public record ItemAggRow(
            String itemCode,
            String itemName,
            Integer amountTotal
    ) {}

    /**
     * 사용자/회사 부담 공제 집계 Row
     * (4대보험 등)
     */
    public record BurdenAggRow(
            String deductionId,
            String deductionName,
            Integer employeeAmount,
            Integer employerAmount,
            Integer totalAmount
    ) {}

    /**
     * 월별 급여 구성 스택 차트 집계 Row
     */
    public record MonthStackAggRow(
            String month,
            Integer baseTotal,
            Integer allowanceTotal,
            Integer overtimeTotal,
            Integer bonusTotal,
            Integer deductionTotal,
            Integer laborCostTotal
    ) {}
}
