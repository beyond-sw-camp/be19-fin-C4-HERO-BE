package com.c4.hero.domain.approval.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.common.s3.S3Service;
import com.c4.hero.domain.approval.dto.ApprovalLineDTO;
import com.c4.hero.domain.approval.dto.ApprovalReferenceDTO;
import com.c4.hero.domain.approval.dto.request.ApprovalActionRequestDTO;
import com.c4.hero.domain.approval.dto.request.ApprovalRequestDTO;
import com.c4.hero.domain.approval.dto.response.ApprovalActionResponseDTO;
import com.c4.hero.domain.approval.entity.*;
import com.c4.hero.domain.approval.event.ApprovalCompletedEvent;
import com.c4.hero.domain.approval.event.ApprovalRejectedEvent;
import com.c4.hero.domain.approval.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * <pre>
 * Class Name  : ApprovalCommandService
 * Description : 전자결재 커맨드 관련 서비스 로직 (삽입/수정/삭제)
 *
 * History
 *   2025/12/25 (민철) CQRS 패턴 적용 및 작성화면 조회 메서드 로직 추가
 *   2025/12/26 (민철) 결재선/참조목록 저장 로직 추가 및 DTO 필드명 수정
 *   2025/12/28 (승건) 반려 이벤트 발행 로직 추가
 *   2025/12/31 (민철) 임시저장 문서 수정 및 상신 메서드 추가
 *   2026/01/01 (민철) S3 파일 업로드 방식으로 변경
 * </pre>
 *
 * @author 민철
 * @version 2.5
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalCommandService {

    private final ApprovalDocumentRepository documentRepository;
    private final ApprovalAttachmentRepository attachmentRepository;
    private final ApprovalLineRepository lineRepository;
    private final ApprovalReferenceRepository referenceRepository;
    private final ApprovalBookmarkRepository bookmarkRepository;
    private final ApprovalTemplateRepository templateRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final S3Service s3Service;

    /* ========================================== */
    /* 즐겨찾기 */
    /* ========================================== */

    /**
     * 즐겨찾기 토글 (있으면 삭제, 없으면 추가)
     *
     * @param empId      사원 ID
     * @param templateId 문서 템플릿 ID
     * @return 즐겨찾기 상태
     */
    @Transactional
    public boolean toggleBookmark(Integer empId, Integer templateId) {
        Optional<ApprovalBookmark> bookmark =
                bookmarkRepository.findByEmpIdAndTemplateId(empId, templateId);

        if (bookmark.isPresent()) {
            bookmarkRepository.delete(bookmark.get());
            return false; // 즐겨찾기 해제
        } else {
            ApprovalBookmark newBookmark = ApprovalBookmark.builder()
                    .empId(empId)
                    .templateId(templateId)
                    .build();
            bookmarkRepository.save(newBookmark);
            return true; // 즐겨찾기 등록
        }
    }

    /* ========================================== */
    /* 문서 생성 (임시저장/상신) */
    /* ========================================== */

    /**
     * 문서 생성 (임시저장 or 상신)
     *
     * @param employeeId 기안자 ID
     * @param dto        문서 생성 요청 DTO
     * @param files      첨부 파일 목록
     * @param status     문서 상태 (DRAFT / INPROGRESS)
     * @return 생성된 문서 ID
     */
    @Transactional
    public Integer createDocument(
            Integer employeeId,
            ApprovalRequestDTO dto,
            List<MultipartFile> files,
            String status
    ) {
        log.info("문서 생성 시작 - employeeId: {}, status: {}", employeeId, status);

        // 1. 문서 본문 저장
        ApprovalDocument document = createApprovalDocument(employeeId, dto, status);
        ApprovalDocument savedDoc = documentRepository.save(document);
        log.info("문서 저장 완료 - docId: {}", savedDoc.getDocId());

        // 2. 결재선 저장
        if (dto.getLines() != null && !dto.getLines().isEmpty()) {
            saveApprovalLines(savedDoc.getDocId(), dto.getLines());
            log.info("결재선 저장 완료 - 결재자 수: {}", dto.getLines().size());
        }

        // 3. 참조자 저장
        if (dto.getReferences() != null && !dto.getReferences().isEmpty()) {
            saveReferences(savedDoc.getDocId(), dto.getReferences());
            log.info("참조자 저장 완료 - 참조자 수: {}", dto.getReferences().size());
        }

        // 4. 첨부파일 저장 (S3)
        if (files != null && !files.isEmpty()) {
            saveFilesToS3(files, savedDoc);
            log.info("첨부파일 저장 완료 - 파일 수: {}", files.size());
        }

        log.info("문서 생성 완료 - docId: {}", savedDoc.getDocId());
        return savedDoc.getDocId();
    }

    /**
     * 문서 번호 생성 (Format: HERO-yyyy-00001)
     * 동시성 제어를 위해 synchronized 사용
     */
    private synchronized String generateDocNo() {
        String currentYear = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        String prefix = "HERO-" + currentYear + "-";

        String lastDocNo = documentRepository.findLastDocNoLike(prefix + "%");

        int nextSeq = 1;
        if (lastDocNo != null) {
            String seqStr = lastDocNo.substring(lastDocNo.lastIndexOf("-") + 1);
            nextSeq = Integer.parseInt(seqStr) + 1;
        }

        return prefix + String.format("%05d", nextSeq);
    }

    /**
     * ApprovalDocument Entity 생성
     */
    private ApprovalDocument createApprovalDocument(
            Integer employeeId,
            ApprovalRequestDTO dto,
            String status
    ) {
        ApprovalTemplate templateEntity = templateRepository.findByTemplateKey(dto.getFormType());

        return ApprovalDocument.builder()
                .templateId(templateEntity.getTemplateId())
                .drafterId(employeeId)
                .title(dto.getTitle())
                .details(dto.getDetails())
                .docStatus(status)
                .build();
    }

    /* ========================================== */
    /* 결재선 저장 */
    /* ========================================== */

    /**
     * 결재선 저장 (기안자 자동 승인)
     * - seq=1 (기안자): APPROVED 상태로 저장, processDate 자동 설정
     * - seq>1 (결재자들): PENDING 상태로 저장
     *
     * @param docId 문서 ID
     * @param lines 결재선 DTO 목록
     */
    private void saveApprovalLines(Integer docId, List<ApprovalLineDTO> lines) {
        for (ApprovalLineDTO lineDTO : lines) {
            String initialStatus = (lineDTO.getSeq() == 1) ? "APPROVED" : "PENDING";

            ApprovalLine.ApprovalLineBuilder builder = ApprovalLine.builder()
                    .docId(docId)
                    .approverId(lineDTO.getApproverId())
                    .seq(lineDTO.getSeq())
                    .lineStatus(initialStatus);

            if (lineDTO.getSeq() == 1) {
                builder.processDate(LocalDateTime.now());
            }

            ApprovalLine line = builder.build();
            lineRepository.save(line);

            log.debug("결재선 저장 - seq: {}, approverId: {}, status: {}",
                    lineDTO.getSeq(), lineDTO.getApproverId(), initialStatus);
        }
    }

    /* ========================================== */
    /* 참조자 저장 */
    /* ========================================== */

    /**
     * 참조자 저장
     *
     * @param docId      문서 ID
     * @param references 참조자 DTO 목록
     */
    private void saveReferences(Integer docId, List<ApprovalReferenceDTO> references) {
        for (ApprovalReferenceDTO refDTO : references) {
            ApprovalReference reference = ApprovalReference.builder()
                    .docId(docId)
                    .empId(refDTO.getReferencerId())
                    .build();

            referenceRepository.save(reference);
            log.debug("참조자 저장 - empId: {}", refDTO.getReferencerId());
        }
    }

    /* ========================================== */
    /* 첨부파일 저장 (S3) */
    /* ========================================== */

    /**
     * 첨부파일을 S3에 업로드하고 DB에 저장
     *
     * @param files    업로드할 파일 목록
     * @param document 문서 엔티티
     */
    private void saveFilesToS3(List<MultipartFile> files, ApprovalDocument document) {
        for (MultipartFile file : files) {
            try {
                // S3에 파일 업로드
                String s3Key = s3Service.uploadFile(file, "approval");

                // DB에 첨부파일 정보 저장
                ApprovalAttachment attachment = ApprovalAttachment.builder()
                        .document(document)
                        .originName(file.getOriginalFilename())
                        .savePath(s3Key)
                        .fileSize(file.getSize())
                        .build();

                attachmentRepository.save(attachment);
                log.info("S3 파일 업로드 및 DB 저장 완료 - 원본명: {}, S3 Key: {}",
                        file.getOriginalFilename(), s3Key);

            } catch (Exception e) {
                log.error("파일 업로드 실패 - 파일명: {}", file.getOriginalFilename(), e);
                throw new RuntimeException("파일 업로드에 실패했습니다: " + file.getOriginalFilename(), e);
            }
        }
    }

    /* ========================================== */
    /* 임시저장 문서 수정 */
    /* ========================================== */

    /**
     * 임시저장 문서 수정
     *
     * @param employeeId 사원 ID
     * @param docId      문서 ID
     * @param dto        수정할 내용
     * @param files      새 첨부파일 목록
     * @return 수정된 문서 ID
     */
    @Transactional
    public Integer updateDraftDocument(
            Integer employeeId,
            Integer docId,
            ApprovalRequestDTO dto,
            List<MultipartFile> files
    ) {
        log.info("임시저장 문서 수정 시작 - docId: {}, employeeId: {}", docId, employeeId);

        // 1. 문서 조회 및 검증
        ApprovalDocument document = documentRepository.findById(docId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "문서를 찾을 수 없습니다."));

        if (!"DRAFT".equals(document.getDocStatus())) {
            throw new IllegalArgumentException("임시저장 상태의 문서만 수정 가능합니다.");
        }

        if (!document.getDrafterId().equals(employeeId)) {
            throw new IllegalArgumentException("문서 작성자만 수정할 수 있습니다.");
        }

        // 2. 문서 본문 업데이트
        document.updateTitle(dto.getTitle());
        document.updateDetails(dto.getDetails());
        log.info("문서 본문 업데이트 완료 - docId: {}", docId);

        // 3. 기존 결재선 삭제 후 재생성
        lineRepository.deleteByDocId(docId);
        if (dto.getLines() != null && !dto.getLines().isEmpty()) {
            saveApprovalLines(docId, dto.getLines());
            log.info("결재선 업데이트 완료 - 결재자 수: {}", dto.getLines().size());
        }

        // 4. 기존 참조자 삭제 후 재생성
        referenceRepository.deleteByDocId(docId);
        if (dto.getReferences() != null && !dto.getReferences().isEmpty()) {
            saveReferences(docId, dto.getReferences());
            log.info("참조자 업데이트 완료 - 참조자 수: {}", dto.getReferences().size());
        }

        // 5. 기존 첨부파일 삭제 (S3 및 DB)
        deleteAttachments(docId);

        // 6. 새 파일 업로드 (S3)
        if (files != null && !files.isEmpty()) {
            saveFilesToS3(files, document);
            log.info("첨부파일 업데이트 완료 - 파일 수: {}", files.size());
        }

        log.info("임시저장 문서 수정 완료 - docId: {}", docId);
        return docId;
    }

    /**
     * 문서의 모든 첨부파일 삭제 (S3 및 DB)
     *
     * @param docId 문서 ID
     */
    private void deleteAttachments(Integer docId) {
        List<ApprovalAttachment> existingFiles = attachmentRepository.findByDocumentDocId(docId);

        for (ApprovalAttachment attachment : existingFiles) {
            // S3에서 파일 삭제
            try {
                s3Service.deleteFile(attachment.getSavePath());
                log.info("S3 파일 삭제 완료 - S3 Key: {}", attachment.getSavePath());
            } catch (Exception e) {
                log.error("S3 파일 삭제 실패 - S3 Key: {}", attachment.getSavePath(), e);
                // S3 삭제 실패해도 계속 진행
            }
        }

        // DB에서 첨부파일 레코드 삭제
        attachmentRepository.deleteByDocumentDocId(docId);
        log.info("첨부파일 DB 레코드 삭제 완료 - docId: {}", docId);
    }

    /* ========================================== */
    /* 임시저장 문서 상신 */
    /* ========================================== */

    /**
     * 임시저장 문서를 상신 처리
     * - DRAFT → INPROGRESS 상태 변경
     * - 문서 번호는 최종 승인 시 생성
     *
     * @param employeeId 사원 ID
     * @param docId      문서 ID
     * @param dto        수정할 내용
     * @param files      새 첨부파일 목록
     * @return 상신된 문서 ID
     */
    @Transactional
    public Integer submitDraftDocument(
            Integer employeeId,
            Integer docId,
            ApprovalRequestDTO dto,
            List<MultipartFile> files
    ) {
        log.info("임시저장 문서 상신 시작 - docId: {}, employeeId: {}", docId, employeeId);

        // 1. 문서 조회 및 검증
        ApprovalDocument document = documentRepository.findById(docId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "문서를 찾을 수 없습니다."));

        if (!"DRAFT".equals(document.getDocStatus())) {
            throw new IllegalArgumentException("임시저장 상태의 문서만 상신 가능합니다.");
        }

        if (!document.getDrafterId().equals(employeeId)) {
            throw new IllegalArgumentException("문서 작성자만 상신할 수 있습니다.");
        }

        // 2. 문서 본문 업데이트
        document.updateTitle(dto.getTitle());
        document.updateDetails(dto.getDetails());
        log.info("문서 본문 업데이트 완료 - docId: {}", docId);

        // 3. 문서 상태 변경 (DRAFT → INPROGRESS)
        document.changeStatus("INPROGRESS");
        documentRepository.save(document);
        log.info("문서 상태 변경 완료 - docId: {}, status: INPROGRESS (문서 번호는 최종 승인 시 생성)", docId);

        // 4. 기존 결재선 삭제 후 재생성
        lineRepository.deleteByDocId(docId);
        if (dto.getLines() != null && !dto.getLines().isEmpty()) {
            saveApprovalLines(docId, dto.getLines());
            log.info("결재선 업데이트 완료 - 결재자 수: {}", dto.getLines().size());
        }

        // 5. 기존 참조자 삭제 후 재생성
        referenceRepository.deleteByDocId(docId);
        if (dto.getReferences() != null && !dto.getReferences().isEmpty()) {
            saveReferences(docId, dto.getReferences());
            log.info("참조자 업데이트 완료 - 참조자 수: {}", dto.getReferences().size());
        }

        // 6. 기존 첨부파일 삭제 (S3 및 DB)
        deleteAttachments(docId);

        // 7. 새 파일 업로드 (S3)
        if (files != null && !files.isEmpty()) {
            saveFilesToS3(files, document);
            log.info("첨부파일 업데이트 완료 - 파일 수: {}", files.size());
        }

        log.info("임시저장 문서 상신 완료 - docId: {} (문서 번호는 최종 승인 시 생성됨)", docId);
        return docId;
    }

    /* ========================================== */
    /* 결재 처리 */
    /* ========================================== */

    /**
     * 결재 처리 (승인/반려)
     *
     * @param request    결재 처리 요청
     * @param employeeId 결재자 ID
     * @return 처리 결과
     */
    @Transactional
    public ApprovalActionResponseDTO processApproval(
            ApprovalActionRequestDTO request,
            Integer employeeId
    ) {
        // 1. 유효성 검증
        validateApprovalAction(request);

        // 2. 결재선 조회 및 권한 확인
        ApprovalLine line = lineRepository.findById(request.getLineId())
                .orElseThrow(() -> new IllegalArgumentException("결재선을 찾을 수 없음"));

        if (!line.getApproverId().equals(employeeId)) {
            throw new IllegalArgumentException("결재 권한 없음");
        }

        if (!"PENDING".equals(line.getLineStatus())) {
            throw new IllegalArgumentException("이미 처리된 결재임");
        }

        // 3. 문서 조회 및 상태 확인
        ApprovalDocument document = documentRepository.findById(request.getDocId())
                .orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없음"));

        if (!"INPROGRESS".equals(document.getDocStatus())) {
            throw new IllegalArgumentException("진행중인 문서가 아님");
        }

        // 4. 결재 처리
        if ("REJECT".equals(request.getAction())) {
            // 반려 처리
            line.reject(request.getComment());
            document.reject();

            publishApprovalRejectedEvent(document, request.getComment());

            return ApprovalActionResponseDTO.builder()
                    .success(true)
                    .message("반려 처리 완료")
                    .docStatus("REJECTED")
                    .build();
        } else {
            // 승인 처리
            line.approve();

            // 5. 모든 결재자 승인 확인
            List<ApprovalLine> allLines = lineRepository.findByDocIdOrderBySeqAsc(request.getDocId());
            boolean allApproved = allLines.stream()
                    .filter(l -> l.getSeq() > 1)
                    .allMatch(l -> "APPROVED".equals(l.getLineStatus()));

            if (allApproved) {
                // 최종 승인 완료
                document.complete();

                // 문서 번호가 없으면 생성 (상신 시점에 생성되었을 것이므로 일반적으로 실행 안됨)
                if (document.getDocNo() == null || document.getDocNo().isEmpty()) {
                    String docNo = generateDocNo();
                    document.assignDocNo(docNo);
                    log.info("최종 승인 완료 - 문서 번호 생성됨: {}", docNo);
                }

                publishApprovalCompletedEvent(document);

                return ApprovalActionResponseDTO.builder()
                        .success(true)
                        .message("최종 승인 완료")
                        .docStatus("APPROVED")
                        .docNo(document.getDocNo())
                        .build();
            } else {
                // 아직 대기중인 결재자 있음
                document.changeStatus("INPROGRESS");

                return ApprovalActionResponseDTO.builder()
                        .success(true)
                        .message("승인 처리 완료")
                        .docStatus("INPROGRESS")
                        .build();
            }
        }
    }

    /**
     * 결재 완료 이벤트 발행
     */
    private void publishApprovalCompletedEvent(ApprovalDocument document) {
        ApprovalTemplate template = templateRepository.findByTemplateId(document.getTemplateId());
        ApprovalCompletedEvent event = new ApprovalCompletedEvent(
                document.getDocId(),
                template.getTemplateKey(),
                document.getDetails(),
                document.getDrafterId(),
                document.getTitle()
        );

        log.info("결재 완료 이벤트 발행 - docId: {}, templateKey: {}",
                document.getDocId(), template.getTemplateKey());

        eventPublisher.publishEvent(event);
    }

    /**
     * 결재 반려 이벤트 발행
     */
    private void publishApprovalRejectedEvent(ApprovalDocument document, String comment) {
        ApprovalTemplate template = templateRepository.findByTemplateId(document.getTemplateId());
        ApprovalRejectedEvent event = new ApprovalRejectedEvent(
                document.getDocId(),
                template.getTemplateKey(),
                document.getDetails(),
                document.getDrafterId(),
                comment
        );

        log.info("결재 반려 이벤트 발행 - docId: {}, templateKey: {}",
                document.getDocId(), template.getTemplateKey());

        eventPublisher.publishEvent(event);
    }

    private void validateApprovalAction(ApprovalActionRequestDTO request) {
        if (!"APPROVE".equals(request.getAction()) && !"REJECT".equals(request.getAction())) {
            throw new IllegalArgumentException("유효하지 않은 결재 액션");
        }

        if ("REJECT".equals(request.getAction()) &&
                (request.getComment() == null || request.getComment().trim().isEmpty())) {
            throw new IllegalArgumentException("반려 시 반려 사유 필수");
        }
    }

    /* ========================================== */
    /* 문서 회수 */
    /* ========================================== */

    /**
     * 결재 대기 중인 문서 회수
     * - INPROGRESS 문서를 DRAFT로 변경
     *
     * @param docId 문서ID
     * @return message 회수 성공 메시지
     */
    @Transactional
    public String cancelDocument(Integer docId) {
        ApprovalDocument document = documentRepository.findById(docId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "문서를 찾을 수 없습니다."));

        if (!"INPROGRESS".equals(document.getDocStatus())) {
            throw new IllegalArgumentException("진행 중인 문서만 회수할 수 있습니다.");
        }

        document.changeStatus("DRAFT");
        log.info("문서 회수 완료 - docId: {}, status: DRAFT", docId);
        documentRepository.save(document);
        return "성공하였습니다.";
    }

    /**
     * 임시저장 문서 삭제
     * - 첨부파일 S3에서 삭제
     * - DB에서 문서 및 관련 데이터 삭제
     *
     * @param docId 문서 ID
     * @return 삭제 성공 메시지
     */
    @Transactional
    public String deleteDocument(Integer docId) {
        try {
            // 1. 첨부파일 삭제 (S3 및 DB)
            deleteAttachments(docId);

            // 2. 결재선 삭제
            lineRepository.deleteByDocId(docId);
            log.info("결재선 삭제 완료 - docId: {}", docId);

            // 3. 참조자 삭제
            referenceRepository.deleteByDocId(docId);
            log.info("참조자 삭제 완료 - docId: {}", docId);

            // 4. 문서 삭제
            documentRepository.deleteById(docId);
            log.info("문서 삭제 완료 - docId: {}", docId);

        } catch (Exception ex) {
            log.error("문서 삭제 실패 - docId: {}", docId, ex);
            throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "삭제실패");
        }

        return "삭제를 성공하였습니다.";
    }
}