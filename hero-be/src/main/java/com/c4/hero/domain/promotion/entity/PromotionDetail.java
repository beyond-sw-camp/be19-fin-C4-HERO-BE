package com.c4.hero.domain.promotion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: PromotionDetail
 * Description: 승진 상세 계획 정보를 담는 엔티티
 *
 * History
 * 2025-12-19 (이승건) 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
@Entity
@Table(name = "tbl_promotion_detail")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDetail {

    /**
     * 승진 상세 계획 ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_detail_id")
    private Integer promotionDetailId;

    /**
     * 상위 승진 계획
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_plan_id", nullable = false)
    private PromotionPlan promotionPlan;

    /**
     * 대상 부서 ID
     */
    @Column(name = "department_id", nullable = false)
    private Integer departmentId;

    /**
     * 대상 직급 ID
     */
    @Column(name = "grade_id", nullable = false)
    private Integer gradeId;

    /**
     * 생성 일시
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 승진 TO (인원수)
     */
    @Column(name = "quota_count")
    private Integer quotaCount;
}
