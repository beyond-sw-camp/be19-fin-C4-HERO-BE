package com.c4.hero.domain.approval.service;

import com.c4.hero.common.response.PageResponse;
import com.c4.hero.common.s3.S3Service;
import com.c4.hero.domain.approval.dto.ApprovalDefaultLineDTO;
import com.c4.hero.domain.approval.dto.ApprovalDefaultRefDTO;
import com.c4.hero.domain.approval.dto.ApprovalTemplateResponseDTO;
import com.c4.hero.domain.approval.dto.response.*;
import com.c4.hero.domain.approval.entity.*;
import com.c4.hero.domain.approval.mapper.ApprovalMapper;
import com.c4.hero.domain.approval.repository.ApprovalAttachmentRepository;
import com.c4.hero.domain.approval.repository.ApprovalBookmarkRepository;
import com.c4.hero.domain.approval.repository.ApprovalTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <pre>
 * Class Name  : ApprovalQueryService
 * Description :
 * - 전자결재 조회 관련 서비스 로직
 * - 전자결재 단순 조회 -> JPA
 * - 전자결재 복잡한 조회(쿼리) -> MyBatis
 *
 * History
 * 2025/12/15 (민철) 최초 작성
 * 2025/12/19 (민철) ApprovalTemplate.java 문서템플릿 필드명 수정에 의한 getter메서드 수정
 * 2025/12/25 (민철) CQRS 패턴 적용 및 작성화면 조회 메서드 로직 추가
 * 2025/12/26 (민철) 문서함 목록 조회 구현 (PageResponse 사용)
 * 2025/12/26 (민철) 페이지 인덱스 음수 방지 로직 추가
 * 2026/01/01 (민철) 첨부파일 다운로드 URL 생성 추가
 *
 * </pre>
 *
 * @author 민철
 * @version 2.3
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalQueryService {

    private final ApprovalTemplateRepository templateRepository;
    private final ApprovalBookmarkRepository bookmarkRepository;
    private final ApprovalAttachmentRepository attachmentRepository;
    private final ApprovalMapper approvalMapper;
    private final S3Service s3Service;


    /**
     * 모든 결재 양식 조회 (즐겨찾기 여부 포함)
     *
     * @param employeeId 현재 로그인한 사원 ID
     * @return 문서 템플릿 목록
     */
    @Transactional(readOnly = true)
    public List<ApprovalTemplateResponseDTO> getAllTemplates(Integer employeeId) {

        List<ApprovalTemplate> templates =
                templateRepository.findAll();

        List<Integer> bookmarkedIds =
                bookmarkRepository.findTemplateIdsByEmpId(employeeId);

        return templates.stream()
                .map(t -> ApprovalTemplateResponseDTO.builder()
                        .templateId(t.getTemplateId())
                        .templateName(t.getTemplateName())
                        .templateKey(t.getTemplateKey())
                        .category(t.getCategory())
                        .description(t.getDescription())
                        .bookmarking(bookmarkedIds.contains(t.getTemplateId()))
                        .build())
                .collect(Collectors.toList());
    }


    /**
     * 문서함 조회 메소드 (탭별 필터링, 페이지네이션)
     *
     * @param page      페이지 번호 (1부터 시작)
     * @param size      페이지 크기
     * @param tab       탭 구분 (all/que/request/reject/ref/end/draft)
     * @param fromDate  시작일
     * @param toDate    종료일
     * @param sortBy    정렬 기준
     * @param condition 필터 조건
     * @param employeeId 사원 ID
     * @return PageResponse<ApprovalDocumentsResponseDTO> 문서 목록 (페이지 정보 포함)
     */
    public PageResponse<ApprovalDocumentsResponseDTO> getInboxDocuments(
            int page, int size, String tab, String fromDate, String toDate,
            String sortBy, String condition, Integer employeeId) {

        // 페이지 번호 유효성 검증 (최소값 1)
        if (page < 1) {
            page = 1;
        }

        // 페이지 크기 유효성 검증 (최소값 1, 최대값 100)
        if (size < 1) {
            size = 10;
        } else if (size > 100) {
            size = 100;
        }

        // 페이지 오프셋 계산 (프론트에서 1부터 시작, DB는 0부터, PageResponse도 0부터)
        int pageIndex = page - 1;  // 0부터 시작하는 페이지 인덱스
        int offset = pageIndex * size;

        // 탭 유효성 검증
        if (tab == null || tab.isEmpty()) {
            tab = "all";
        }

        // 문서 목록 조회 (탭별 필터링 포함)
        List<ApprovalDocumentsResponseDTO> documents = approvalMapper.selectInboxDocuments(
                employeeId, tab, offset, size, fromDate, toDate, sortBy, condition
        );
        documents.forEach(doc -> {
            if ("INPROGRESS".equals(doc.getDocStatus())) {
                doc.setDocStatus("진행중");
            } else if ("APPROVED".equals(doc.getDocStatus())) {
                doc.setDocStatus("승인완료");
            } else if ("REJECTED".equals(doc.getDocStatus())) {
                doc.setDocStatus("반려");
            } else if ("DRAFT".equals(doc.getDocStatus())) {
                doc.setDocStatus("임시저장");
            }
        });

        // 전체 문서 개수 조회 (탭별 필터링 포함)
        int totalElements = approvalMapper.countInboxDocuments(
                employeeId, tab, fromDate, toDate, sortBy, condition
        );

        // PageResponse.of() 정적 메서드 사용
        return PageResponse.of(documents, pageIndex, size, totalElements);
    }

    /**
     * 문서 상세 조회 메소드
     *
     * @param docId      문서 ID
     * @param employeeId 조회하는 사원 ID (권한 확인용)
     * @return ApprovalDocumentDetailResponseDTO 문서 상세 정보
     */
    @Transactional(readOnly = true)
    public ApprovalDocumentDetailResponseDTO getDocumentDetail(Integer docId, Integer employeeId) {

        // 문서 상세 조회 (MyBatis)
        ApprovalDocumentDetailResponseDTO document = approvalMapper.selectDocumentDetail(docId);

        if (document == null) {
            throw new IllegalArgumentException("해당 문서를 찾을 수 없습니다. docId=" + docId);
        }

        // 결재선 조회
        List<ApprovalLineResponseDTO> lines = approvalMapper.selectApprovalLines(docId);
        document.setLines(lines);

        // 참조자 조회
        List<ApprovalReferenceResponseDTO> references = approvalMapper.selectApprovalReferences(docId);
        document.setReferences(references);

        // 첨부파일 조회 (MyBatis)
        List<ApprovalAttachmentResponseDTO> attachments = approvalMapper.selectApprovalAttachments(docId);

        // 각 첨부파일에 대해 Presigned URL 생성
        if (attachments != null && !attachments.isEmpty()) {
            // JPA로 실제 파일 정보 다시 조회하여 save_path 가져오기
            List<ApprovalAttachment> entities = attachmentRepository.findByDocumentDocId(docId);

            for (ApprovalAttachmentResponseDTO dto : attachments) {
                try {
                    // DTO의 attachmentId와 매칭되는 엔티티 찾기
                    ApprovalAttachment entity = entities.stream()
                            .filter(e -> e.getFileId().equals(dto.getAttachmentId()))
                            .findFirst()
                            .orElse(null);

                    if (entity != null && entity.getSavePath() != null) {
                        String presignedUrl = s3Service.generatePresignedUrl(entity.getSavePath(), 7);
                        dto.setDownloadUrl(presignedUrl);
                        log.debug("Presigned URL 생성 완료 - attachmentId: {}, fileName: {}",
                                dto.getAttachmentId(), dto.getOriginalFilename());
                    } else {
                        log.warn("첨부파일 경로 없음 - attachmentId: {}", dto.getAttachmentId());
                    }
                } catch (Exception e) {
                    log.error("Presigned URL 생성 실패 - attachmentId: {}", dto.getAttachmentId(), e);
                }
            }
        }

        document.setAttachments(attachments);

        return document;
    }

    /**
     * 서식 화면 조회 메서드
     * * @param employeeId 기안자 ID (로그인 사용자)
     * @param templateId 서식 ID
     * @return 서식 정보 + 계산된 결재선/참조자 목록
     */
    @Transactional(readOnly = true)
    public ApprovalTemplateDetailResponseDTO getTemplate(Integer employeeId, Integer templateId) {

        // 1. 서식 기본 정보 조회 (JPA)
        // -> 단순히 서식명, 카테고리 등을 가져옴
        ApprovalTemplate templateEntity = templateRepository.findByTemplateId(templateId);

        if (templateEntity == null) {
            throw new IllegalArgumentException("해당 서식을 찾을 수 없습니다. id=" + templateId);
        }
//        log.info("template detail response: {}", templateEntity);

        // 2. 동적 결재선 조회 (MyBatis)
        // -> 0일 때 직속 부서장 변환 로직은 쿼리 내부에서 처리
        List<ApprovalDefaultLineDTO> lineDTOs = approvalMapper.selectDefaultLines(employeeId, templateId);
//        log.info("template detail response: {}", lineDTOs);

        // 3. 동적 참조자 조회 (MyBatis)
        // -> 0일 때 직속 부서장 변환 로직은 쿼리 내부에서 처리
        List<ApprovalDefaultRefDTO> refDTOs = approvalMapper.selectDefaultReferences(employeeId, templateId);
//        log.info("template detail response: {}", refDTOs);

        // 4. 결과 조립 및 반환
        return ApprovalTemplateDetailResponseDTO.builder()
                .templateId(templateEntity.getTemplateId())
                .templateName(templateEntity.getTemplateName())
                .templateKey(templateEntity.getTemplateKey())
                .category(templateEntity.getCategory())
                .lines(lineDTOs)
                .references(refDTOs)
                .build();
    }
}