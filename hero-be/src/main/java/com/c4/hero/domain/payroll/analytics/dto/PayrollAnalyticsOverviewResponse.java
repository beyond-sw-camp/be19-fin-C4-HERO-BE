package com.c4.hero.domain.payroll.analytics.dto;

import java.util.List;

/**
 * <pre>
 * DTO Name : PayrollAnalyticsOverviewResponse
 * Description : 급여 분석(Overview) 요약 응답 DTO
 *
 * History
 *   2026/01/02 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record PayrollAnalyticsOverviewResponse(
        String month,
        Kpi kpi,
        NetPay netPay,
        List<TrendPoint> trend
) {

    /**
     * 급여 요약 KPI
     */
    public record Kpi(
            Integer headcount,
            Integer grossTotal,
            Integer deductionTotal,
            Integer laborCostTotal
    ) {}

    /**
     * 실지급(Net Pay) 요약 지표
     */
    public record NetPay(
            Integer avgNetPay,
            Integer medianNetPay,
            Double momChangeRate
    ) {}

    /**
     * 월별 인건비 추이 포인트
     */
    public record TrendPoint(
            String month,
            Integer laborCostTotal,
            Double momChangeRate
    ) {}
}