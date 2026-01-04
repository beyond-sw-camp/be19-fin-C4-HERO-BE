package com.c4.hero.domain.payroll.report.controller;


import com.c4.hero.domain.auth.security.CustomUserDetails;
import com.c4.hero.domain.auth.security.LoginOnly;
import com.c4.hero.domain.payroll.report.dto.MyPaySummaryDTO;
import com.c4.hero.domain.payroll.report.dto.PayHistoryResponseDTO;
import com.c4.hero.domain.payroll.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 사원 급여 리포트 조회 컨트롤러
 *
 * <pre>
 * Class Name: ReportController
 * Description: 사원의 급여 요약 및 급여 이력 조회 API를 제공한다.
 *              - 월별 급여 요약 조회
 *              - 최근 12개월 급여 이력 및 통계 조회
 *
 * History
 * 2025/12/08 - 동근 최초 작성
 * 2025/12/14 - 동근 payslip 조회 API 분리 (명세서 관련 API 제거)
 * 2025/12/18 - 동근 swagger 문서화 주석 추가
 * 2026/01/03 - 동근 권한 인가 정책 추가
 * </pre>
 *
 * @author 동근
 * @version 1.3
 */
@LoginOnly
@RestController
@RequestMapping("/api/me/payroll")
@RequiredArgsConstructor
@Tag(name = "내 급여 리포트 API", description = "사원 본인 급여 요약 및 최근 급여 이력/통계 조회 API")
public class ReportController {
    private final ReportService service;

    /**
     * 내 급여 요약 조회
     * @param month 조회 할 급여 월(YYYY-MM)
     * @return 내 급여 요약 정보
     */
    @Operation(
            summary = "내 급여 요약 조회",
            description = "사원 본인의 급여 요약을 조회합니다. month를 지정하지 않으면 기본 월(현재 월 또는 최신 데이터)을 기준으로 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = MyPaySummaryDTO.class))),
            @ApiResponse(responseCode = "400", description = "요청값이 올바르지 않음(월 형식 오류 등)", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 필요", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @GetMapping
    public ResponseEntity<MyPaySummaryDTO> getMyPayroll(
            @RequestParam(required = false) String month,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Integer employeeId = user.getEmployeeId();
        return ResponseEntity.ok(service.getMyPayroll(employeeId, month));
    }


    /**
     * 급여 이력 (최근 12개월)
     * @return 급여 이력 및 통계 정보
     */
    @Operation(
            summary = "내 급여 이력 조회(최근 12개월)",
            description = "사원 본인의 최근 12개월 급여 이력 및 통계 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PayHistoryResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "인증 필요", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @GetMapping("/history")
    public ResponseEntity<PayHistoryResponseDTO> getHistory(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Integer employeeId = user.getEmployeeId();
        return ResponseEntity.ok(service.getPayHistory(employeeId));
    }
}
