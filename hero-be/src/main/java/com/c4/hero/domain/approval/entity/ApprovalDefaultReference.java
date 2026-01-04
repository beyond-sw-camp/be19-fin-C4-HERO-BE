package com.c4.hero.domain.approval.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: ApprovalDefaultReference
 * Description: 기본 참조자 목록 엔티티 클래스
 *              서식별로 미리 설정된 참조자 템플릿 (ReadOnly)
 *              데이터 변경 불가 - 관리자만 설정 가능
 *
 * History
 * 2025/12/24 (민철) 기본 참조목록 엔티티 클래스
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
@Table(name = "tbl_approval_default_reference")
public class ApprovalDefaultReference {

    /**
     * 기본 참조자 ID (Primary Key)
     */
    @Id
    @Column(name = "def_ref_id")
    private Integer defRefId;

    /**
     * 서식 템플릿 ID (Foreign Key)
     * 어떤 서식에 대한 참조자인지 지정
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
     * 생성일시
     */
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}