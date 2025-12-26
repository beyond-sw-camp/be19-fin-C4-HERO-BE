package com.c4.hero.domain.approval.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: ApprovalLine
 * Description: 결재선 엔티티
 *
 * History
 * 2025/12/26 (민철) 결재 처리 편의 메서드 추가
 *
 * </pre>
 *
 * @author 민철
 * @version 2.0
 */
@Entity
@Table(name = "tbl_approval_line")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ApprovalLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_id")
    private Integer lineId;

    @Column(name = "doc_id")
    private Integer docId; // 연관관계 매핑 대신 심플하게 ID만 저장

    @Column(name = "approver_id")
    private Integer approverId;

    private int seq;

    // 중요: 결재(APPROVER)인지 합의(AGREEMENT)인지 구분
//    @Column(name = "type")
//    private String type;

    @Column(name = "line_status")
    private String lineStatus; // PENDING, APPROVED, REJECTED

    private String comment;

    @Column(name = "process_date")
    private LocalDateTime processDate;

    /* ========================================== */
    /* 편의 메서드 */
    /* ========================================== */

    /**
     * 승인 처리
     */
    public void approve() {
        this.lineStatus = "APPROVED";
        this.processDate = LocalDateTime.now();
    }

    /**
     * 반려 처리
     * @param rejectComment 반려 사유
     */
    public void reject(String rejectComment) {
        this.lineStatus = "REJECTED";
        this.processDate = LocalDateTime.now();
        this.comment = rejectComment;
    }
}