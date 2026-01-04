package com.c4.hero.domain.payroll.payslip.controller;

import com.c4.hero.domain.auth.security.LoginOnly;
import com.c4.hero.domain.payroll.payslip.dto.PayslipDetailDTO;
import com.c4.hero.domain.payroll.payslip.service.PayslipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.c4.hero.domain.auth.security.CustomUserDetails;

/**
 * 사원 급여 명세서 조회 컨트롤러
 *
 * <pre>
 * Class Name: PayslipController
 * Description: 사원의 급여명세서 상세 조회 API를 제공한다.
 *              - 급여월 기준 명세서 상세 조회
 *              - PDF 다운로드를 위한 URL 제공
 *
 * History
 * 2025/12/14 - 동근 report 도메인에서 payslip 도메인으로 분리
 * 2025/12/18 - 동근 swagger 문서화 주석 추가
 * 2026/01/03 - 동근 권한 인가 정책 추가
 * </pre>
 *
 * @author 동근
 * @version 1.2
 */

@LoginOnly
@RestController
@RequestMapping("/api/me/payroll")
@RequiredArgsConstructor
@Tag(name = "급여 명세서 API", description = "사원 본인 급여명세서 상세 조회 API")
public class PayslipController {
    private final PayslipService payslipService;

    /**
     * 급여명세서 모달
     * @param month 조회 할 급여 월(YYYY-MM)
     * @return 명세서 상세 정보
     */
    @Operation(
            summary = "내 급여명세서 상세 조회",
            description = "사원 본인의 급여명세서를 급여월(YYYY-MM) 기준으로 상세 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PayslipDetailDTO.class))),
            @ApiResponse(responseCode = "400", description = "요청값이 올바르지 않음(월 형식 오류 등)", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 필요", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "해당 월 급여명세서를 찾을 수 없음", content = @Content)
    })
    @GetMapping("/payslip")
    public ResponseEntity<PayslipDetailDTO> getPayslip(
            @RequestParam String month,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Integer employeeId = user.getEmployeeId();
        return ResponseEntity.ok(payslipService.getPayslipDetail(employeeId, month));
    }
}
