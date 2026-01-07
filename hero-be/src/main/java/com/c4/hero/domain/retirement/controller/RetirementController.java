package com.c4.hero.domain.retirement.controller;

import com.c4.hero.common.response.CustomResponse;
import com.c4.hero.domain.retirement.dto.*;
import com.c4.hero.domain.retirement.service.RetirementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <pre>
 * Class Name: RetirementController
 * Description: 퇴직 관리 관련 API 요청을 처리하는 컨트롤러 클래스
 *
 * History
 * 2025/12/30 (승건) 최초 작성
 * 2026/01/07 (승건) 스웨거 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@RestController
@RequestMapping("/api/retirement")
@RequiredArgsConstructor
@Tag(name = "퇴직 API", description = "퇴직 사유, 현황, 통계 및 강제 퇴직 처리 API")
public class RetirementController {

    private final RetirementService retirementService;

    /**
     * 퇴사 사유 목록을 조회합니다.
     *
     * @return 퇴사 사유 목록
     */
    @GetMapping("/reasons")
    @Operation(summary = "퇴사 사유 목록 조회", description = "퇴사 신청 시 선택할 수 있는 퇴사 사유 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    public ResponseEntity<CustomResponse<List<ExitReasonDTO>>> getExitReasons() {
        return ResponseEntity.ok(CustomResponse.success(retirementService.getExitReasons()));
    }
    
    /**
     * 퇴직 현황 요약 정보를 조회합니다.
     *
     * @return 퇴직 현황 요약 정보 (잔존률, 정착률, 이직률 등)
     */
    @GetMapping("/summary")
    @Operation(summary = "퇴직 현황 요약 조회", description = "잔존률, 정착률, 종합 이직률, 신입 이직률 등 주요 지표를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    public ResponseEntity<CustomResponse<RetirementSummaryDTO>> getRetirementSummary() {
        return ResponseEntity.ok(CustomResponse.success(retirementService.getRetirementSummary()));
    }

    /**
     * 퇴사 사유별 통계 데이터를 조회합니다.
     *
     * @return 사유별 퇴직 통계 리스트
     */
    @GetMapping("/stats/reason")
    @Operation(summary = "사유별 퇴직 통계 조회", description = "퇴사 사유별 통계 데이터를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    public ResponseEntity<CustomResponse<ExitReasonStatsResponseDTO>> getExitReasonStats() {
        return ResponseEntity.ok(CustomResponse.success(retirementService.getExitReasonStats()));
    }

    /**
     * 근속 연수별 인력 분포 통계 데이터를 조회합니다.
     *
     * @return 근속 연수별 인력 분포 리스트
     */
    @GetMapping("/stats/tenure")
    @Operation(summary = "근속 연수별 인력 분포 조회", description = "현재 재직자들의 근속 연수별 인원 비율을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    public ResponseEntity<CustomResponse<List<TenureDistributionDTO>>> getTenureDistributionStats() {
        return ResponseEntity.ok(CustomResponse.success(retirementService.getTenureDistributionStats()));
    }

    /**
     * 분기별 신입 사원의 정착률 및 이직률 통계 데이터를 조회합니다.
     *
     * @return 신입 정착률 및 이직률 리스트
     */
    @GetMapping("/stats/new-hire")
    @Operation(summary = "신입 정착률 및 이직률 조회", description = "분기별 신입 사원의 정착률과 이직률을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    public ResponseEntity<CustomResponse<List<NewHireStatDTO>>> getNewHireStats() {
        return ResponseEntity.ok(CustomResponse.success(retirementService.getNewHireStats()));
    }

    /**
     * 부서별 이직률 통계 데이터를 조회합니다.
     *
     * @return 부서별 이직률 리스트
     */
    @GetMapping("/stats/department")
    @Operation(summary = "부서별 이직률 조회", description = "부서별 현재 인원, 퇴사 인원, 이직률을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    public ResponseEntity<CustomResponse<List<DepartmentTurnoverDTO>>> getDepartmentTurnoverStats() {
        return ResponseEntity.ok(CustomResponse.success(retirementService.getDepartmentTurnoverStats()));
    }

    /**
     * 관리자가 직원을 강제로 퇴직 처리합니다. (해고, 사망 등)
     *
     * @param employeeId 대상 직원 ID
     * @param request 퇴직 정보
     * @return 성공 응답
     */
    @PostMapping("/terminate/{employeeId}")
//    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "관리자 강제 퇴직 처리", description = "관리자가 직원을 즉시 퇴직 처리합니다. (해고, 사망 등)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "처리 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    public ResponseEntity<CustomResponse<Void>> forceTerminateEmployee(
            @PathVariable Integer employeeId,
            @Valid @RequestBody ForceRetirementRequestDTO request) {
        retirementService.forceTerminateEmployee(employeeId, request);
        return ResponseEntity.ok(CustomResponse.success());
    }
}
