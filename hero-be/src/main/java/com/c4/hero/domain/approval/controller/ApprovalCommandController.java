package com.c4.hero.domain.approval.controller;

import com.c4.hero.domain.approval.dto.request.ApprovalActionRequestDTO;
import com.c4.hero.domain.approval.dto.request.ApprovalRequestDTO;
import com.c4.hero.domain.approval.dto.response.ApprovalActionResponseDTO;
import com.c4.hero.domain.approval.service.ApprovalCommandService;
import com.c4.hero.domain.auth.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * 2025/12/31 (민철) 대기중 문서 회수처리 api
 * 2026/01/01 (민철) 임시저장 문서 삭제 api 추가
 *
 * </pre>
 *
 * @author 민철
 * @version 2.1
 */
@Slf4j
@RestController
@Tag(name = "결재 API")
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
    @Operation(
            summary = "서식 즐겨찾기 설정/해제",
            description = "자주 사용하는 문서 서식을 즐겨찾기에 추가하거나 해제(토글)합니다. 반환값이 true이면 즐겨찾기 등록, false이면 해제 상태입니다.")
    @PostMapping("/templates/{templateId}/bookmark")
    public ResponseEntity<Boolean> toggleBookmark(
            @PathVariable Integer templateId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        boolean isBookmarked =
                approvalCommandService.toggleBookmark(userDetails.getEmployeeId(), templateId);

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
    @Operation(
            summary = "문서 임시저장",
            description = "작성 중인 기안 문서를 임시로 저장합니다. 첨부파일과 데이터를 저장하지만 결재 프로세스는 시작되지 않습니다. (상태: DRAFT)")
    @PostMapping(
            value = "/draft",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> createDraft(
            @RequestPart("data") ApprovalRequestDTO dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        Integer docId =
                approvalCommandService.createDocument(userDetails.getEmployeeId(), dto, files, "DRAFT");

        return ResponseEntity.ok().body("임시저장 완료. ID: " + docId);
    }


    /**
     * 상신
     *
     * @param dto   문서 생성 요청 DTO
     * @param files 첨부 파일 목록
     * @return 처리 결과
     */
    @Operation(
            summary = "결재 문서 상신",
            description = "작성된 기안 문서를 정식으로 상신합니다. 데이터 저장과 동시에 결재 프로세스가 시작되며 대기 상태로 전환됩니다. (상태: PENDING)")
    @PostMapping(
            value = "/submit",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> submit(
            @RequestPart("data") ApprovalRequestDTO dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {


        Integer docId =
                approvalCommandService.createDocument(userDetails.getEmployeeId(), dto, files, "INPROGRESS");

        return ResponseEntity.ok().body("상신 완료. ID: " + docId);
    }

    /**
     * 임시저장 문서 업데이트
     *
     * @param docId   문서 ID
     * @param request 문서 수정 요청 DTO
     * @param files   첨부 파일 목록
     * @return 업데이트된 문서 ID
     */
    @Operation(
            summary = "임시저장 문서 수정",
            description = "임시저장 문서의 내용을 수정합니다. 제목, 상세내용, 결재선, 참조자, 첨부파일을 모두 변경할 수 있습니다.")
    @PutMapping("/documents/{docId}")
    public ResponseEntity<Integer> updateDraftDocument(
            @PathVariable Integer docId,
            @RequestPart("data") ApprovalRequestDTO request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer updatedDocId =
                approvalCommandService.updateDraftDocument(userDetails.getEmployeeId(), docId, request, files);

        return ResponseEntity.ok(updatedDocId);
    }

    /**
     * 임시저장 문서를 상신으로 변경
     *
     * @param docId 문서 ID
     * @param data  문서 수정 요청 DTO
     * @param files 첨부 파일 목록
     * @return 상신된 문서 ID
     */
    @Operation(
            summary = "임시저장 문서 상신",
            description = "임시저장된 문서를 정식으로 상신합니다. 문서 번호가 생성되고 결재 프로세스가 시작됩니다. (상태: DRAFT → INPROGRESS)")
    @PostMapping(
            value = "/documents/{docId}/submit",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> submitDraftDocument(
            @PathVariable Integer docId,
            @RequestPart("data") ApprovalRequestDTO data,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer submittedDocId =
                approvalCommandService.submitDraftDocument(userDetails.getEmployeeId(), docId, data, files);

        return ResponseEntity.ok().body("상신 완료. ID: " + submittedDocId);
    }

    /**
     * 결재 승인/반려 처리
     *
     * @param request     결재 처리 요청 DTO
     * @param userDetails 인증된 사용자 정보
     * @return ResponseEntity<ApprovalActionResponseDTO> 처리 결과
     */
    @Operation(
            summary = "결재 승인/반려 처리",
            description = "결재자가 문서를 승인하거나 반려함. 반려 시 comment 필수"
    )
    @PostMapping("/process")
    public ResponseEntity<ApprovalActionResponseDTO> processApproval(
            @RequestBody ApprovalActionRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ApprovalActionResponseDTO response =
                approvalCommandService.processApproval(request, userDetails.getEmployeeId());

        return ResponseEntity.ok().body(response);
    }

    /**
     * 결재 대기 중 문서 회수 처리
     *
     * @param docId 문서ID
     * @return message 회수처리성공 메시지(임시저장문서함으로 이동하였습니다)
     */
    @Operation(
            summary = "결재 대기 중 문서 회수",
            description = "진행 중인 결재 문서를 회수하여 임시저장 상태로 변경합니다. 결재선의 모든 승인 상태가 초기화되며, 문서 상태가 DRAFT로 변경됩니다."
    )
    @PostMapping("/{docId}/cancellations")
    public ResponseEntity<?> cancelDocument(@PathVariable Integer docId) {

        String message = approvalCommandService.cancelDocument(docId);

        return ResponseEntity.ok().body(message);
    }

    /**
     * 임시저장 문서 삭제
     *
     * @param docId 임시저장 문서번호
     * @return 삭제 완료 메시지
     */
    @Operation(
            summary = "임시저장 문서 삭제",
            description = "임시저장 상태(DRAFT)의 문서를 완전히 삭제합니다. 문서의 결재선, 참조자, 첨부파일이 모두 함께 삭제되며, 복구할 수 없습니다."
    )
    @DeleteMapping("/{docId}")
    public ResponseEntity<?> deleteDocument(@PathVariable Integer docId) {

        String message = approvalCommandService.deleteDocument(docId);

        return ResponseEntity.ok().body(message);
    }

}