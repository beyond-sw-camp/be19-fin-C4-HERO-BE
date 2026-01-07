package com.c4.hero.domain.approval.service;

import com.c4.hero.common.response.PageResponse;
import com.c4.hero.common.s3.S3Service;
import com.c4.hero.domain.approval.dto.ApprovalDefaultLineDTO;
import com.c4.hero.domain.approval.dto.ApprovalDefaultRefDTO;
import com.c4.hero.domain.approval.dto.response.ApprovalTemplateResponseDTO;
import com.c4.hero.domain.approval.dto.response.*;
import com.c4.hero.domain.approval.entity.*;
import com.c4.hero.domain.approval.exception.ApprovalDocumentNotFoundException;
import com.c4.hero.domain.approval.exception.ApprovalTemplateNotFoundException;
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
 * Description : 전자결재 조회 관련 서비스 로직 (CQRS의 Query 담당)
 *               단순 조회는 JPA, 복잡한 조회(조인 등)는 MyBatis 사용
 *
 * 주요 기능:
 *   - 서식 목록 조회 (즐겨찾기 포함)
 *   - 문서함 목록 조회 (페이징, 탭별 필터링)
 *   - 문서 상세 조회 (결재선, 참조자, 첨부파일 포함)
 *   - 서식 상세 조회 (자동 결재선/참조자 계산)
 *
 * History
 * 2025/12/15 (민철) 최초 작성
 * 2025/12/19 (민철) ApprovalTemplate.java 문서템플릿 필드명 수정에 의한 getter메서드 수정
 * 2025/12/25 (민철) CQRS 패턴 적용 및 작성화면 조회 메서드 로직 추가
 * 2025/12/26 (민철) 문서함 목록 조회 구현 (PageResponse 사용)
 * 2025/12/26 (민철) 페이지 인덱스 음수 방지 로직 추가
 * 2026/01/01 (민철) 첨부파일 다운로드 URL 생성 추가
 * 2026/01/03 (민철) 메서드 주석 개선
 *
 * </pre>
 *
 * @author 민철
 * @version 2.4
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
     * <pre>
     * 처리 흐름:
     * 1. JPA로 전체 서식 목록 조회
     * 2. 현재 사용자의 즐겨찾기 목록 조회 (성능 최적화를 위해 ID만 조회)
     * 3. 각 서식에 즐겨찾기 여부 설정
     * </pre>
     * @param employeeId 현재 로그인한 사원 ID
     * @return 문서 템플릿 목록 (즐겨찾기 여부 포함)
     */
    @Transactional(readOnly = true)
    public List<ApprovalTemplateResponseDTO> getAllTemplates(Integer employeeId) {

        List<ApprovalTemplate> templates = templateRepository.findAll();

        List<Integer> bookmarkedIds = bookmarkRepository.findTemplateIdsByEmpId(employeeId);

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
     * <pre>
     * 탭 구분:
     * - all: 전체 문서
     * - que: 대기 (내가 결재할 차례인 문서)
     * - request: 요청 (내가 기안한 문서)
     * - reject: 반려된 문서
     * - ref: 참조 (내가 참조자로 지정된 문서)
     * - end: 승인 완료
     * - draft: 임시저장
     *
     * 페이지 처리:
     * - 프론트엔드: 1부터 시작
     * - 백엔드 내부: 0부터 시작 (pageIndex)
     * - DB 오프셋: pageIndex * size
     *
     * 유효성 검증:
     * - 페이지 번호 최소값 1
     * - 페이지 크기 최소값 1, 최대값 100
     *
     * 상태값 변환:
     * - INPROGRESS → 진행중
     * - APPROVED → 승인완료
     * - REJECTED → 반려
     * - DRAFT → 임시저장
     * </pre>
     * @param page       페이지 번호 (1부터 시작)
     * @param size       페이지 크기
     * @param tab        탭 구분 (all/que/request/reject/ref/end/draft)
     * @param fromDate   시작일
     * @param toDate     종료일
     * @param sortBy     정렬 기준
     * @param condition  필터 조건
     * @param employeeId 사원 ID
     * @return PageResponse<ApprovalDocumentsResponseDTO> 문서 목록 (페이지 정보 포함)
     */
    public PageResponse<ApprovalDocumentsResponseDTO> getInboxDocuments(
            int page, int size, String tab, String fromDate, String toDate,
            String sortBy, String condition, Integer employeeId) {

        if (page < 1) {
            page = 1;
        }

        if (size < 1) {
            size = 10;
        } else if (size > 100) {
            size = 100;
        }

        int pageIndex = page - 1;
        int offset = pageIndex * size;

        if (tab == null || tab.isEmpty()) {
            tab = "all";
        }

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

        int totalElements = approvalMapper.countInboxDocuments(
                employeeId, tab, fromDate, toDate, sortBy, condition
        );

        return PageResponse.of(documents, pageIndex, size, totalElements);
    }

    /**
     * 문서 상세 조회 메소드
     * <pre>
     * 처리 흐름:
     * 1. MyBatis로 문서 기본 정보 조회
     * 2. 결재선 목록 조회
     * 3. 참조자 목록 조회
     * 4. 첨부파일 목록 조회 (MyBatis)
     * 5. 각 첨부파일에 대해 S3 Presigned URL 생성
     *    - JPA로 실제 파일 정보 다시 조회 (save_path 가져오기)
     *    - attachmentId로 매칭하여 Presigned URL 생성 (7일 유효)
     *
     * S3 Presigned URL:
     * - 임시 다운로드 URL (7일간 유효)
     * - 인증 없이 파일 다운로드 가능
     * - URL 만료 후에는 재생성 필요
     * </pre>
     * @param docId      문서 ID
     * @param employeeId 조회하는 사원 ID (권한 확인용)
     * @return ApprovalDocumentDetailResponseDTO 문서 상세 정보
     * @throws IllegalArgumentException 문서를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public ApprovalDocumentDetailResponseDTO getDocumentDetail(Integer docId, Integer employeeId) {

        ApprovalDocumentDetailResponseDTO document = approvalMapper.selectDocumentDetail(docId);

        if (document == null) {
            throw new ApprovalDocumentNotFoundException(docId);
        }

        List<ApprovalLineResponseDTO> lines = approvalMapper.selectApprovalLines(docId);
        document.setLines(lines);

        List<ApprovalReferenceResponseDTO> references = approvalMapper.selectApprovalReferences(docId);
        document.setReferences(references);

        List<ApprovalAttachmentResponseDTO> attachments = approvalMapper.selectApprovalAttachments(docId);

        if (attachments != null && !attachments.isEmpty()) {
            List<ApprovalAttachment> entities = attachmentRepository.findByDocumentDocId(docId);

            for (ApprovalAttachmentResponseDTO dto : attachments) {
                try {
                    ApprovalAttachment entity = entities.stream()
                            .filter(e -> e.getFileId().equals(dto.getAttachmentId()))
                            .findFirst()
                            .orElse(null);

                    if (entity != null && entity.getSavePath() != null) {
                        String presignedUrl = s3Service.generatePresignedUrl(entity.getSavePath(), 7);
                        dto.setDownloadUrl(presignedUrl);
                    } else {
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
     * 서식 화면 조회 메소드 (작성 화면 진입 시 사용)
     * <pre>
     * 처리 흐름:
     * 1. JPA로 서식 기본 정보 조회 (서식명, 카테고리 등)
     * 2. MyBatis로 동적 결재선 조회
     *    - department_id가 0인 경우 기안자의 직속 부서장으로 변환
     *    - 기안자의 부서/직급에 따라 자동으로 결재선 계산
     * 3. MyBatis로 동적 참조자 조회
     *    - department_id가 0인 경우 기안자의 직속 부서장으로 변환
     * 4. 결과 조립 및 반환
     *
     * 동적 결재선/참조자:
     * - 서식 템플릿에 미리 설정된 규칙에 따라 자동으로 계산됨
     * - 기안자의 소속 부서, 직급 등을 고려하여 결재선이 자동 구성됨
     * </pre>
     * @param employeeId 기안자 ID (로그인 사용자)
     * @param templateId 서식 ID
     * @return ApprovalTemplateDetailResponseDTO 서식 정보 + 계산된 결재선/참조자 목록
     * @throws IllegalArgumentException 서식을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public ApprovalTemplateDetailResponseDTO getTemplate(Integer employeeId, Integer templateId) {

        ApprovalTemplate templateEntity = templateRepository.findByTemplateId(templateId);

        if (templateEntity == null) {
            throw new ApprovalTemplateNotFoundException(templateId);
        }

        List<ApprovalDefaultLineDTO> lineDTOs = approvalMapper.selectDefaultLines(employeeId, templateId);

        List<ApprovalDefaultRefDTO> refDTOs = approvalMapper.selectDefaultReferences(employeeId, templateId);

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