package com.c4.hero.domain.payroll.analytics.service;

import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.analytics.dto.PayrollAnalyticsCompositionResponse;
import com.c4.hero.domain.payroll.analytics.dto.PayrollAnalyticsCompositionRows;
import com.c4.hero.domain.payroll.analytics.mapper.PayrollAnalyticsCompositionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Service Name : PayrollAnalyticsCompositionServiceImpl
 * Description  : 급여 구성(Composition) 분석 서비스 구현체
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
public class PayrollAnalyticsCompositionServiceImpl implements PayrollAnalyticsCompositionService {

    private final PayrollAnalyticsCompositionMapper mapper;

    /**
     * 급여 구성(Composition) 분석 데이터 조회
     * @param month        기준 월 (YYYY-MM)
     * @param trendMonths  추이 조회 개월 수 (null 이면 기본 범위 적용)
     * @return 급여 구성 분석 응답 DTO
     */
    @Override
    public PayrollAnalyticsCompositionResponse getComposition(String month, Integer trendMonths) {
        int months = (trendMonths == null || trendMonths <= 0) ? 6 : trendMonths;

        var deptAgg = mapper.selectDeptShareAgg(month);
        int deptTotal = deptAgg.stream().mapToInt(r -> nz(r.laborCostTotal())).sum();

        List<PayrollAnalyticsCompositionResponse.DeptShareRow> deptShare = new ArrayList<>();
        for (var r : deptAgg) {
            double pct = (deptTotal <= 0) ? 0.0 : (nz(r.laborCostTotal()) * 100.0 / deptTotal);
            deptShare.add(new PayrollAnalyticsCompositionResponse.DeptShareRow(
                    r.departmentId(),
                    r.departmentName(),
                    nz(r.laborCostTotal()),
                    pct
            ));
        }

        var allowAgg = mapper.selectItemAgg(month, "ALLOWANCE");
        var dedAgg = mapper.selectItemAgg(month, "DEDUCTION");

        int allowTotal = allowAgg.stream().mapToInt(r -> nz(r.amountTotal())).sum();
        int dedTotal = dedAgg.stream().mapToInt(r -> nz(r.amountTotal())).sum();

        var allowList = toItemRows(allowAgg, allowTotal);
        var dedList = toItemRows(dedAgg, dedTotal);

        var items = new PayrollAnalyticsCompositionResponse.ItemSummary(allowList, dedList);

        var burdenAgg = mapper.selectBurdenAgg(month);
        List<PayrollAnalyticsCompositionResponse.BurdenRow> burden = new ArrayList<>();
        for (var r : burdenAgg) {
            burden.add(new PayrollAnalyticsCompositionResponse.BurdenRow(
                    r.deductionId(),
                    r.deductionName(),
                    nz(r.employeeAmount()),
                    nz(r.employerAmount()),
                    nz(r.totalAmount())
            ));
        }

        YearMonth to = YearMonth.parse(month);
        YearMonth from = to.minusMonths(months - 1);
        var trendAgg = mapper.selectStackTrend(from.toString(), to.toString());

        List<PayrollAnalyticsCompositionResponse.MonthStackRow> stackTrend = new ArrayList<>();
        for (var r : trendAgg) {
            stackTrend.add(new PayrollAnalyticsCompositionResponse.MonthStackRow(
                    r.month(),
                    nz(r.baseTotal()),
                    nz(r.allowanceTotal()),
                    nz(r.overtimeTotal()),
                    nz(r.bonusTotal()),
                    nz(r.deductionTotal()),
                    nz(r.laborCostTotal())
            ));
        }

        return new PayrollAnalyticsCompositionResponse(month, deptShare, items, burden, stackTrend);
    }

    /**
     * 항목 합계 집계 Row를 화면용 ItemRow로 변환하고 비중(%)을 계산한다.
     *
     * @param agg   항목별 합계 집계 Row 목록
     * @param total 항목 유형 전체 합계
     * @return 화면용 ItemRow 목록
     */
    private List<PayrollAnalyticsCompositionResponse.ItemRow> toItemRows(
            List<PayrollAnalyticsCompositionRows.ItemAggRow> agg, int total
    ) {
        List<PayrollAnalyticsCompositionResponse.ItemRow> out = new ArrayList<>();
        for (var r : agg) {
            double pct = (total <= 0) ? 0.0 : (nz(r.amountTotal()) * 100.0 / total);
            out.add(new PayrollAnalyticsCompositionResponse.ItemRow(
                    r.itemCode(),
                    r.itemName(),
                    nz(r.amountTotal()),
                    pct
            ));
        }
        return out;
    }

    private int nz(Integer v) { return v == null ? 0 : v; }
}
