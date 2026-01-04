package com.c4.hero.domain.payroll.analytics.service;

import com.c4.hero.domain.payroll.analytics.dto.PayrollAnalyticsOverviewResponse;

/**
 * <pre>
 * Service Name : PayrollAnalyticsService
 * Description  : 급여 분석(Overview) 서비스 인터페이스
 *
 * History
 *   2026/01/02 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public interface PayrollAnalyticsService {

    /**
     * 급여 분석 Overview 데이터 조회
     *
     * @param month        기준 월 (YYYY-MM)
     * @param trendMonths  추이 조회 개월 수 (null 이면 기본 범위 적용)
     * @return 급여 분석 Overview 응답 DTO
     */
    PayrollAnalyticsOverviewResponse getOverview(String month, Integer trendMonths);
}