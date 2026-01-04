package com.c4.hero.domain.payroll.allowance.controller;

import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.allowance.dto.AllowanceCreateRequestDTO;
import com.c4.hero.domain.payroll.allowance.dto.AllowanceResponseDTO;
import com.c4.hero.domain.payroll.allowance.entity.Allowance;
import com.c4.hero.domain.payroll.allowance.service.AllowanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <pre>
 * Controller Name : AllowanceController
 * Description     : 수당(Allowance) 마스터 관리 API 컨트롤러
 *
 * History
 *   2025/12/22 - 동근 최초 작성
 *   2026/01/03 - 동근 권한 인가 정책 추가
 * </pre>
 *
 * @author 동근
 * @version 1.1
 */

@PayrollAdminOnly
@RestController
@RequestMapping("/api/admin/payroll/allowances")
@RequiredArgsConstructor
@Tag(name = "급여 수당 마스터 API", description = "관리자 수당(Allowance) 마스터 관리(조회/등록/수정/활성/비활성) API")
public class AllowanceController {

    private final AllowanceService allowanceService;

    /**
     * 수당 목록 조회
     *
     * @param activeYn Y/N 여부 (없으면 전체)
     * @return 수당 목록
     */
    @Operation(
            summary = "수당 목록 조회",
            description = "수당(Allowance) 마스터 목록을 조회합니다. activeYn이 없으면 전체, 'Y' 또는 'N'으로 필터링 가능합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = AllowanceResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<AllowanceResponseDTO>> getAllowances(
            @RequestParam(required = false) String activeYn
    ) {
        List<AllowanceResponseDTO> result = allowanceService.getAll(activeYn)
                .stream()
                .map(AllowanceResponseDTO::from)
                .toList();
        return ResponseEntity.ok(result);
    }

    /**
     * 수당 생성
     *
     * @param request 수당 생성 요청 DTO
     * @return 200 OK
     */
    @Operation(
            summary = "수당 등록",
            description = "새 수당(Allowance) 마스터를 등록합니다. allowanceId는 중복될 수 없습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "요청값이 올바르지 않음(필수값 누락 등)", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 수당 코드(allowanceId 중복)", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Void> createAllowance(
            @RequestBody AllowanceCreateRequestDTO request
    ) {
        Allowance allowance = Allowance.builder()
                .allowanceId(request.allowanceId())
                .allowanceName(request.allowanceName())
                .description(request.description())
                .defaultAmount(request.defaultAmount())
                .taxableYn(request.taxableYn())
                .activeYn("Y")
                .build();
        allowanceService.create(allowance);
        return ResponseEntity.ok().build();
    }

    /**
     * 수당 수정
     *
     * @param allowanceId 수정할 수당 코드
     * @param request     수정 요청 DTO
     * @return 200 OK
     */
    @Operation(
            summary = "수당 수정",
            description = "기존 수당(Allowance) 마스터를 수정합니다. 식별자는 path의 allowanceId를 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "요청값이 올바르지 않음", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "대상 수당을 찾을 수 없음", content = @Content)
    })
    @PutMapping("/{allowanceId}")
    public ResponseEntity<Void> updateAllowance(
            @PathVariable String allowanceId,
            @RequestBody AllowanceCreateRequestDTO request
    ) {
        allowanceService.update(
                allowanceId,
                request.allowanceName(),
                request.description(),
                request.defaultAmount(),
                request.taxableYn()
        );
        return ResponseEntity.ok().build();
    }

    /**
     * 수당 비활성화
     *
     * @param allowanceId 수당 코드
     * @return 200 OK
     */
    @Operation(
            summary = "수당 비활성화",
            description = "수당(Allowance)을 비활성 상태(activeYn = 'N')로 변경합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "대상 수당을 찾을 수 없음", content = @Content)
    })
    @PatchMapping("/{allowanceId}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable String allowanceId) {
        allowanceService.deactivate(allowanceId);
        return ResponseEntity.ok().build();
    }

    /**
     * 수당 활성화
     *
     * @param allowanceId 수당 코드
     * @return 200 OK
     */
    @Operation(
            summary = "수당 활성화",
            description = "수당(Allowance)을 다시 활성 상태(activeYn = 'Y')로 변경합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "대상 수당을 찾을 수 없음", content = @Content)
    })
    @PatchMapping("/{allowanceId}/activate")
    public ResponseEntity<Void> activate(@PathVariable String allowanceId) {
        allowanceService.activate(allowanceId);
        return ResponseEntity.ok().build();
    }
}
