package com.c4.hero.domain.payroll.analytics.service;

import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.analytics.dto.PayrollAnalyticsOverviewResponse;
import com.c4.hero.domain.payroll.analytics.dto.PayrollAnalyticsOverviewRows;
import com.c4.hero.domain.payroll.analytics.mapper.PayrollAnalyticsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Service Name : PayrollAnalyticsServiceImpl
 * Description  : 급여 분석(Overview) 서비스 구현체
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
public class PayrollAnalyticsServiceImpl implements PayrollAnalyticsService {

    private final PayrollAnalyticsMapper mapper;

    /**
     * 급여 분석 Overview 데이터 조회
     *
     * @param month        기준 월 (YYYY-MM)
     * @param trendMonths  추이 조회 개월 수 (null 이면 기본 범위 적용)
     * @return Overview 응답 DTO
     */
    @Override
    public PayrollAnalyticsOverviewResponse getOverview(String month, Integer trendMonths) {
        int months = (trendMonths == null || trendMonths <= 0) ? 6 : trendMonths;

        PayrollAnalyticsOverviewRows.OverviewAggRow agg = mapper.selectOverviewAgg(month);
        PayrollAnalyticsOverviewRows.NetPayStatsRow net = mapper.selectNetPayStats(month);

        String prevMonth = YearMonth.parse(month).minusMonths(1).toString();
        Integer prevAvg = mapper.selectPrevMonthAvgNetPay(prevMonth);
        Double netMom = calcRate(prevAvg, net.avgNetPay());

        YearMonth to = YearMonth.parse(month);
        YearMonth from = to.minusMonths(months - 1);

        List<PayrollAnalyticsOverviewRows.TrendAggRow> rows =
                mapper.selectLaborCostTrend(from.toString(), to.toString());

        List<PayrollAnalyticsOverviewResponse.TrendPoint> trend = new ArrayList<>();
        PayrollAnalyticsOverviewRows.TrendAggRow prev = null;
        for (PayrollAnalyticsOverviewRows.TrendAggRow r : rows) {
            Double mom = (prev == null) ? null : calcRate(prev.laborCostTotal(), r.laborCostTotal());
            trend.add(new PayrollAnalyticsOverviewResponse.TrendPoint(r.month(), r.laborCostTotal(), mom));
            prev = r;
        }

        var kpi = new PayrollAnalyticsOverviewResponse.Kpi(
                nz(agg.headcount()),
                nz(agg.grossTotal()),
                nz(agg.deductionTotal()),
                nz(agg.laborCostTotal())
        );

        var netPay = new PayrollAnalyticsOverviewResponse.NetPay(
                nz(net.avgNetPay()),
                nz(net.medianNetPay()),
                netMom
        );

        return new PayrollAnalyticsOverviewResponse(month, kpi, netPay, trend);
    }

    /**
     * null 안전 정수 변환
     *
     * @param v 입력 값
     * @return null 이면 0, 아니면 원 값
     */
    private Integer nz(Integer v) {
        return v == null ? 0 : v;
    }

    /**
     * 전월 대비 증감률(%) 계산
     *
     * @param prev 전월 값
     * @param curr 현재 값
     * @return 증감률(%) (계산 불가 시 null)
     */
    private Double calcRate(Integer prev, Integer curr) {
        if (prev == null || prev <= 0 || curr == null) return null;
        return ((double) (curr - prev)) / prev * 100.0;
    }
}
