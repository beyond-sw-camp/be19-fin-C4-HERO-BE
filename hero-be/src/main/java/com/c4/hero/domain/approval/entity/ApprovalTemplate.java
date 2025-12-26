package com.c4.hero.domain.approval.entity;

/**
 * <pre>
 * Class Name  : ApprovalTemplate
 * Description : 전자결재 문서 양식(템플릿)을 관리하는 엔티티
 *
 * History
 * 2025/12/15 (민철) 최초 작성
 * 2025/12/19 민철 AccessLevel 변경: 테스트코드에서 생성자접근 가능하도록 수정.
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@ToString
@Table(name = "tbl_approval_form_template")
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalTemplate {

    @Id
    private Integer templateId;

    @Column(insertable = false, updatable = false)
    private String templateName;

    @Column(insertable = false, updatable = false)
    private String templateKey;

    @Column(insertable = false, updatable = false)
    private String category;

    @Column(insertable = false, updatable = false)
    private String description;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @JoinColumn(name = "template_id")
    @OneToMany(fetch = FetchType.LAZY)
    private List<ApprovalDefaultLine> lines;

    @JoinColumn(name = "template_id")
    @OneToMany(fetch = FetchType.LAZY)
    private List<ApprovalDefaultReference> references;
}