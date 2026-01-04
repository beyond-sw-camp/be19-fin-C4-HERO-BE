package com.c4.hero.domain.payroll.analytics.service;

import com.c4.hero.domain.payroll.analytics.dto.PayrollAnalyticsCompositionResponse;

/**
 * <pre>
 * Service Name : PayrollAnalyticsCompositionService
 * Description  : 급여 구성(Composition) 분석 서비스 인터페이스
 *
 * History
 *   2026/01/02 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public interface PayrollAnalyticsCompositionService {

    /**
     * 급여 구성(Composition) 분석 데이터 조회
     *
     * @param month        기준 월 (YYYY-MM)
     * @param trendMonths  추이 조회 개월 수 (null 이면 기본 범위 적용)
     * @return 급여 구성 분석 응답 DTO
     */
    PayrollAnalyticsCompositionResponse getComposition(String month, Integer trendMonths);
}
