package com.c4.hero.domain.approval.service;

import com.c4.hero.domain.approval.dto.ApprovalLineDTO;
import com.c4.hero.domain.approval.dto.ApprovalReferenceDTO;
import com.c4.hero.domain.approval.dto.request.ApprovalRequestDTO;
import com.c4.hero.domain.approval.entity.*;
import com.c4.hero.domain.approval.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
        log.info("📝 문서 생성 시작 - employeeId: {}, status: {}", employeeId, status);

        // 1. 문서 본문 저장
        ApprovalDocument document = createApprovalDocument(employeeId, dto, status);
        ApprovalDocument savedDoc = documentRepository.save(document);
        log.info("✅ 문서 저장 완료 - docId: {}", savedDoc.getDocId());

        // 2. 결재선 저장 (✅ 필드명 수정: approvalLine → lines)
        if (dto.getLines() != null && !dto.getLines().isEmpty()) {
            saveApprovalLines(savedDoc.getDocId(), dto.getLines());
            log.info("✅ 결재선 저장 완료 - 결재자 수: {}", dto.getLines().size());
        }

        // 3. 참조자 저장
        if (dto.getReferences() != null && !dto.getReferences().isEmpty()) {
            saveReferences(savedDoc.getDocId(), dto.getReferences());
            log.info("✅ 참조자 저장 완료 - 참조자 수: {}", dto.getReferences().size());
        }

        // 4. 첨부파일 저장
        if (files != null && !files.isEmpty()) {
            saveFiles(files, savedDoc);
            log.info("✅ 첨부파일 저장 완료 - 파일 수: {}", files.size());
        }

        log.info("🎉 문서 생성 완료 - docId: {}", savedDoc.getDocId());
        return savedDoc.getDocId();
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
                .templateId(templateEntity.getTemplateId())              // TODO: dto.getFormType()으로 템플릿 ID 조회 로직 필요
                .drafterId(employeeId)      // ✅ 현재 로그인한 사용자 ID
                .title(dto.getTitle())
                .details(dto.getDetails())  // JSON String 그대로 저장
                .docStatus(status)          // DRAFT or PENDING
                .build();
    }

    /* ========================================== */
    /* 결재선 저장 */
    /* ========================================== */

    /**
     * 결재선 저장
     *
     * @param docId        문서 ID
     * @param lines        결재선 DTO 목록
     */
    private void saveApprovalLines(Integer docId, List<ApprovalLineDTO> lines) {
        for (ApprovalLineDTO lineDTO : lines) {
            ApprovalLine line = ApprovalLine.builder()
                    .docId(docId)
                    .approverId(lineDTO.getApproverId())
                    .seq(lineDTO.getSeq())
//                    .type("APPROVER")           // 기본값: 결재자
                    .lineStatus("PENDING")      // 초기 상태: 대기
                    .build();

            lineRepository.save(line);

            log.debug("📌 결재선 저장 - seq: {}, approverId: {}, approverName: {}",
                    lineDTO.getSeq(), lineDTO.getApproverId(), lineDTO.getApproverName());
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

            log.debug("📌 참조자 저장 - referencerId: {}, referencerName: {}",
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

                log.debug("📌 첨부파일 저장 - originName: {}, size: {} bytes",
                        originalName, file.getSize());

            } catch (IOException e) {
                log.error("❌ 파일 저장 실패: {}", originalName, e);
                throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.");
            }
        }
    }
}