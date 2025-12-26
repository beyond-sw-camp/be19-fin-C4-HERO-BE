package com.c4.hero.domain.approval.controller;

import com.c4.hero.domain.approval.dto.request.ApprovalRequestDTO;
import com.c4.hero.domain.approval.service.ApprovalCommandService;
import com.c4.hero.domain.auth.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
/**
 * <pre>
 * Class Name  : ApprovalCommandController
 * Description : 전자결재 문서 템플릿 관련 API를 처리하는 컨트롤러
 * - 북마크기능 api
 * - 상신/임시저장 api
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
public class ApprovalCommandController {

    private final ApprovalCommandService approvalCommandService;


    /**
     * 문서 템플릿 즐겨찾기 토글
     *
     * @param templateId 문서 템플릿 ID
     * @return 즐겨찾기 여부
     */
    @Operation(summary = "서식 즐겨찾기 설정/해제", description = "자주 사용하는 문서 서식을 즐겨찾기에 추가하거나 해제(토글)합니다. 반환값이 true이면 즐겨찾기 등록, false이면 해제 상태입니다.")
    @PostMapping("/templates/{templateId}/bookmark")
    public ResponseEntity<Boolean> toggleBookmark(
            @PathVariable Integer templateId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer employeeId = userDetails.getEmployeeId();
        boolean isBookmarked = approvalCommandService.toggleBookmark(employeeId, templateId);

        return ResponseEntity.ok(isBookmarked);
    }


    /**
     * 임시저장
     * <p>
     * consumes = MediaType.MULTIPART_FORM_DATA_VALUE 필수
     *
     * @param dto   문서 생성 요청 DTO
     * @param files 첨부 파일 목록
     * @return 처리 결과
     */
    @Operation(summary = "문서 임시저장", description = "작성 중인 기안 문서를 임시로 저장합니다. 첨부파일과 데이터를 저장하지만 결재 프로세스는 시작되지 않습니다. (상태: DRAFT)")
    @PostMapping(
            value = "/draft",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> createDraft(
            @RequestPart("data") ApprovalRequestDTO dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        Integer employeeId = userDetails.getEmployeeId();
        Integer docId = approvalCommandService.createDocument(employeeId, dto, files, "DRAFT");

        return ResponseEntity.ok().body("임시저장 완료. ID: " + docId);
    }


    /**
     * 상신
     *
     * @param dto   문서 생성 요청 DTO
     * @param files 첨부 파일 목록
     * @return 처리 결과
     */
    @Operation(summary = "결재 문서 상신", description = "작성된 기안 문서를 정식으로 상신합니다. 데이터 저장과 동시에 결재 프로세스가 시작되며 대기 상태로 전환됩니다. (상태: PENDING)")
    @PostMapping(
            value = "/submit",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> submit(
            @RequestPart("data") ApprovalRequestDTO dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {


        Integer employeeId = userDetails.getEmployeeId();
        Integer docId = approvalCommandService.createDocument(employeeId, dto, files, "PENDING");

        return ResponseEntity.ok().body("상신 완료. ID: " + docId);
    }

}
