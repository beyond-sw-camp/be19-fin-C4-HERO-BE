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
 *
 * History
 * 2025/12/26 (민철) 문서 완료 시간 설정 편의 메서드 추가
 *
 * </pre>
 *
 * @author 민철
 * @version 2.0
 */
@Entity
@Table(name = "tbl_approval_document")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ApprovalDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_id")
    private Integer docId;

    @Column(name = "template_id")
    private Integer templateId;

    @Column(name = "drafter_id")
    private Integer drafterId;

    @Column(name = "doc_no")
    private String docNo;

    private String title;

    // DB의 JSON 타입 컬럼
    @Column(columnDefinition = "json")
    private String details;

    @Column(name = "doc_status")
    private String docStatus; // DRAFT, INPROGRESS, APPROVED, REJECTED

    @Column(name = "end_date")
    private LocalDateTime completedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

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
     */
    public void complete() {
        this.docStatus = "APPROVED";
        this.completedAt = LocalDateTime.now();
    }

    /**
     * 문서 반려 처리
     */
    public void reject() {
        this.docStatus = "REJECTED";
    }

    /**
     * 문서 번호 할당
     */
    public void assignDocNo(String docNo) {
        this.docNo = docNo;
    }
}