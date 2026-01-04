package com.c4.hero.domain.promotion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: PromotionPlan
 * Description: 승진 계획 정보를 담는 엔티티
 *
 * History
 * 2025-12-19 (이승건) 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
@Builder
@Entity
@Table(name = "tbl_promotion_plan")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PromotionPlan {

    /**
     * 승진 계획 ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_plan_id")
    private Integer promotionPlanId;

    /**
     * 승진 계획명
     */
    @Column(name = "plan_name", nullable = false, length = 50)
    private String planName;

    /**
     * 생성 일시
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 추천 마감일
     */
    @Column(name = "nomination_deadline_at", nullable = false)
    private LocalDate nominationDeadlineAt;

    /**
     * 발령일
     */
    @Column(name = "appointment_at", nullable = false)
    private LocalDate appointmentAt;

    /**
     * 계획 관련 추가 내용
     */
    @Column(name = "plan_content", columnDefinition = "TEXT")
    private String planContent;
}
