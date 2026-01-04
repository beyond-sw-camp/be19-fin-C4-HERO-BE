package com.c4.hero.domain.payroll.analytics.mapper;

import com.c4.hero.domain.payroll.analytics.dto.PayrollAnalyticsCompositionRows;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <pre>
 * Mapper Name : PayrollAnalyticsCompositionMapper
 * Description : 급여 구성(Composition) 분석 조회 전용 MyBatis 매퍼
 *
 * History
 *   2026/01/02 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Mapper
public interface PayrollAnalyticsCompositionMapper {

    /**
     * 부서별 인건비 합계 조회
     *
     * @param month 기준 월 (YYYY-MM)
     * @return 부서별 인건비 집계 Row 목록
     */
    List<PayrollAnalyticsCompositionRows.DeptShareAggRow> selectDeptShareAgg(@Param("month") String month);

    /**
     * 항목 유형별 합계 조회
     * (수당 / 공제)
     *
     * @param month    기준 월 (YYYY-MM)
     * @param itemType 항목 유형 (ALLOWANCE / DEDUCTION)
     * @return 항목별 합계 집계 Row 목록
     */
    List<PayrollAnalyticsCompositionRows.ItemAggRow> selectItemAgg(
            @Param("month") String month,
            @Param("itemType") String itemType
    );

    /**
     * 부담금(보험 공제) 집계 조회
     *
     * @param month 기준 월 (YYYY-MM)
     * @return 부담금 집계 Row 목록
     */
    List<PayrollAnalyticsCompositionRows.BurdenAggRow> selectBurdenAgg(@Param("month") String month);

    /**
     * 월별 급여 구성 변화(스택 차트) 조회
     *
     * @param fromMonth 시작 월 (YYYY-MM)
     * @param toMonth   종료 월 (YYYY-MM)
     * @return 월별 급여 구성 집계 Row 목록
     */
    List<PayrollAnalyticsCompositionRows.MonthStackAggRow> selectStackTrend(
            @Param("fromMonth") String fromMonth,
            @Param("toMonth") String toMonth
    );
}
