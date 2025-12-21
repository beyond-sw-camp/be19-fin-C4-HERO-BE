package com.c4.hero.domain.approval.entity;
/**
 * <pre>
 * Class Name  : ApprovalFormTemplate
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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_approval_form_template")
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
public class ApprovalFormTemplate {

    /** 문서 템플릿 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id")
    private Integer templateId;

    /** 템플릿 생성자 사원 ID */
    @Column(nullable = false)
    private String templateName;

    /** 문서 카테고리(영어) */
    @Column(name = "template_key", nullable = false, unique = true)
    private String templateKey;

    /** 문서 카테고리(한글) */
    @Column(nullable = false)
    private String category;

    /** 문서 서식 설명 */
    private String description;

    /** 문서 서식 생성일시 */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** 문서 서식 수정일시 */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}