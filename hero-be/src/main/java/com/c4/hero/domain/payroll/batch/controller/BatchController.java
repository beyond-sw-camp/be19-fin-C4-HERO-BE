package com.c4.hero.domain.payroll.batch.controller;


import com.c4.hero.common.response.CustomResponse;
import com.c4.hero.domain.auth.security.CustomUserDetails;
import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.batch.dto.PayrollBatchDetailResponseDTO;
import com.c4.hero.domain.payroll.batch.dto.PayrollBatchListResponseDTO;
import com.c4.hero.domain.payroll.batch.dto.PayrollBatchTargetEmployeeResponseDTO;
import com.c4.hero.domain.payroll.batch.dto.PayrollEmployeeResultResponseDTO;
import com.c4.hero.domain.payroll.batch.mapper.PayrollBatchQueryMapper;
import com.c4.hero.domain.payroll.batch.service.PayrollBatchService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <pre>
 * Controller Name : BatchController
 * Description     : 관리자(Admin) 급여 배치 관리 API 컨트롤러
 *
 * History
 *  2025/12/15 - 동근 최초 작성
 *  2025/12/18 - 동근 지급(pay) API 추가
 *             - swagger 문서화 주석 추가
 *  2026/01/03 - 동근 권한 인가 정책 추가
 * </pre>
 *
 *  @author 동근
 *  @version 1.2
 */
@PayrollAdminOnly
@RequestMapping("/api/admin/payroll/batches")
@RequiredArgsConstructor
@RestController
@Tag(name = "급여 배치 API", description = "관리자 급여 배치(생성/계산/확정/지급/조회) 관련 API")
public class BatchController {

    private final PayrollBatchService batchService;
    private final PayrollBatchQueryMapper batchQueryMapper;

    /**
     * 급여 배치 생성
     *
     * @param month 급여월 (YYYY-MM)
     * @return 생성된 급여 배치 ID
     */
    @Operation(summary = "급여 배치 생성", description = "지정한 급여월(YYYY-MM) 기준으로 급여 배치를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "400", description = "요청값이 올바르지 않음", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 배치(중복 생성)", content = @Content)
    })
    @PostMapping
public ResponseEntity<Integer> create(
        @RequestParam String month,
        @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(batchService.createBatch(month, user.getEmployeeId()));
    }

    /**
     * 급여 배치 계산 실행
     *
     * @param batchId     급여 배치 ID
     * @param employeeIds 계산 대상 사원 ID 목록 (null일 경우 배치 전체 대상)
     */
    @Operation(summary = "급여 배치 계산 실행",
            description = "배치 단위 급여 계산을 실행합니다. employeeIds가 없으면 배치 전체 대상 계산을 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "400", description = "요청값이 올바르지 않음", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "배치를 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "409", description = "상태가 맞지 않아 계산 불가", content = @Content)
    })
    @PostMapping("/{batchId}/calculate")
    public ResponseEntity<Void> calculate(
            @PathVariable Integer batchId,
            @RequestBody(required = false) List<Integer> employeeIds
    ) {
        batchService.calculate(batchId, employeeIds);
        return ResponseEntity.ok().build();
    }

    /**
     * 급여 배치 확정
     *
     * @param batchId 급여 배치 ID
     */
    @Operation(summary = "급여 배치 확정", description = "급여 배치를 확정(CONFIRMED) 상태로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "배치를 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "409", description = "상태가 맞지 않아 확정 불가", content = @Content)
    })
    @PostMapping("/{batchId}/confirm")
public ResponseEntity<Void> confirm(
        @PathVariable Integer batchId,
        @AuthenticationPrincipal CustomUserDetails user) {
            batchService.confirm(batchId, user.getEmployeeId());
        return ResponseEntity.ok().build();
    }

    /**
     * 급여 배치 목록 조회
     *
     * @param month  급여월 (YYYY-MM), 선택 조건
     * @param status 배치 상태 (READY / CALCULATED / CONFIRMED / PAID), 선택 조건
     * @return 급여 배치 목록 리스트
     */
    @Operation(summary = "급여 배치 목록 조회", description = "급여 배치 목록을 조회합니다. month/status는 선택 조건입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PayrollBatchListResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @GetMapping
    public List<PayrollBatchListResponseDTO> list(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String status
    ) {
        return batchQueryMapper.selectBatchList(month, status);
    }

    /**
     * 급여 배치 상세 조회
     *
     * @param batchId 급여 배치 ID
     * @return 급여 배치 상세 정보 및 처리 현황
     */
    @Operation(summary = "급여 배치 상세 조회", description = "특정 급여 배치의 상세 정보 및 처리 현황을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PayrollBatchDetailResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "배치를 찾을 수 없음", content = @Content)
    })
    @GetMapping("/{batchId}")
    public PayrollBatchDetailResponseDTO detail(@PathVariable Integer batchId) {
        return batchQueryMapper.selectBatchDetail(batchId);
    }

    /**
     * 배치별 사원 급여 계산 결과 조회
     *
     * @param batchId 급여 배치 ID
     * @return 사원별 급여 계산 결과 목록
     */
    @Operation(summary = "배치별 사원 급여 결과 조회", description = "특정 배치에 포함된 사원들의 급여 계산 결과 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PayrollEmployeeResultResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "배치를 찾을 수 없음", content = @Content)
    })
    @GetMapping("/{batchId}/employees")
    public List<PayrollEmployeeResultResponseDTO> employees(@PathVariable Integer batchId) {
        return batchQueryMapper.selectPayrollEmployees(batchId);
    }

    /**
     * 급여 배치 대상 사원 목록 조회
     *
     * @return 급여 배치 대상 사원 목록
     */
    @Operation(summary = "급여 배치 대상 사원 조회", description = "급여 배치 계산 대상 사원 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PayrollBatchTargetEmployeeResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @GetMapping("/targets")
    public List<PayrollBatchTargetEmployeeResponseDTO> targets() {
        return batchQueryMapper.selectBatchTargetEmployees();
    }

    /**
     * 급여 배치 지급 처리
     *
     * @param batchId 급여 배치 ID
     * @return 공통 성공 응답
     */
    @Operation(summary = "급여 배치 지급 처리", description = "확정된 배치에 대해 지급(PAID) 처리를 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "처리 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "배치를 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "409", description = "상태가 맞지 않아 지급 불가", content = @Content)
    })
    @PostMapping("/{batchId}/pay")
public ResponseEntity<CustomResponse<Void>> pay(
        @PathVariable Integer batchId,
        @AuthenticationPrincipal CustomUserDetails user) {
            batchService.pay(batchId, user.getEmployeeId());
        return ResponseEntity.ok(CustomResponse.success());
    }
}
