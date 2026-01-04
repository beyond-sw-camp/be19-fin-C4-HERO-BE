package com.c4.hero.domain.approval.dto.response;

import lombok.Data;
import java.util.List;

/**
 * <pre>
 * Class Name: ApprovalDocumentDetailResponseDTO
 * Description: 문서 상세 조회 응답 DTO
 *              결재 문서의 모든 상세 정보를 포함 (문서 정보, 결재선, 참조자, 첨부파일)
 *
 * History
 * 2025/12/26 (민철) 최초작성
 * 2026/01/01 (민철) 필드 주석 추가
 *
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */

@Data
public class ApprovalDocumentDetailResponseDTO {

    /**
     * 문서 ID (Primary Key)
     */
    private Integer docId;

    /**
     * 문서 번호
     * 예: DOC-2025-001
     */
    private String docNo;

    /**
     * 문서 상태
     * - DRAFT: 임시저장
     * - INPROGRESS: 진행중
     * - APPROVED: 승인완료
     * - REJECTED: 반려
     */
    private String docStatus;

    /**
     * 서식 템플릿 ID
     */
    private Integer templateId;

    /**
     * 서식 템플릿명
     * 예: 휴가신청서, 초과근무신청서
     */
    private String templateName;

    /**
     * 서식 템플릿 키
     * 예: vacation, overtime
     */
    private String templateKey;

    /**
     * 문서 분류
     * 예: 근태, 인사, 급여
     */
    private String category;

    /**
     * 문서 제목
     */
    private String title;

    /**
     * 기안자 ID
     */
    private Integer drafterId;

    /**
     * 기안자 이름
     */
    private String drafter;

    /**
     * 기안자 부서
     */
    private String drafterDept;

    /**
     * 기안자 직급
     */
    private String drafterGrade;

    /**
     * 기안일
     * 형식: yyyy-MM-dd
     */
    private String draftDate;

    /**
     * 상신일시
     * 형식: yyyy-MM-dd HH:mm:ss
     */
    private String submittedAt;

    /**
     * 완료일시 (승인 또는 반려 완료 시간)
     * 형식: yyyy-MM-dd HH:mm:ss
     */
    private String completedAt;

    /**
     * 서식별 상세 데이터 (JSON String)
     * 각 서식 유형에 따라 다른 구조의 JSON 데이터
     */
    private String details;

    /**
     * 결재선 목록
     * seq 순서대로 정렬됨
     */
    private List<ApprovalLineResponseDTO> lines;

    /**
     * 참조자 목록
     */
    private List<ApprovalReferenceResponseDTO> references;

    /**
     * 첨부파일 목록
     */
    private List<ApprovalAttachmentResponseDTO> attachments;
}