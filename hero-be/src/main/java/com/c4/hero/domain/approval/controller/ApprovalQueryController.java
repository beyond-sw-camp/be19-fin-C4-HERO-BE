package com.c4.hero.domain.approval.controller;

import com.c4.hero.domain.approval.dto.ApprovalTemplateResponseDTO;
import com.c4.hero.domain.approval.dto.response.ApprovalDocumentsResponseDTO;
import com.c4.hero.domain.approval.dto.response.ApprovalTemplateDetailResponseDTO;
import com.c4.hero.domain.approval.service.ApprovalQueryService;
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
 *
 * History
 * 2025/12/15 (민철) 최초 작성 - 서식 목록 조회 / 북마크 / 상신 / 임시저장 api
 * 2025/12/17 (민철) 문서함 조회 api
 * 2025/12/25 (민철) 작성화면 조회 api 및 CQRS 패턴 적용
 *
 * </pre>
 *
 * @author 민철
 * @version 2.0
 */
@Slf4j
@RestController
@RequestMapping("/api/approval")
@RequiredArgsConstructor
public class ApprovalQueryController {

    private final ApprovalQueryService approvalQueryService;


    /**
     * 문서 템플릿 전체 조회
     *
     * @return 문서 템플릿 목록
     */
    @Operation(summary = "전체 문서 서식 목록 조회", description = "기안문 작성 시 선택 가능한 모든 문서 템플릿 목록을 조회합니다.")
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
     * @param templateId 서식이름
     * @return  ResponseEntity<ApprovalTemplateDetailResponseDTO> 서식키/서식이름/서식분류/결재선자동지정/참조목록자동지정
     */
    @Operation(summary = "기안 작성을 위한 서식 상세 정보 조회", description = "특정 서식(templateId) 선택 시, 해당 서식의 상세 정보(카테고리, 자동 지정된 결재선 및 참조자 등)를 조회하여 기안 작성 화면을 구성합니다.")
    @GetMapping("/templates/{templateId}")
    public ResponseEntity<ApprovalTemplateDetailResponseDTO> getTemplate(
            @PathVariable Integer templateId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        Integer employeeId = userDetails.getEmployeeId();
        ApprovalTemplateDetailResponseDTO response = approvalQueryService.getTemplate(employeeId, templateId);
//        log.info("template detail response: {}", response);
        return ResponseEntity.ok().body(response);
    }


    /**
     * 문서 목록 조회
     *
     * @param  page         페이지
     * @param  size         사이즈
     * @param  condition    필터조건 - 문서번호/문서분류/문서서식/문서제목/상신자부서/상신자
     * @param  fromDate     시작일
     * @param  toDate       종료일
     * @param  sortBy       정렬기준 - 날짜/문서번호
     *
     * @return ResponseEntity<>
     */
    @Operation(summary = "나의 결재 문서 목록 조회", description = "로그인한 사용자와 관련된 결재 문서 목록을 조회합니다. 날짜 범위, 검색 조건(condition), 정렬 기준(sortBy)을 적용하여 페이징 된 결과를 반환합니다.")
    @GetMapping("/documents/my-list")
    public ResponseEntity<List<ApprovalDocumentsResponseDTO>> getAllDocuments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String condition) {
        List<ApprovalDocumentsResponseDTO> response = approvalQueryService.getDocuments(page, size, fromDate, toDate, sortBy, condition);

        return ResponseEntity.ok().body(response);
    }
}
