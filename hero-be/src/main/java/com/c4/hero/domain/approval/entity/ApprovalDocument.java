package com.c4.hero.domain.approval.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

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
    private Integer templateId; // 편의상 ID로 매핑 (연관관계 매핑 가능)

    @Column(name = "drafter_id")
    private Integer drafterId;

    private String title;

    // DB의 JSON 타입 컬럼
    @Column(columnDefinition = "json")
    private String details;

    @Column(name = "doc_status")
    private String docStatus; // DRAFT, PENDING, APPROVED...

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 상태 변경 편의 메서드
    public void changeStatus(String status) {
        this.docStatus = status;
    }
}