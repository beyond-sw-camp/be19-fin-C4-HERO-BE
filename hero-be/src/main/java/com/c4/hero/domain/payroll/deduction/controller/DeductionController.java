package com.c4.hero.domain.payroll.deduction.controller;

import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.deduction.dto.DeductionCreateRequestDTO;
import com.c4.hero.domain.payroll.deduction.dto.DeductionResponseDTO;
import com.c4.hero.domain.payroll.deduction.entity.Deduction;
import com.c4.hero.domain.payroll.deduction.service.DeductionService;
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
 * Controller Name : DeductionController
 * Description     : 공제(Deduction) 마스터 관리 API
 *
 * History
 *   2025/12/22 - 동근 최초 작성
 *   2026/01/03 - 동근 권한 인가 정책 추가
 * </pre>
 *
 * @version 1.1
 * @author 동근
 */
@PayrollAdminOnly
@RestController
@RequestMapping("/api/admin/payroll/deductions")
@RequiredArgsConstructor
@Tag(name = "급여 공제 마스터 API", description = "관리자 공제(Deduction) 마스터 관리(조회/등록/수정/활성/비활성) API")
public class DeductionController {

    private final DeductionService deductionService;

    /**
     * 공제 목록 조회
     *
     * @param activeYn 필터(Y/N), 없으면 전체 조회
     * @return 공제 목록 (DTO 변환 후 반환)
     */
    @Operation(
            summary = "공제 목록 조회",
            description = "공제(Deduction) 마스터 목록을 조회합니다. activeYn이 없으면 전체, 'Y' 또는 'N'으로 필터링 가능합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = DeductionResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<DeductionResponseDTO>> getDeductions(
            @RequestParam(required = false) String activeYn
    ) {
        List<DeductionResponseDTO> result = deductionService.getAll(activeYn)
                .stream()
                .map(DeductionResponseDTO::from)
                .toList();
        return ResponseEntity.ok(result);
    }

    /**
     * 공제 항목 생성
     *
     * @param request 공제 생성 요청 DTO
     */
    @Operation(
            summary = "공제 항목 등록",
            description = "새 공제(Deduction) 마스터를 등록합니다. deductionId는 중복될 수 없습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "요청값이 올바르지 않음(필수값 누락 등)", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 공제 코드(deductionId 중복)", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Void> createDeduction(
            @RequestBody DeductionCreateRequestDTO request
    ) {
        Deduction deduction = Deduction.builder()
                .deductionId(request.deductionId())
                .deductionName(request.deductionName())
                .description(request.description())
                .deductionType(request.deductionType())
                .calculationType(request.calculationType())
                .rate(request.rate())
                .fixedAmount(request.fixedAmount())
                .activeYn("Y") // 신규로 등록할땐 디폴트로 활성 상태
                .build();

        deductionService.create(deduction);
        return ResponseEntity.ok().build();
    }

    /**
     * 공제 항목 수정
     *
     * @param deductionId 수정 대상 공제 코드
     * @param request     수정 요청 DTO
     */
    @Operation(
            summary = "공제 항목 수정",
            description = "기존 공제(Deduction) 마스터를 수정합니다. 식별자는 path의 deductionId를 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "요청값이 올바르지 않음", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "대상 공제를 찾을 수 없음", content = @Content)
    })
    @PutMapping("/{deductionId}")
    public ResponseEntity<Void> updateDeduction(
            @PathVariable String deductionId,
            @RequestBody DeductionCreateRequestDTO request
    ) {
        deductionService.update(
                deductionId,
                request.deductionName(),
                request.description(),
                request.deductionType(),
                request.calculationType(),
                request.rate(),
                request.fixedAmount()
        );
        return ResponseEntity.ok().build();
    }

    /**
     * 공제 항목 비활성화 (activeYn = 'N')
     */
    @Operation(
            summary = "공제 항목 비활성화",
            description = "공제(Deduction) 항목을 비활성 상태(activeYn = 'N')로 변경합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "대상 공제를 찾을 수 없음", content = @Content)
    })
    @PatchMapping("/{deductionId}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable String deductionId) {
        deductionService.deactivate(deductionId);
        return ResponseEntity.ok().build();
    }

    /**
     * 공제 항목 다시 활성화 (activeYn = 'Y')
     */
    @Operation(
            summary = "공제 항목 활성화",
            description = "공제(Deduction) 항목을 다시 활성 상태(activeYn = 'Y')로 변경합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "대상 공제를 찾을 수 없음", content = @Content)
    })
    @PatchMapping("/{deductionId}/activate")
    public ResponseEntity<Void> activate(@PathVariable String deductionId) {
        deductionService.activate(deductionId);
        return ResponseEntity.ok().build();
    }
}
