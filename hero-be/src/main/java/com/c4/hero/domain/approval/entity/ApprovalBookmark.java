package com.c4.hero.domain.approval.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * Class Name  : ApprovalBookmark
 * Description : 전자결재 문서 템플릿 즐겨찾기 정보를 관리하는 엔티티
 *
 * History
 * 2025/12/15 (변민철) 최초 작성
 * </pre>
 *
 * @author 변민철
 * @version 1.0
 */
@Entity
@Table(name = "tbl_approval_bookmark")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ApprovalBookmark {

    /** 즐겨찾기 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Integer bookmarkId;

    /** 사원 ID */
    @Column(name = "emp_id", nullable = false)
    private Integer empId;

    /** 문서 템플릿 ID */
    @Column(name = "template_id", nullable = false)
    private Integer templateId;
}