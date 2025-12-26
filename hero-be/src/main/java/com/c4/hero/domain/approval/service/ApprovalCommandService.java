package com.c4.hero.domain.approval.service;

import com.c4.hero.domain.approval.dto.request.ApprovalRequestDTO;
import com.c4.hero.domain.approval.entity.ApprovalAttachment;
import com.c4.hero.domain.approval.entity.ApprovalBookmark;
import com.c4.hero.domain.approval.entity.ApprovalDocument;
import com.c4.hero.domain.approval.repository.ApprovalAttachmentRepository;
import com.c4.hero.domain.approval.repository.ApprovalBookmarkRepository;
import com.c4.hero.domain.approval.repository.ApprovalDocumentRepository;
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
 * Description :
 * - 전자결재 커맨드 관련 서비스 로직 (삽입/수정/삭제)
 *
 * History
 * 2025/12/25 (민철) CQRS 패턴 적용 및 작성화면 조회 메서드 로직 추가
 *
 * </pre>
 *
 * @author 민철
 * @version 2.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalCommandService {

    private final ApprovalDocumentRepository documentRepository;
    private final ApprovalAttachmentRepository attachmentRepository;
    private final ApprovalBookmarkRepository bookmarkRepository;
    private final String UPLOAD_DIR = "C:/hero_uploads/";


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
            return true; // 생성됨 즐겨찾기 등록
        }
    }


    /**
     * 문서 생성 (임시저장 or 상신)
     *
     * @param dto    문서 생성 요청 DTO
     * @param files  첨부 파일 목록
     * @param status 문서 상태 (DRAFT / PENDING)
     * @return 생성된 문서 ID
     */
    @Transactional
    public Integer createDocument(Integer employeeId, ApprovalRequestDTO dto, List<MultipartFile> files, String status
    ) {

        // 1. 문서 Entity 생성 및 저장
        ApprovalDocument document = ApprovalDocument.builder()
                .templateId(1) // TODO: dto.getFormType()으로 템플릿 ID 조회 로직 필요
                .drafterId(1)  // TODO: SecurityContext에서 현재 로그인한 사용자 ID 가져오기
                .title(dto.getTitle())
                .details(dto.getDetails()) // JSON String 그대로 저장
                .docStatus(status)         // DRAFT or PENDING
                .build();

        ApprovalDocument savedDoc = documentRepository.save(document);

        // 2. 파일 처리 (파일이 있는 경우에만)
        if (files != null && !files.isEmpty()) {
            saveFiles(files, savedDoc);
        }

        // 3. 결재선 저장 로직 (ApprovalLineRepository 이용) - 생략됨
        // snapshotApprovalLine(savedDoc, dto.getApprovalLine());

        return savedDoc.getDocId();
    }

    private void saveFiles(
            List<MultipartFile> files,
            ApprovalDocument document
    ) {

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

            } catch (IOException e) {
                log.error("파일 저장 실패: {}", originalName, e);
                throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.");
            }
        }
    }
}
