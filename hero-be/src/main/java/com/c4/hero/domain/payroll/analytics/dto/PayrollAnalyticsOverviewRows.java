package com.c4.hero.domain.payroll.analytics.dto;

/**
 * <pre>
 * DTO Name : PayrollAnalyticsOverviewRows
 * Description : 급여 분석(Overview) 집계용 Row DTO 모음
 *
 * History
 *   2026/01/02 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public class PayrollAnalyticsOverviewRows {

    /**
     * KPI 집계 Row
     */
    public record OverviewAggRow(
            Integer headcount,
            Integer grossTotal,
            Integer deductionTotal,
            Integer laborCostTotal
    ) {}

    /**
     * 실지급 평균/중앙값 집계 Row
     */
    public record NetPayStatsRow(
            Integer avgNetPay,
            Integer medianNetPay
    ) {}

    /**
     * 월별 인건비 추이 집계 Row
     */
    public record TrendAggRow(
            String month,
            Integer laborCostTotal
    ) {}
}
