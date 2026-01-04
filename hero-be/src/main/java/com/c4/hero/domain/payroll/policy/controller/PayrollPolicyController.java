package com.c4.hero.domain.payroll.policy.controller;

import com.c4.hero.common.response.CustomResponse;
import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.common.type.ItemType;
import com.c4.hero.domain.payroll.policy.dto.request.PolicyCopyRequestDTO;
import com.c4.hero.domain.payroll.policy.dto.request.PolicyUpdateRequestDTO;
import com.c4.hero.domain.payroll.policy.dto.response.ItemPolicyResponseDTO;
import com.c4.hero.domain.payroll.policy.dto.request.ItemPolicyTargetRequestDTO;
import com.c4.hero.domain.payroll.policy.dto.request.ItemPolicyUpsertRequestDTO;
import com.c4.hero.domain.payroll.policy.dto.request.PolicyActivateRequestDTO;
import com.c4.hero.domain.payroll.policy.dto.response.PolicyConfigResponseDTO;
import com.c4.hero.domain.payroll.policy.dto.request.PolicyConfigUpsertRequestDTO;
import com.c4.hero.domain.payroll.policy.dto.request.PolicyCreateRequestDTO;
import com.c4.hero.domain.payroll.policy.dto.response.PolicyDetailResponseDTO;
import com.c4.hero.domain.payroll.policy.dto.response.PolicyResponseDTO;
import com.c4.hero.domain.payroll.policy.service.ItemPolicyService;
import com.c4.hero.domain.payroll.policy.service.PayrollPolicyCopyTxService;
import com.c4.hero.domain.payroll.policy.service.PayrollPolicyService;
import com.c4.hero.domain.payroll.policy.service.PayrollPolicyTxService;
import com.c4.hero.domain.payroll.policy.service.PolicyConfigService;
import com.c4.hero.domain.payroll.policy.service.PolicyDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
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
 * Controller Name : PayrollPolicyController
 * Description     : 관리자(Admin) 급여 정책(Policy) 관리 API 컨트롤러
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 *  2026/01/03 - 동근 권한 인가 정책 추가
 * </pre>
 *
 * @author 동근
 * @version 1.1
 */
@Tag(
        name = "급여 정책 관리 (Admin)",
        description = "관리자용 급여 정책(Policy), 설정(Config), 항목 정책(Item Policy)을 관리하는 API"
)
@PayrollAdminOnly
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/settings/payroll/policies")
public class PayrollPolicyController {

