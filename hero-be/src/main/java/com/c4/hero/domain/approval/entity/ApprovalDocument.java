package com.c4.hero.domain.approval.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: ApprovalDocument
 * Description: 결재 문서 엔티티
 *              전자결재 시스템의 핵심 엔티티로, 모든 결재 문서 정보를 관리
 *
 * History
 * 2025/12/26 (민철) 문서 완료 시간 설정 편의 메서드 추가
 * 2026/01/01 (민철) 필드 주석 추가
 *
 * </pre>
 *
 * @author 민철
 * @version 2.1
 */
@Entity
@Table(name = "tbl_approval_document")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ApprovalDocument {

    /**
     * 문서 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_id")
    private Integer docId;

    /**
     * 서식 템플릿 ID (Foreign Key)
     * 어떤 서식으로 작성된 문서인지 지정
     */
    @Column(name = "template_id")
    private Integer templateId;

    /**
     * 기안자 ID (Foreign Key)
     * 문서를 작성한 직원의 ID
     */
    @Column(name = "drafter_id")
    private Integer drafterId;

    /**
     * 문서 번호
     * 형식: DOC-2025-001
     * 상신 시 자동 생성 (임시저장 시에는 null)
     */
    @Column(name = "doc_no")
    private String docNo;

    /**
     * 문서 제목
     */
    private String title;

    /**
     * 서식별 상세 데이터 (JSON 타입)
     * 각 서식 유형에 따라 다른 구조의 JSON 데이터 저장
     * 예: 휴가신청서, 초과근무신청서, 사직서 등의 상세 정보
     */
    @Column(columnDefinition = "json")
    private String details;

    /**
     * 문서 상태
     * - DRAFT: 임시저장
     * - INPROGRESS: 진행중 (결재 대기)
     * - APPROVED: 승인완료
     * - REJECTED: 반려
     */
    @Column(name = "doc_status")
    private String docStatus;

    /**
     * 문서 완료 일시
     * 최종 승인 또는 반려된 시간
     */
    @Column(name = "end_date")
    private LocalDateTime completedAt;

    /**
     * 생성일시
     * 문서가 처음 생성된 시간 (자동 설정)
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정일시
     * 문서가 마지막으로 수정된 시간 (자동 갱신)
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /* ========================================== */
    /* 편의 메서드 */
    /* ========================================== */

    /**
     * 문서 상태 변경
     * @param status 변경할 상태
     */
    public void changeStatus(String status) {
        this.docStatus = status;
    }

    /**
     * 문서 승인 완료 처리
     * 상태를 APPROVED로 변경하고 완료 시간을 현재 시간으로 설정
     */
    public void complete() {
        this.docStatus = "APPROVED";
        this.completedAt = LocalDateTime.now();
    }

    /**
     * 문서 반려 처리
     * 상태를 REJECTED로 변경
     */
    public void reject() {
        this.docStatus = "REJECTED";
    }

    /**
     * 문서 번호 할당
     * 임시저장에서 상신으로 전환 시 호출
     * @param docNo 할당할 문서 번호
     */
    public void assignDocNo(String docNo) {
        this.docNo = docNo;
    }

    /**
     * 제목 수정
     * @param title 새로운 제목
     */
    public void updateTitle(String title) {
        this.title = title;
    }

    /**
     * 상세 내용 수정
     * @param details 새로운 상세 내용 (JSON String)
     */
    public void updateDetails(String details) {
        this.details = details;
    }
}