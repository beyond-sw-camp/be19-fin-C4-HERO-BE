package com.c4.hero.domain.settings.entity;

import com.c4.hero.domain.approval.entity.ApprovalTemplate;
import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * <pre>
 * Class Name: SettingsApprovalLine
 * Description: 기본 결재선 정보 엔티티 클래스
 *
 * History
 * 2025/12/19 (민철) 최초작성
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
@Getter
@Entity
@Builder
@Table(name = "tbl_approval_default_line")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SettingsApprovalLine {

    @Id
    @Column(name = "def_line_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer defLineId;

    @JoinColumn(name = "template_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ApprovalTemplate template;

    @Column
    private Integer seq;

    @Column(name = "department_id")
    private Integer departmentId;

//    @CreatedDate
//    @Column(nullable = false, updatable = false)
//    private LocalDateTime createdAt;
//
//    @LastModifiedDate
//    @Column(nullable = false)
//    private LocalDateTime updatedAt;


}
