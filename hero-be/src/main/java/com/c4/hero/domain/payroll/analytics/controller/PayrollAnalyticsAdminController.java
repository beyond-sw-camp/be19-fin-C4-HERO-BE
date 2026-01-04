package com.c4.hero.domain.payroll.analytics.controller;

import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.analytics.dto.PayrollAnalyticsCompositionResponse;
import com.c4.hero.domain.payroll.analytics.dto.PayrollAnalyticsOrgResponse;
import com.c4.hero.domain.payroll.analytics.dto.PayrollAnalyticsOverviewResponse;
import com.c4.hero.domain.payroll.analytics.service.PayrollAnalyticsCompositionService;
import com.c4.hero.domain.payroll.analytics.service.PayrollAnalyticsOrgService;
import com.c4.hero.domain.payroll.analytics.service.PayrollAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <pre>
 * Controller Name : PayrollAnalyticsAdminController
 * Description     : 급여 분석(Admin) 대시보드 API 컨트롤러
 *
 * History
 *   2026/01/02 - 동근 최초 작성
 *   2026/01/03 - 동근 권한 인가 정책 추가
 * </pre>
 *
 * @author 동근
 * @version 1.1
 */
@Tag(
        name = "급여 분석(Analytics) API",
        description = "관리자(Admin) 급여 분석 대시보드용 요약/조직/구성 분석 API"
)
@PayrollAdminOnly
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/payroll/analytics")
public class PayrollAnalyticsAdminController {

    private final PayrollAnalyticsService service;
    private final PayrollAnalyticsOrgService orgService;
    private final PayrollAnalyticsCompositionService compositionService;

    /**
     * 급여 분석 요약 지표 조회
     *
     * @param month        기준 월 (YYYY-MM)
     * @param trendMonths  추이 조회 개월 수 (null 이면 기본값 사용)
     * @return 급여 분석 요약 응답 DTO
     */
    @Operation(
            summary = "급여 분석 요약 조회",
            description = """
                    관리자 급여 분석 대시보드 상단에 표시되는 요약 KPI 정보를 조회합니다.
                    """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PayrollAnalyticsOverviewResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "요청 파라미터가 올바르지 않음", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @GetMapping("/overview")
    public PayrollAnalyticsOverviewResponse overview(
            @RequestParam String month,
            @RequestParam(required = false) Integer trendMonths
    ) {
        return service.getOverview(month, trendMonths);
    }

    /**
     * 조직/부서 단위 급여 분포 조회
     *
     * @param month  기준 월 (YYYY-MM)
     * @param deptId 부서 ID (null 이면 전체 조직 기준)
     * @return 조직 급여 분석 응답 DTO
     */

    @Operation(
            summary = "조직(부서)별 급여 분석 조회",
            description = """
                    조직/부서 단위 급여 현황을 조회합니다.
                    """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PayrollAnalyticsOrgResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "요청 파라미터가 올바르지 않음", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @GetMapping("/organization")
    public PayrollAnalyticsOrgResponse organization(
            @RequestParam String month,
            @RequestParam(required = false) Integer deptId
    ) {
        return orgService.getOrganization(month, deptId);
    }

    /**
     * 급여 구성 항목 비중 분석 조회
     *
     * @param month        기준 월 (YYYY-MM)
     * @param trendMonths  추이 조회 개월 수 (null 이면 기본값 사용)
     * @return 급여 구성 분석 응답 DTO
     */
    @Operation(
            summary = "급여 구성 항목 비중 분석 조회",
            description = """
                    급여 구성 항목별 비중 및 추이를 분석합니다.
                    """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PayrollAnalyticsCompositionResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "요청 파라미터가 올바르지 않음", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @GetMapping("/composition")
    public PayrollAnalyticsCompositionResponse composition(
            @RequestParam String month,
            @RequestParam(required = false) Integer trendMonths
    ) {
        return compositionService.getComposition(month, trendMonths);
    }
}
