package com.c4.hero.domain.payroll.analytics.dto;

import java.util.List;

/**
 * <pre>
 * DTO Name : PayrollAnalyticsCompositionResponse
 * Description : 급여 구성(Composition) 분석 응답 DTO
 *
 * History
 *   2026/01/02 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record PayrollAnalyticsCompositionResponse(
        String month,
        List<DeptShareRow> deptShare,
        ItemSummary items,
        List<BurdenRow> burden,
        List<MonthStackRow> stackTrend
) {
    /**
     * 부서별 인건비 비중 Row
     */
    public record DeptShareRow(
            Integer departmentId,
            String departmentName,
            Integer laborCostTotal,
            Double sharePct
    ) {}

    /**
     * 수당 / 공제 항목 요약
     */
    public record ItemSummary(
            List<ItemRow> allowance,
            List<ItemRow> deduction
    ) {}

    /**
     * 수당/공제 단일 항목 Row
     */
    public record ItemRow(
            String itemCode,
            String itemName,
            Integer amountTotal,
            Double sharePct
    ) {}

    /**
     * 사용자/회사 부담 공제 항목 Row
     * (4대보험 등)
     */
    public record BurdenRow(
            String deductionId,
            String deductionName,
            Integer employeeAmount,
            Integer employerAmount,
            Integer totalAmount
    ) {}

    /**
     * 월별 급여 구성 스택 차트 Row
     */
    public record MonthStackRow(
            String month,
            Integer baseTotal,
            Integer allowanceTotal,
            Integer overtimeTotal,
            Integer bonusTotal,
            Integer deductionTotal,
            Integer laborCostTotal
    ) {}
}
