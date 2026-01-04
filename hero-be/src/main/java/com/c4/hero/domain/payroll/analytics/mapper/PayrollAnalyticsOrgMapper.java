package com.c4.hero.domain.payroll.analytics.mapper;

import com.c4.hero.domain.payroll.analytics.dto.PayrollAnalyticsOrgResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <pre>
 * Mapper Name : PayrollAnalyticsOrgMapper
 * Description : 급여 조직 분석(Organization) 조회 전용 MyBatis 매퍼
 *
 * History
 *   2026/01/02 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Mapper
public interface PayrollAnalyticsOrgMapper {

    /**
     * 전체 조직 인건비 합계 조회
     * (부서별 인건비 비중 계산용)
     *
     * @param month 기준 월 (YYYY-MM)
     * @return 전체 인건비 합계
     */
    Integer selectTotalLaborCost(@Param("month") String month);

    /**
     * 전체 조직 KPI 조회
     *
     * @param month 기준 월 (YYYY-MM)
     * @return 조직 KPI 집계 Row
     */
    PayrollAnalyticsOrgResponse.OrgKpi selectOrgKpiAll(@Param("month") String month);

    /**
     * 선택 부서 KPI 조회
     *
     * @param month  기준 월 (YYYY-MM)
     * @param deptId 부서 ID
     * @return 선택 부서 KPI 집계 Row
     */
    PayrollAnalyticsOrgResponse.OrgKpi selectOrgKpiByDept(@Param("month") String month,
                                                          @Param("deptId") Integer deptId);

    /**
     * 부서별 급여 현황 테이블 조회
     * (전월 대비 증감률 포함)
     *
     * @param month     기준 월 (YYYY-MM)
     * @param prevMonth 전월 (YYYY-MM)
     * @return 부서별 급여 테이블 Row 목록
     */
    List<PayrollAnalyticsOrgResponse.DeptRow> selectDeptTable(@Param("month") String month,
                                                              @Param("prevMonth") String prevMonth);

    /**
     * 부서별 급여 구성 스택 차트 조회
     *
     * @param month 기준 월 (YYYY-MM)
     * @return 부서별 스택 차트 Row 목록
     */
    List<PayrollAnalyticsOrgResponse.DeptStackRow> selectDeptStacks(@Param("month") String month);

    /**
     * 실지급 상위 TOP 10 조회
     *
     * @param month  기준 월 (YYYY-MM)
     * @param deptId 선택 부서 ID (null 이면 전체)
     * @return 실지급 상위 Row 목록
     */
    List<PayrollAnalyticsOrgResponse.PersonRow> selectNetPayTop10(@Param("month") String month,
                                                                  @Param("deptId") Integer deptId);

    /**
     * 실지급 하위 TOP 10 조회
     *
     * @param month  기준 월 (YYYY-MM)
     * @param deptId 선택 부서 ID (null 이면 전체)
     * @return 실지급 하위 Row 목록
     */
    List<PayrollAnalyticsOrgResponse.PersonRow> selectNetPayBottom10(@Param("month") String month,
                                                                     @Param("deptId") Integer deptId);

    /**
     * 공제율 기준 TOP 10 조회
     *
     * @param month  기준 월 (YYYY-MM)
     * @param deptId 선택 부서 ID (null 이면 전체)
     * @return 공제율 상위 Row 목록
     */
    List<PayrollAnalyticsOrgResponse.DeductionRateRow> selectDeductionRateTop10(@Param("month") String month,
                                                                                @Param("deptId") Integer deptId);
}