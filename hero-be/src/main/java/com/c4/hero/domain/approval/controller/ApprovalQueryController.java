package com.c4.hero.domain.approval.controller;

import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.approval.dto.ApprovalTemplateResponseDTO;
import com.c4.hero.domain.approval.dto.response.VacationTypeResponseDTO;
import com.c4.hero.domain.approval.dto.response.ApprovalDocumentDetailResponseDTO;
import com.c4.hero.domain.approval.dto.response.ApprovalDocumentsResponseDTO;
import com.c4.hero.domain.approval.dto.response.ApprovalTemplateDetailResponseDTO;
import com.c4.hero.domain.approval.dto.organization.*;
import com.c4.hero.domain.approval.repository.ApprovalVacationTypeRepository;
import com.c4.hero.domain.approval.service.ApprovalQueryService;
import com.c4.hero.domain.approval.service.OrganizationService;
import com.c4.hero.domain.auth.security.CustomUserDetails;
import com.c4.hero.domain.vacation.entity.VacationType;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
 * 2025/12/26 (민철) 문서함 목록 조회 구현 (PageResponse 사용)
 *
 * </pre>
 *
 * @author 민철
 * @version 2.2
 */
@Slf4j
@RestController
@RequestMapping("/api/approval")
@RequiredArgsConstructor
public class ApprovalQueryController {

    private final ApprovalQueryService approvalQueryService;
    private final OrganizationService organizationService;
    private final ApprovalVacationTypeRepository approvalVacationTypeRepository;


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
     * 문서함 문서 목록 조회 (탭별 필터링)
     *
     * @param page      페이지 번호 (1부터 시작)
     * @param size      페이지 크기
     * @param tab       탭 구분 (all/que/request/reject/ref/end/draft)
     * @param fromDate  시작일
     * @param toDate    종료일
     * @param sortBy    정렬 기준
     * @param condition 검색 조건
     * @param userDetails 인증된 사용자 정보
     * @return ResponseEntity<PageResponse<ApprovalDocumentsResponseDTO>> 문서 목록 (페이지 정보 포함)
     */
    @Operation(
            summary = "문서함 문서 목록 조회",
            description = "로그인한 사용자의 문서함을 탭별로 필터링하여 조회함. " +
                    "탭: all(전체), que(대기), request(요청), reject(반려), ref(참조), end(승인), draft(임시저장)"
    )
    @GetMapping("/inbox/documents")
    public ResponseEntity<PageResponse<ApprovalDocumentsResponseDTO>> getInboxDocuments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "all") String tab,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String condition,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer employeeId = userDetails.getEmployeeId();
        log.info("📬 문서함 조회 요청 - employeeId: {}, tab: {}, page: {}", employeeId, tab, page);

        PageResponse<ApprovalDocumentsResponseDTO> response = approvalQueryService.getInboxDocuments(
                page, size, tab, fromDate, toDate, sortBy, condition, employeeId
        );

        log.info("✅ 문서함 조회 완료 - 결과: {}건", response.getTotalElements());
        return ResponseEntity.ok().body(response);
    }

    /**
     * 문서 상세 조회
     *
     * @param docId       문서 ID
     * @param userDetails 인증된 사용자 정보
     * @return ResponseEntity<ApprovalDocumentDetailResponseDTO> 문서 상세 정보
     */
    @Operation(
            summary = "문서 상세 조회",
            description = "문서 ID로 결재 문서의 상세 정보를 조회함. 문서 기본 정보, 결재선, 참조자, 첨부파일 정보 포함"
    )
    @GetMapping("/documents/{docId}")
    public ResponseEntity<ApprovalDocumentDetailResponseDTO> getDocumentDetail(
            @PathVariable Integer docId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer employeeId = userDetails.getEmployeeId();
        log.info("📄 문서 상세 조회 요청 - docId: {}, employeeId: {}", docId, employeeId);

        ApprovalDocumentDetailResponseDTO response = approvalQueryService.getDocumentDetail(docId, employeeId);

        log.info("✅ 문서 상세 조회 완료 - docNo: {}", response.getDocNo());
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
    
    /**
     * 
     *
     * @param  
     * @return  
     */
    @GetMapping("/vacation-types")
    public ResponseEntity<List<VacationTypeResponseDTO>> getVacationTypes() {
        
        List<VacationType> typeEntity = approvalVacationTypeRepository.findAll();

        List<VacationTypeResponseDTO> response = typeEntity.stream()
                .map(type -> VacationTypeResponseDTO.builder()
                        .vacationTypeId(type.getVacationTypeId())
                        .vacationTypeName(type.getVacationTypeName())
                        .build()).collect(Collectors.toList());;
        return ResponseEntity.ok().body(response);
    }
}