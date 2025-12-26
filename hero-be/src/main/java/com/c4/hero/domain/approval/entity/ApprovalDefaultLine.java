package com.c4.hero.domain.approval.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;
/**
 * <pre>
 * Class Name: ApprovalDefaultLine
 * Description: 기본 결재선 엔티티 클래스, ReadOnly 클래스, 데이터 변동 x
 *
 * History
 * 2025/12/24 (민철) 기본 결재선 엔티티 클래스
 *
 * </pre>
 *
 * @author 민철
 * @version
 */
@Getter
@Entity
@ToString
@Immutable
@Table(name = "tbl_approval_default_line")
public class ApprovalDefaultLine {

    @Id
    @Column(name = "def_line_id")
    private Integer defLineId;

    @Column(name = "template_id", insertable = false, updatable = false)
    private Integer templateId;

    @Column(name = "department_id", insertable = false, updatable = false)
    private Integer departmentId;

    @Column(insertable = false, updatable = false)
    private Integer seq;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}