package com.c4.hero.domain.approval.controller;

import com.c4.hero.domain.approval.dto.ApprovalTemplateResponseDTO;
import com.c4.hero.domain.approval.dto.response.ApprovalDocumentsResponseDTO;
import com.c4.hero.domain.approval.dto.response.ApprovalTemplateDetailResponseDTO;
import com.c4.hero.domain.approval.dto.organization.*;
import com.c4.hero.domain.approval.service.ApprovalQueryService;
import com.c4.hero.domain.approval.service.OrganizationService;
import com.c4.hero.domain.auth.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <pre>
 * Class Name  : ApprovalQueryController
 * Description : 전자결재 관련 조회 API 컨트롤러
 * - 서식목록조회 api
 * - 문서함 내 목록 조회 api
 * - 작성화면 조회 api
 * - 조직도 조회 api
 *
 * History
 * 2025/12/15 (민철) 최초 작성 - 서식 목록 조회 / 북마크 / 상신 / 임시저장 api
 * 2025/12/17 (민철) 문서함 조회 api
 * 2025/12/25 (민철) 작성화면 조회 api 및 CQRS 패턴 적용
 * 2025/12/26 (민철) 조직도 조회 api 추가
 *
 * </pre>
 *
 * @author 민철
 * @version 2.1
 */
@Slf4j
@RestController
@RequestMapping("/api/approval")
@RequiredArgsConstructor
public class ApprovalQueryController {

    private final ApprovalQueryService approvalQueryService;
    private final OrganizationService organizationService;


    /**
     * 문서 템플릿 전체 조회
     *
     * @param userDetails 인증된 사용자 정보
     * @return 문서 템플릿 목록
     */
    @Operation(
            summary = "전체 문서 서식 목록 조회",
            description = "기안문 작성 시 선택 가능한 모든 문서 템플릿 목록을 조회함"
    )
    @GetMapping("/templates")
    public ResponseEntity<List<ApprovalTemplateResponseDTO>> getTemplates(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer employeeId = userDetails.getEmployeeId();
        List<ApprovalTemplateResponseDTO> templates = approvalQueryService.getAllTemplates(employeeId);

        return ResponseEntity.ok(templates);
    }


    /**
     * 서식 작성 화면 조회
     *
     * @param templateId  서식 ID
     * @param userDetails 인증된 사용자 정보
     * @return ResponseEntity<ApprovalTemplateDetailResponseDTO> 서식 상세 정보
     */
    @Operation(
            summary = "기안 작성을 위한 서식 상세 정보 조회",
            description = "특정 서식(templateId) 선택 시, 해당 서식의 상세 정보(카테고리, 자동 지정된 결재선 및 참조자 등)를 조회하여 기안 작성 화면을 구성함"
    )
    @GetMapping("/templates/{templateId}")
    public ResponseEntity<ApprovalTemplateDetailResponseDTO> getTemplate(
            @PathVariable Integer templateId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer employeeId = userDetails.getEmployeeId();
        ApprovalTemplateDetailResponseDTO response = approvalQueryService.getTemplate(employeeId, templateId);

        return ResponseEntity.ok().body(response);
    }


    /**
     * 문서 목록 조회
     *
     * @param page      페이지 번호
     * @param size      페이지 크기
     * @param condition 필터 조건 (문서번호/문서분류/문서서식/문서제목/상신자부서/상신자)
     * @param fromDate  시작일
     * @param toDate    종료일
     * @param sortBy    정렬 기준 (날짜/문서번호)
     * @return ResponseEntity<List < ApprovalDocumentsResponseDTO>> 문서 목록
     */
    @Operation(
            summary = "나의 결재 문서 목록 조회",
            description = "로그인한 사용자와 관련된 결재 문서 목록을 조회함. 날짜 범위, 검색 조건(condition), 정렬 기준(sortBy)을 적용하여 페이징된 결과를 반환함"
    )
    @GetMapping("/documents/my-list")
    public ResponseEntity<List<ApprovalDocumentsResponseDTO>> getAllDocuments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String condition
    ) {
        List<ApprovalDocumentsResponseDTO> response = approvalQueryService.getDocuments(
                page, size, fromDate, toDate, sortBy, condition
        );

        return ResponseEntity.ok().body(response);
    }


    /* ========================================================================= */
    /* 조직도 관련 API */
    /* ========================================================================= */

    /**
     * 조직도 전체 조회
     * 계층 구조로 조직도를 조회함
     *
     * @return ResponseEntity<OrganizationTreeResponseDTO> 조직도 트리 구조
     */
    @Operation(
            summary = "조직도 전체 조회",
            description = "계층 구조로 된 전체 조직도를 조회함. 부서와 직원 정보를 트리 형태로 반환함"
    )
    @GetMapping("/organization/tree")
    public ResponseEntity<OrganizationTreeResponseDTO> getOrganizationTree() {
        log.info("📋 조직도 전체 조회 요청");

        OrganizationTreeResponseDTO response = organizationService.getOrganizationTree();

        log.info("✅ 조직도 조회 완료");
        return ResponseEntity.ok().body(response);
    }


    /**
     * 직원 검색
     * 이름, 부서, 직책으로 직원을 검색함
     *
     * @param keyword      검색 키워드 (이름, 부서, 직책)
     * @param departmentId 부서 ID (선택)
     * @param gradeId      직급 ID (선택)
     * @return ResponseEntity<EmployeeSearchResponseDTO> 검색 결과
     */
    @Operation(
            summary = "직원 검색",
            description = "이름, 부서, 직책으로 직원을 검색함. 검색 키워드는 필수이며, 부서 ID와 직급 ID는 선택적으로 필터링할 수 있음"
    )
    @GetMapping("/organization/employees/search")
    public ResponseEntity<EmployeeSearchResponseDTO> searchEmployees(
            @RequestParam String keyword,
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) Integer gradeId
    ) {
        log.info("🔍 직원 검색 요청 - keyword: {}, departmentId: {}, gradeId: {}",
                keyword, departmentId, gradeId);

        EmployeeSearchRequestDTO requestDTO = EmployeeSearchRequestDTO.builder()
                .keyword(keyword)
                .departmentId(departmentId)
                .gradeId(gradeId)
                .build();

        EmployeeSearchResponseDTO response = organizationService.searchEmployees(requestDTO);

        log.info("✅ 직원 검색 완료 - 결과: {}건", response.getTotalCount());
        return ResponseEntity.ok().body(response);
    }


    /**
     * 특정 부서의 직원 목록 조회
     * 특정 부서에 속한 모든 직원 목록을 조회함
     *
     * @param departmentId 부서 ID
     * @return ResponseEntity<List < OrganizationEmployeeDTO>> 부서 소속 직원 목록
     */
    @Operation(
            summary = "특정 부서의 직원 목록 조회",
            description = "특정 부서에 속한 모든 직원 목록을 조회함. 부서 ID를 기반으로 해당 부서의 직원들을 반환함"
    )
    @GetMapping("/organization/departments/{departmentId}/employees")
    public ResponseEntity<List<OrganizationEmployeeDTO>> getDepartmentEmployees(
            @PathVariable Integer departmentId
    ) {
        log.info("👥 부서별 직원 조회 요청 - departmentId: {}", departmentId);

        List<OrganizationEmployeeDTO> employees = organizationService.getDepartmentEmployees(departmentId);

        log.info("✅ 부서별 직원 조회 완료 - 결과: {}명", employees.size());
        return ResponseEntity.ok().body(employees);
    }
}