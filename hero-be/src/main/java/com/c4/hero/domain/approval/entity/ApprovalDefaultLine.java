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
 * Description: 기본 결재선 엔티티 클래스
 *              서식별로 미리 설정된 결재선 템플릿 (ReadOnly)
 *              데이터 변경 불가 - 관리자만 설정 가능
 *
 * History
 * 2025/12/24 (민철) 기본 결재선 엔티티 클래스
 * 2026/01/01 (민철) 필드 주석 추가
 *
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */
@Getter
@Entity
@ToString
@Immutable
@Table(name = "tbl_approval_default_line")
public class ApprovalDefaultLine {

    /**
     * 기본 결재선 ID (Primary Key)
     */
    @Id
    @Column(name = "def_line_id")
    private Integer defLineId;

    /**
     * 서식 템플릿 ID (Foreign Key)
     * 어떤 서식에 대한 결재선인지 지정
     */
    @Column(name = "template_id", insertable = false, updatable = false)
    private Integer templateId;

    /**
     * 부서 ID (Foreign Key)
     * - 0: 기안자의 직속 부서장
     * - 양수: 특정 부서의 부서장
     */
    @Column(name = "department_id", insertable = false, updatable = false)
    private Integer departmentId;

    /**
     * 결재 순서
     * - 1: 기안
     * - 2: 1차 결재
     * - 3: 2차 결재
     * - 4: 3차 결재 (최종 결재)
     */
    @Column(insertable = false, updatable = false)
    private Integer seq;

    /**
     * 생성일시
     */
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정일시
     */
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}