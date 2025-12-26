package com.c4.hero.domain.approval.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
/**
 * <pre>
 * Class Name: 
 * Description: 
 *
 * History
 * 2025/12/ (민철) 
 * 
 * </pre>
 *
 * @author 민철
 * @version 
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
}