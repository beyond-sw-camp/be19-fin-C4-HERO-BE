package com.c4.hero.domain.payroll.policy.controller;

import com.c4.hero.common.response.CustomResponse;
import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.allowance.dto.AllowanceResponseDTO;
import com.c4.hero.domain.payroll.allowance.service.AllowanceService;
import com.c4.hero.domain.payroll.deduction.dto.DeductionResponseDTO;
import com.c4.hero.domain.payroll.deduction.service.DeductionService;
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

import java.util.List;
/**
 * <pre>
 * Class Name : PayrollPolicyReferenceController
 * Description : 설정(Settings) > 급여 정책/설정 화면에서 참조할 마스터(수당/공제) 조회 전용 컨트롤러
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 *  2026/01/03 - 동근 권한 인가 정책 추가
 * </pre>
 *
 *  @author 동근
 *  @version 1.1
 */
@Tag(
        name = "급여 정책 설정 참조 (Admin)",
        description = "설정 화면에서 선택/참조할 마스터 데이터(수당/공제) 조회 API"
)
@PayrollAdminOnly
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/settings/payroll")
public class PayrollPolicyReferenceController {

    private final AllowanceService allowanceService;
    private final DeductionService deductionService;

    /**
     * 수당(Allowance) 마스터 목록 조회 (설정 화면 선택용)
     * @param activeYn 기본값 Y (활성만 조회)
     */
    @Operation(
            summary = "수당 마스터 목록 조회",
            description = "급여 정책/설정 화면에서 선택할 수당(Allowance) 마스터 목록을 조회합니다. 기본값은 활성(Y)만 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수당 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = AllowanceResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/allowances")
    public CustomResponse<List<AllowanceResponseDTO>> listAllowances(
            @RequestParam(required = false, defaultValue = "Y") String activeYn
    ) {
        return CustomResponse.success(
                allowanceService.getAll(activeYn).stream().map(AllowanceResponseDTO::from).toList()
        );
    }

    /**
     * 공제(Deduction) 마스터 목록 조회 (설정 화면 선택용)
     * @param activeYn 기본값 Y (활성만 조회)
     */
    @Operation(
            summary = "공제 마스터 목록 조회",
            description = "급여 정책/설정 화면에서 선택할 공제(Deduction) 마스터 목록을 조회합니다. 기본값은 활성(Y)만 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공제 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = DeductionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping("/deductions")
    public CustomResponse<List<DeductionResponseDTO>> listDeductions(
            @RequestParam(required = false, defaultValue = "Y") String activeYn
    ) {
        return CustomResponse.success(
                deductionService.getAll(activeYn).stream().map(DeductionResponseDTO::from).toList()
        );
    }
}