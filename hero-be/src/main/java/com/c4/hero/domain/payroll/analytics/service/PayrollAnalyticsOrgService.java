package com.c4.hero.domain.payroll.analytics.service;

import com.c4.hero.domain.payroll.analytics.dto.PayrollAnalyticsOrgResponse;

/**
 * <pre>
 * Service Name : PayrollAnalyticsOrgService
 * Description  : 급여 조직 분석(Organization) 서비스 인터페이스
 *
 * History
 *   2026/01/02 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public interface PayrollAnalyticsOrgService {

    /**
     * 급여 조직 분석 데이터 조회
     *
     * @param month  기준 월 (YYYY-MM)
     * @param deptId 선택 부서 ID (null 이면 전체 조직 기준)
     * @return 급여 조직 분석 응답 DTO
     */
    PayrollAnalyticsOrgResponse getOrganization(String month, Integer deptId);
}
