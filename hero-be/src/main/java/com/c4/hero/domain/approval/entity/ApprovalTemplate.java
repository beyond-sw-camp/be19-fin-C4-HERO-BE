package com.c4.hero.domain.approval.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <pre>
 * Class Name  : ApprovalTemplate
 * Description : 전자결재 문서 양식(템플릿)을 관리하는 엔티티
 *               각 서식의 기본 정보 및 기본 결재선/참조자 템플릿을 관리
 *
 * History
 * 2025/12/15 (민철) 최초 작성
 * 2025/12/19 (민철) AccessLevel 변경: 테스트코드에서 생성자접근 가능하도록 수정
 * 2026/01/01 (민철) 필드 주석 추가
 * </pre>
 *
 * @author 민철
 * @version 1.2
 */
@Entity
@Getter
@Builder
@ToString
@Table(name = "tbl_approval_form_template")
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalTemplate {

    /**
     * 서식 템플릿 ID (Primary Key)
     */
    @Id
    private Integer templateId;

    /**
     * 서식 템플릿명
     * 예: 휴가신청서, 초과근무신청서, 사직서, 인사발령신청서, 급여인상신청서
     */
    @Column(insertable = false, updatable = false)
    private String templateName;

    /**
     * 서식 템플릿 키
     * 프론트엔드에서 서식 유형을 구분하기 위한 고유 키
     * 예: vacation, overtime, resign, personnel, payroll
     */
    @Column(insertable = false, updatable = false)
    private String templateKey;

    /**
     * 문서 분류
     * 예: 근태, 인사, 급여
     */
    @Column(insertable = false, updatable = false)
    private String category;

    /**
     * 서식 설명
     * 서식 사용 방법 또는 용도에 대한 상세 설명
     */
    @Column(insertable = false, updatable = false)
    private String description;

    /**
     * 생성일시
     */
    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정일시
     */
    @Column(insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    /**
     * 기본 결재선 목록 (One-to-Many)
     * 이 서식에 설정된 기본 결재선 템플릿
     */
    @JoinColumn(name = "template_id")
    @OneToMany(fetch = FetchType.LAZY)
    private List<ApprovalDefaultLine> lines;

    /**
     * 기본 참조자 목록 (One-to-Many)
     * 이 서식에 설정된 기본 참조자 템플릿
     */
    @JoinColumn(name = "template_id")
    @OneToMany(fetch = FetchType.LAZY)
    private List<ApprovalDefaultReference> references;
}