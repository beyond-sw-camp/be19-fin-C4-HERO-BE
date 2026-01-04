package com.c4.hero.domain.payroll.payment.controller;

import com.c4.hero.common.response.CustomResponse;
import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.payment.dto.PayrollPaymentDetailResponseDTO;
import com.c4.hero.domain.payroll.payment.dto.PayrollPaymentSearchRequestDTO;
import com.c4.hero.domain.payroll.payment.dto.PayrollPaymentSearchRowResponseDTO;
import com.c4.hero.domain.payroll.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <pre>
 * Class Name : PaymentController
 * Description : 관리자(Admin)용 급여 지급/조회 API 컨트롤러
 *
 * History
 *  2025/12/28 - 동근 최초 작성
 *  2026/01/03 - 동근 권한 인가 정책 추가
 * </pre>
 *
 * @author 동근
 * @version 1.1
 */
@Tag(name = "급여 조회 (Admin)", description = "관리자용 급여 조회 및 상세 조회 API")
@PayrollAdminOnly
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/payroll/payments")
public class PaymentController {

    private final PaymentService service;

    /**
     * 급여조회 목록 (관리자)
     *
     * @param salaryMonth 기준 급여월 (yyyy-MM)
     * @param departmentId 부서 ID (선택)
     * @param jobTitleId 직책 ID (선택)
     * @param keyword 사번 또는 사원명 검색 키워드 (선택)
     * @param page 페이지 번호 (1-based)
     * @param size 페이지당 조회 건수
     * @return 급여 조회 목록 (페이징)
     */
    @Operation(
            summary = "급여 조회 목록",
            description = "급여월 기준으로 사원 급여 목록을 조회합니다. 부서/직책/키워드 조건 검색을 지원합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "급여 조회 성공",
                    content = @Content(schema = @Schema(
                            implementation = PageResponse.class
                    ))
            ),
            @ApiResponse(responseCode = "400", description = "요청 파라미터 오류"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/search")
    public CustomResponse<com.c4.hero.common.response.PageResponse<PayrollPaymentSearchRowResponseDTO>> search(
            @RequestParam String salaryMonth,
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) Integer jobTitleId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        PayrollPaymentSearchRequestDTO req = new PayrollPaymentSearchRequestDTO(
                salaryMonth, departmentId, jobTitleId, keyword
        );

        var data = service.search(req, page, size);
        return CustomResponse.success(data);
    }

    /**
     * 급여조회 상세 (관리자)
     *
     * @param payrollId 급여 ID
     * @return 급여 상세 정보
     */
    @Operation(
            summary = "급여 상세 조회",
            description = "선택한 급여 ID에 대한 상세 급여 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "급여 상세 조회 성공",
                    content = @Content(schema = @Schema(
                            implementation = PayrollPaymentDetailResponseDTO.class
                    ))
            ),
            @ApiResponse(responseCode = "404", description = "급여 정보 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{payrollId}")
    public CustomResponse<PayrollPaymentDetailResponseDTO> detail(@PathVariable Integer payrollId) {
        PayrollPaymentDetailResponseDTO data = service.getDetail(payrollId);

        return CustomResponse.success(data);
    }
}
