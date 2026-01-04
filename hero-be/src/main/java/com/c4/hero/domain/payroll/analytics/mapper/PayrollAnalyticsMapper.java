package com.c4.hero.domain.payroll.analytics.mapper;

import com.c4.hero.domain.payroll.analytics.dto.PayrollAnalyticsOverviewRows;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <pre>
 * Mapper Name : PayrollAnalyticsMapper
 * Description : 급여 분석(Overview) 조회 전용 MyBatis 매퍼
 *
 * History
 *   2026/01/02 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Mapper
public interface PayrollAnalyticsMapper {

    /**
     * 급여 분석 Overview KPI 집계 조회
     *
     * @param month 기준 월 (YYYY-MM)
     * @return KPI 집계 Row
     */
    PayrollAnalyticsOverviewRows.OverviewAggRow selectOverviewAgg(@Param("month") String month);

    /**
     * 실지급 평균/중앙값 통계 조회
     *
     * @param month 기준 월 (YYYY-MM)
     * @return 실지급 통계 집계 Row
     */
    PayrollAnalyticsOverviewRows.NetPayStatsRow selectNetPayStats(@Param("month") String month);

    /**
     * 전월 평균 실지급액 조회
     * (전월 대비 증감률 계산용)
     *
     * @param prevMonth 전월 (YYYY-MM)
     * @return 전월 평균 실지급액
     */
    Integer selectPrevMonthAvgNetPay(@Param("prevMonth") String prevMonth);

    /**
     * 월별 인건비 추이 조회
     *
     * @param fromMonth 시작 월 (YYYY-MM)
     * @param toMonth   종료 월 (YYYY-MM)
     * @return 월별 인건비 집계 Row 목록
     */
    List<PayrollAnalyticsOverviewRows.TrendAggRow> selectLaborCostTrend(
            @Param("fromMonth") String fromMonth,
            @Param("toMonth") String toMonth
    );
}
