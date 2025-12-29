package com.c4.hero.domain.approval.service;

import com.c4.hero.domain.approval.dto.ApprovalLineDTO;
import com.c4.hero.domain.approval.dto.ApprovalReferenceDTO;
import com.c4.hero.domain.approval.dto.request.ApprovalActionRequestDTO;
import com.c4.hero.domain.approval.dto.request.ApprovalRequestDTO;
import com.c4.hero.domain.approval.dto.response.ApprovalActionResponseDTO;
import com.c4.hero.domain.approval.entity.*;
import com.c4.hero.domain.approval.event.ApprovalCompletedEvent;
import com.c4.hero.domain.approval.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <pre>
 * Class Name  : ApprovalCommandService
 * Description : 전자결재 커맨드 관련 서비스 로직 (삽입/수정/삭제)
 *
 * History
 *   2025/12/25 - 민철 CQRS 패턴 적용 및 작성화면 조회 메서드 로직 추가
 *   2025/12/26 - 민철 결재선/참조목록 저장 로직 추가 및 DTO 필드명 수정
 * </pre>
 *
 * @author 민철
 * @version 2.2
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

    private final String UPLOAD_DIR = "C:/hero_uploads/";

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
     * @param status     문서 상태 (DRAFT / PENDING)
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

        // [추가됨] 상신(PENDING) 상태인 경우에만 문서 번호 생성
        if (!"DRAFT".equals(status)) {
            String docNo = generateDocNo(); // 문서 번호 생성 로직 호출
            document.assignDocNo(docNo);      // Setter 혹은 Builder에서 설정
            log.info("문서 번호 생성됨: {}", docNo);
        }
        ApprovalDocument savedDoc = documentRepository.save(document);
        log.info("문서 저장 완료 - docId: {}", savedDoc.getDocId());

        // 2. 결재선 저장 (필드명 수정: approvalLine → lines)
        if (dto.getLines() != null && !dto.getLines().isEmpty()) {
            saveApprovalLines(savedDoc.getDocId(), dto.getLines());
            log.info("결재선 저장 완료 - 결재자 수: {}", dto.getLines().size());
        }

        // 3. 참조자 저장
        if (dto.getReferences() != null && !dto.getReferences().isEmpty()) {
            saveReferences(savedDoc.getDocId(), dto.getReferences());
            log.info("참조자 저장 완료 - 참조자 수: {}", dto.getReferences().size());
        }

        // 4. 첨부파일 저장
        if (files != null && !files.isEmpty()) {
            saveFiles(files, savedDoc);
            log.info("첨부파일 저장 완료 - 파일 수: {}", files.size());
        }

        log.info("문서 생성 완료 - docId: {}", savedDoc.getDocId());
        return savedDoc.getDocId();
    }

    /**
     * 문서 번호 생성 (Format: HERO-yyyy-00001)
     * 동시성 제어를 위해 synchronized 사용 혹은 DB Lock 사용 권장
     */
    private synchronized String generateDocNo() {
        // 1. 현재 년도 구하기
        String currentYear = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        String prefix = "HERO-" + currentYear + "-"; // 예: HERO-2025-

        // 2. 해당 년도의 마지막 문서 번호 조회 (Repository 필요)
        // 예: HERO-2025-00014
        String lastDocNo = documentRepository.findLastDocNoLike(prefix + "%");

        int nextSeq = 1;
        if (lastDocNo != null) {
            // 3. 마지막 번호에서 일련번호 파싱 후 +1
            // HERO-2025-00014 -> "00014" -> 14 -> 15
            String seqStr = lastDocNo.substring(lastDocNo.lastIndexOf("-") + 1);
            nextSeq = Integer.parseInt(seqStr) + 1;
        }

        // 4. 자리수 맞춤 (5자리 0 채움)
        // 15 -> HERO-2025-00015
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
                .drafterId(employeeId)      // 현재 로그인한 사용자 ID
                .title(dto.getTitle())
                .details(dto.getDetails())  // JSON String 그대로 저장
                .docStatus(status)          // DRAFT or INPROGRESS
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
            // seq=1 (기안자)는 자동 승인 처리
            String initialStatus = (lineDTO.getSeq() == 1) ? "APPROVED" : "PENDING";

            ApprovalLine.ApprovalLineBuilder builder = ApprovalLine.builder()
                    .docId(docId)
                    .approverId(lineDTO.getApproverId())
                    .seq(lineDTO.getSeq())
                    .lineStatus(initialStatus);

            // seq=1 (기안자)는 processDate 설정
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

            log.debug("참조자 저장 - referencerId: {}, referencerName: {}",
                    refDTO.getReferencerId(), refDTO.getReferencerName());
        }
    }

    /* ========================================== */
    /* 첨부파일 저장 */
    /* ========================================== */

    /**
     * 첨부파일 저장
     *
     * @param files    첨부 파일 목록
     * @param document 문서 Entity
     */
    private void saveFiles(List<MultipartFile> files, ApprovalDocument document) {
        // 업로드 디렉토리 생성
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            String originalName = file.getOriginalFilename();
            String uuidName = UUID.randomUUID() + "_" + originalName;
            String savePath = UPLOAD_DIR + uuidName;

            try {
                // 실제 파일 저장
                file.transferTo(new File(savePath));

                // DB 메타데이터 저장
                ApprovalAttachment attachment = ApprovalAttachment.builder()
                        .document(document)
                        .originName(originalName)
                        .savePath(savePath)
                        .fileSize(file.getSize())
                        .build();

                attachmentRepository.save(attachment);

                log.debug("첨부파일 저장 - originName: {}, size: {} bytes",
                        originalName, file.getSize());

            } catch (IOException e) {
                log.error("파일 저장 실패: {}", originalName, e);
                throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.");
            }
        }
    }

    /**
     * 결재 처리 (승인/반려)
     *
     * @param request 결재 처리 요청
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
                    .filter(l -> l.getSeq() > 1)  // seq=1(기안자) 제외
                    .allMatch(l -> "APPROVED".equals(l.getLineStatus()));

            if (allApproved) {
                // 최종 승인 완료
                document.complete();

                // 🎉 이벤트 발행 - 다른 도메인에서 후속 처리
                publishApprovalCompletedEvent(document);

                return ApprovalActionResponseDTO.builder()
                        .success(true)
                        .message("최종 승인 완료")
                        .docStatus("APPROVED")
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
     * - 다른 도메인에서 이 이벤트를 수신하여 후속 처리
     *
     * @param document 승인 완료된 문서
     */
    private void publishApprovalCompletedEvent(ApprovalDocument document) {
        ApprovalTemplate template = templateRepository.findByTemplateId(document.getTemplateId());
        ApprovalCompletedEvent event = new ApprovalCompletedEvent(

                document.getDocId(),
                template.getTemplateKey(),     // vacation, overtime, resign 등
                document.getDetails(),          // JSON 데이터
                document.getDrafterId(),
                document.getTitle()
        );

        log.info("결재 완료 이벤트 발행 - docId: {}, templateKey: {}",
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
}