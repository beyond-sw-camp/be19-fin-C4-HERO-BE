package com.c4.hero.domain.approval.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: ApprovalLine
 * Description: 결재선 엔티티
 *              문서의 결재선 정보 및 각 결재자의 결재 상태를 관리
 *
 * History
 * 2025/12/26 (민철) 결재 처리 편의 메서드 추가
 * 2026/01/01 (민철) 필드 주석 추가
 *
 * </pre>
 *
 * @author 민철
 * @version 2.1
 */
@Entity
@Table(name = "tbl_approval_line")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ApprovalLine {

    /**
     * 결재선 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_id")
    private Integer lineId;

    /**
     * 문서 ID (Foreign Key)
     * 어떤 문서의 결재선인지 지정
     */
    @Column(name = "doc_id")
    private Integer docId;

    /**
     * 결재자 ID (Foreign Key)
     * 이 결재선 단계를 처리할 직원의 ID
     */
    @Column(name = "approver_id")
    private Integer approverId;

    /**
     * 결재 순서
     * - 1: 기안
     * - 2: 1차 결재
     * - 3: 2차 결재
     * - 4: 3차 결재 (최종 결재)
     */
    private int seq;

    /**
     * 결재 상태
     * - PENDING: 대기중
     * - APPROVED: 승인
     * - REJECTED: 반려
     * - DRAFT: 임시저장
     * - INPROGRESS: 진행중
     */
    @Column(name = "line_status")
    private String lineStatus;

    /**
     * 결재 의견
     * 승인 또는 반려 시 작성한 코멘트
     */
    private String comment;

    /**
     * 결재 처리 일시
     * 승인 또는 반려를 처리한 시간
     */
    @Column(name = "process_date")
    private LocalDateTime processDate;

    /* ========================================== */
    /* 편의 메서드 */
    /* ========================================== */

    /**
     * 승인 처리
     * 상태를 APPROVED로 변경하고 처리 시간을 현재 시간으로 설정
     */
    public void approve() {
        this.lineStatus = "APPROVED";
        this.processDate = LocalDateTime.now();
    }

    /**
     * 반려 처리
     * 상태를 REJECTED로 변경하고 반려 사유를 저장
     *
     * @param rejectComment 반려 사유
     */
    public void reject(String rejectComment) {
        this.lineStatus = "REJECTED";
        this.processDate = LocalDateTime.now();
        this.comment = rejectComment;
    }
}