package com.c4.hero.domain.payroll.analytics.dto;

import java.util.List;

/**
 * <pre>
 * DTO Name : PayrollAnalyticsOrgResponse
 * Description : 급여 조직 분석(Organization) 응답 DTO
 *
 * History
 *   2026/01/02 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record PayrollAnalyticsOrgResponse(
        String month,
        Integer deptId,
        OrgKpi kpi,
        List<DeptRow> departments,
        List<DeptStackRow> deptStacks,
        List<PersonRow> netPayTop10,
        List<PersonRow> netPayBottom10,
        List<DeductionRateRow> deductionRateTop10
) {

    /**
     * 조직 단위 KPI 요약
     */
    public record OrgKpi(
            Integer headcount,
            Integer laborCostTotal,
            Integer avgNetPay,
            Double laborCostSharePct
    ) {}

    /**
     * 부서별 급여 요약 테이블 Row
     */
    public record DeptRow(
            Integer departmentId,
            String departmentName,
            Integer headcount,
            Integer grossTotal,
            Integer deductionTotal,
            Integer netTotal,
            Double momChangeRate
    ) {}

    /**
     * 부서별 급여 구성 스택 차트 Row
     */
    public record DeptStackRow(
            Integer departmentId,
            String departmentName,
            Integer baseTotal,
            Integer allowanceTotal,
            Integer overtimeTotal,
            Integer bonusTotal,
            Integer deductionTotal,
            Integer laborCostTotal
    ) {}

    /**
     * 개인 실지급 기준 랭킹 Row
     */
    public record PersonRow(
            Integer employeeId,
            String employeeName,
            String departmentName,
            Integer netPay
    ) {}

    /**
     * 개인 공제율 기준 랭킹 Row
     */
    public record DeductionRateRow(
            Integer employeeId,
            String employeeName,
            String departmentName,
            Double deductionRatePct,
            Integer grossPay,
            Integer deductionTotal
    ) {}
}
