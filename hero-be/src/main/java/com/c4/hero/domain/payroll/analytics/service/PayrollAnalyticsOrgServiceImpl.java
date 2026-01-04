package com.c4.hero.domain.payroll.analytics.service;

import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.analytics.dto.PayrollAnalyticsOrgResponse;
import com.c4.hero.domain.payroll.analytics.mapper.PayrollAnalyticsOrgMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

/**
 * <pre>
 * Service Name : PayrollAnalyticsOrgServiceImpl
 * Description  : 급여 조직 분석(Organization) 서비스 구현체
 *
 * History
 *   2026/01/02 - 동근 최초 작성
 *   2026/01/03 - 동근 권한 인가 정책 추가
 * </pre>
 *
 * @author 동근
 * @version 1.1
 */
@PayrollAdminOnly
@Service
@RequiredArgsConstructor
public class PayrollAnalyticsOrgServiceImpl implements PayrollAnalyticsOrgService {

    private final PayrollAnalyticsOrgMapper mapper;

    /**
     * 급여 조직 분석 데이터 조회
     *
     * @param month  기준 월 (YYYY-MM)
     * @param deptId 선택 부서 ID (null 이면 전체 조직 기준)
     * @return 급여 조직 분석 응답 DTO
     */
    @Override
    public PayrollAnalyticsOrgResponse getOrganization(String month, Integer deptId) {
        String prevMonth = YearMonth.parse(month).minusMonths(1).toString();

        Integer totalLaborCost = nz(mapper.selectTotalLaborCost(month));

        PayrollAnalyticsOrgResponse.OrgKpi rawKpi =
                (deptId == null)
                        ? mapper.selectOrgKpiAll(month)
                        : mapper.selectOrgKpiByDept(month, deptId);

        Double share = null;
        if (totalLaborCost > 0 && rawKpi != null && rawKpi.laborCostTotal() != null) {
            share = (double) rawKpi.laborCostTotal() / totalLaborCost * 100.0;
        }

        var kpi = new PayrollAnalyticsOrgResponse.OrgKpi(
                nz(rawKpi.headcount()),
                nz(rawKpi.laborCostTotal()),
                nz(rawKpi.avgNetPay()),
                share
        );

        var departments = mapper.selectDeptTable(month, prevMonth);
        var stacks = mapper.selectDeptStacks(month);

        var top10 = mapper.selectNetPayTop10(month, deptId);
        var bottom10 = mapper.selectNetPayBottom10(month, deptId);
        var dedTop10 = mapper.selectDeductionRateTop10(month, deptId);

        return new PayrollAnalyticsOrgResponse(
                month,
                deptId,
                kpi,
                departments,
                stacks,
                top10,
                bottom10,
                dedTop10
        );
    }

    /**
     * null 안전 정수 변환
     *
     * @param v 입력 값
     * @return null 이면 0, 아니면 원 값
     */
    private Integer nz(Integer v) { return v == null ? 0 : v; }
}