    private final PayrollPolicyService policyService;
    private final PayrollPolicyTxService policyTxService;
    private final PolicyConfigService configService;
    private final ItemPolicyService itemPolicyService;
    private final PolicyDetailService policyDetailService;
    private final PayrollPolicyCopyTxService policyCopyTxService;
    /**
     *  정책 생성
     *
     * @param req 정책 생성 요청 DTO
     * @return 생성된 정책 정보
     */
    @Operation(
            summary = "급여 정책 생성",
            description = "신규 급여 정책을 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정책 생성 성공",
                    content = @Content(schema = @Schema(implementation = PolicyResponseDTO.class)))
    })
    @PostMapping
    public CustomResponse<PolicyResponseDTO> create(@RequestBody PolicyCreateRequestDTO req) {
        return CustomResponse.success(policyService.createPolicy(req));
    }

    /**
     * 정책 목록
     *
     * @return 정책 목록
     */
    @Operation(
            summary = "급여 정책 목록 조회",
            description = "등록된 모든 급여 정책 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정책 목록 조회 성공")
    })
    @GetMapping
    public CustomResponse<List<PolicyResponseDTO>> list() {
        return CustomResponse.success(policyService.getPolicies());
    }

    /**
     * 활성 정책 조회
     *
     * @return 활성 상태의 정책
     */
    @Operation(
            summary = "활성 급여 정책 조회",
            description = "현재 활성화된 급여 정책을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "활성 정책 조회 성공")
    })
    @GetMapping("/active")
    public CustomResponse<PolicyResponseDTO> active() {
        return CustomResponse.success(policyService.getActivePolicy());
    }

    /**
     * 정책 활성화
     *
     * @param policyId 활성화할 정책 ID
     * @param req      활성화 시점/적용 월 등 활성화 파라미터
     * @return 활성화된 정책 정보
     */
    @Operation(
            summary = "급여 정책 활성화",
            description = "지정한 급여 정책을 활성화 처리합니다. (기존 활성 정책은 비활성화)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정책 활성화 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PatchMapping("/{policyId}/activate")
    public CustomResponse<PolicyResponseDTO> activate(
            @PathVariable Integer policyId,
            @RequestBody PolicyActivateRequestDTO req
    ) {
        return CustomResponse.success(policyTxService.activate(policyId, req));
    }

    /**
     * 정책별 설정(Config) 목록 조회
     *
     * @param policyId 정책 ID
     * @return 설정 목록
     */
    @Operation(
            summary = "정책 설정 목록 조회",
            description = "특정 급여 정책에 포함된 설정(Config) 목록을 조회합니다."
    )
    @GetMapping("/{policyId}/configs")
    public CustomResponse<List<PolicyConfigResponseDTO>> getConfigs(@PathVariable Integer policyId) {
        return CustomResponse.success(configService.getConfigs(policyId));
    }


    /**
     * 정책별 설정(Config) 업서트
     *
     * @param policyId 정책 ID
     * @param reqs     설정 업서트 요청 리스트
     * @return 200 OK
     */
    @Operation(
            summary = "정책 설정 업서트",
            description = "급여 정책의 설정(Config)을 일괄 등록 또는 수정합니다."
    )
    @PutMapping("/{policyId}/configs")
    public CustomResponse<Void> upsertConfigs(
            @PathVariable Integer policyId,
            @RequestBody List<PolicyConfigUpsertRequestDTO> reqs
    ) {
        configService.upsertConfigs(policyId, reqs);
        return CustomResponse.success(null);
    }

    /**
     * 정책별 항목 정책(Item Policy) 목록 조회
     *
     * @param policyId 정책 ID
     * @param type     항목 타입(예: ALLOWANCE/DEDUCTION 등)
     * @return 항목 정책 목록
     */
    @Operation(
            summary = "항목 정책 목록 조회",
            description = "급여 정책에 포함된 항목 정책(Item Policy)을 타입별로 조회합니다."
    )
    @GetMapping("/{policyId}/items")
    public CustomResponse<List<ItemPolicyResponseDTO>> items(
            @PathVariable Integer policyId,
            @RequestParam ItemType type
    ) {
        return CustomResponse.success(itemPolicyService.getItems(policyId, type));
    }

    /**
     * 정책별 항목 정책(Item Policy) 생성
     *
     * @param policyId 정책 ID
     * @param req      항목 정책 생성 요청 DTO
     * @return 생성된 항목 정책
     */
    @Operation(
            summary = "항목 정책 생성",
            description = "급여 정책에 새로운 항목 정책(Item Policy)을 생성합니다."
    )
    @PostMapping("/{policyId}/items")
    public CustomResponse<ItemPolicyResponseDTO> createItem(
            @PathVariable Integer policyId,
            @RequestBody ItemPolicyUpsertRequestDTO req
    ) {
        return CustomResponse.success(itemPolicyService.createItem(policyId, req));
    }

    /**
     * 항목 정책(Item Policy) 수정
     *
     * @param itemPolicyId 항목 정책 ID
     * @param req          항목 정책 수정 요청 DTO
     * @return 200 OK
     */
    @Operation(
            summary = "항목 정책 수정",
            description = "기존 항목 정책(Item Policy)을 수정합니다."
    )
    @PutMapping("/items/{itemPolicyId}")
    public CustomResponse<Void> updateItem(
            @PathVariable Integer itemPolicyId,
            @RequestBody ItemPolicyUpsertRequestDTO req
    ) {
        itemPolicyService.updateItem(itemPolicyId, req);
        return CustomResponse.success(null);
    }

    /**
     * 항목 정책 대상(Target) 교체
     *
     * @param itemPolicyId 항목 정책 ID
     * @param targets      대상 교체 요청 리스트
     * @return 200 OK
     */
    @Operation(
            summary = "항목 정책 대상 교체",
            description = "항목 정책에 연결된 대상(Target) 목록을 전체 교체합니다."
    )
    @PutMapping("/items/{itemPolicyId}/targets")
    public CustomResponse<Void> replaceTargets(
            @PathVariable Integer itemPolicyId,
            @RequestBody List<ItemPolicyTargetRequestDTO> targets
    ) {
        itemPolicyService.replaceTargets(itemPolicyId, targets);
        return CustomResponse.success(null);
    }

    /**
     * 급여 정책 상세 조회
     *
     * @param policyId 조회할 급여 정책 ID
     * @return 급여 정책 상세 정보
     */
    @Operation(
            summary = "급여 정책 상세 조회",
            description = "급여 정책의 설정(Config) 및 항목 정책(Item Policy)을 포함한 상세 정보를 조회합니다."
    )
    @GetMapping("/{policyId}")
    public CustomResponse<PolicyDetailResponseDTO> detail(@PathVariable Integer policyId) {
        return CustomResponse.success(policyDetailService.getDetail(policyId));
    }


    /**
     * 급여 정책 수정
     *
     * @param policyId 수정할 정책 ID
     * @param req 정책 수정 요청 DTO
     * @return 수정된 정책 정보
     */
    @Operation(
            summary = "급여 정책 수정",
            description = "급여 정책의 기본 정보를 수정합니다. (Draft 상태 정책만 수정 가능)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정책 수정 성공",
                    content = @Content(schema = @Schema(implementation = PolicyResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "정책을 찾을 수 없음")
    })
    @PutMapping("/{policyId}")
    public CustomResponse<PolicyResponseDTO> update(
            @PathVariable Integer policyId,
            @RequestBody PolicyUpdateRequestDTO req
    ) {
        return CustomResponse.success(policyService.updatePolicy(policyId, req));
    }


    /**
     * 급여 정책 삭제
     *
     * @param policyId 삭제할 정책 ID
     * @return 200 OK
     */
    @Operation(
            summary = "급여 정책 삭제",
            description = "Draft 상태의 급여 정책을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정책 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "삭제할 수 없는 상태의 정책"),
            @ApiResponse(responseCode = "404", description = "정책을 찾을 수 없음")
    })
    @DeleteMapping("/{policyId}")
    public CustomResponse<Void> delete(@PathVariable Integer policyId) {
        policyService.deleteDraftPolicy(policyId);
        return CustomResponse.success(null);
    }


    /**
     * 급여 정책 복사
     *
     * @param policyId 복사 대상 정책 ID
     * @param req 복사 옵션 요청 DTO (선택)
     * @return 복사된 신규 정책 정보
     */
    @Operation(
            summary = "급여 정책 복사",
            description = "기존 급여 정책을 복사하여 새로운 Draft 정책을 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정책 복사 성공",
                    content = @Content(schema = @Schema(implementation = PolicyResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "정책을 찾을 수 없음")
    })
    @PostMapping("/{policyId}/copy")
    public CustomResponse<PolicyResponseDTO> copy(
            @PathVariable Integer policyId,
            @RequestBody(required = false) PolicyCopyRequestDTO req
    ) {
        return CustomResponse.success(policyCopyTxService.copy(policyId, req));
    }


    /**
     * 급여 정책 만료 처리
     *
     * @param policyId 만료할 정책 ID
     * @return 만료된 정책 정보
     */
    @Operation(
            summary = "급여 정책 만료 처리",
            description = "활성 또는 비활성 급여 정책을 만료(EXPIRED) 상태로 변경합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정책 만료 성공",
                    content = @Content(schema = @Schema(implementation = PolicyResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "만료할 수 없는 상태"),
            @ApiResponse(responseCode = "404", description = "정책을 찾을 수 없음")
    })
    @PatchMapping("/{policyId}/expire")
    public CustomResponse<PolicyResponseDTO> expire(@PathVariable Integer policyId) {
        return CustomResponse.success(policyService.expirePolicy(policyId));
    }


    /**
     * 항목 정책 일괄 업서트
     *
     * @param policyId 급여 정책 ID
     * @param type 항목 타입 (ALLOWANCE / DEDUCTION)
     * @param reqs 항목 정책 업서트 요청 목록
     * @return 200 OK
     */
    @Operation(
            summary = "항목 정책 일괄 업서트",
            description = "급여 정책에 속한 항목 정책을 타입별로 일괄 등록 또는 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "항목 정책 업서트 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "정책을 찾을 수 없음")
    })
    @PutMapping("/{policyId}/items")
    public CustomResponse<Void> upsertItemPolicies(
            @PathVariable Integer policyId,
            @RequestParam ItemType type,
            @RequestBody List<ItemPolicyUpsertRequestDTO> reqs
    ) {
        itemPolicyService.upsertItems(policyId, type, reqs);
        return CustomResponse.success(null);
    }


    /**
     * 항목 정책 삭제
     *
     * @param policyId 급여 정책 ID
     * @param itemPolicyId 삭제할 항목 정책 ID
     * @return 200 OK
     */
    @Operation(
            summary = "항목 정책 삭제",
            description = "급여 정책에 속한 특정 항목 정책(Item Policy)을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "항목 정책 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "항목 정책을 찾을 수 없음")
    })
    @DeleteMapping("/{policyId}/items/{itemPolicyId}")
    public CustomResponse<Void> deleteItemPolicy(
            @PathVariable Integer policyId,
            @PathVariable Integer itemPolicyId
    ) {
        itemPolicyService.deleteItem(policyId, itemPolicyId);
        return CustomResponse.success(null);
    }
}
