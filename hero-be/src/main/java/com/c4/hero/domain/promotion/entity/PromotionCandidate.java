package com.c4.hero.domain.promotion.entity;

import com.c4.hero.domain.employee.entity.Employee;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: PromotionCandidate
 * Description: 승진 후보자 정보를 담는 엔티티
 *
 * History
 * 2025/12/19 (승건) 최초 작성
 * 2025/12/22 (승건) 추천 및 추천 취소 메서드 추가
 * </pre>
 *
 * @author 승건
 * @version 1.1
 */
@Entity
@Table(name = "tbl_promotion_candidate")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionCandidate {

    /**
     * 후보자 ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_id")
    private Integer candidateId;

    /**
     * 상위 승진 상세 계획
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_detail_id", nullable = false)
    private PromotionDetail promotionDetail;

    /**
     * 후보자 직원 정보
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    /**
     * 추천인 직원 정보
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nominator_id")
    private Employee nominator;

    /**
     * 추천 사유
     */
    @Column(name = "nomination_reason", columnDefinition = "TEXT")
    private String nominationReason;

    /**
     * 승인 여부
     */
    @Column(name = "is_approved")
    private Boolean isApproved;

    /**
     * 반려 사유
     */
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    /**
     * 후보자 등록 당시의 평가 점수
     */
    @Column(name = "evaluation_point", nullable = false)
    private Integer evaluationPoint;

    /**
     * 후보자를 추천합니다.
     *
     * @param nominator 추천인
     * @param reason    추천 사유
     */
    public void nominate(Employee nominator, String reason) {
        this.nominator = nominator;
        this.nominationReason = reason;
    }

    /**
     * 후보자 추천을 취소합니다.
     */
    public void cancelNomination() {
        this.nominator = null;
        this.nominationReason = null;
    }
}
