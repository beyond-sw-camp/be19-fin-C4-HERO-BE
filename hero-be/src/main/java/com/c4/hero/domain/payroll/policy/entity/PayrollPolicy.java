package com.c4.hero.domain.payroll.policy.entity;

import com.c4.hero.domain.payroll.common.type.PolicyStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name : PayrollPolicy
 * Description : 급여 정책(Payroll Policy)을 관리하는 핵심 엔티티
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Entity
@Table(name = "tbl_payroll_policy")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PayrollPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Integer policyId;

    @Column(nullable = false, length = 100)
    private String policyName;

    /** 정책 상태 (DRAFT / ACTIVE / EXPIRED) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PolicyStatus status;

    @Column(name = "salary_month_from", nullable = false, length = 7)
    private String salaryMonthFrom;

    @Column(name = "salary_month_to", length = 7)
    private String salaryMonthTo;

    @Column(length = 1)
    private String activeYn; // Y/N (DDL에 있다면)

    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;

    @Column(name="updated_at", nullable=false)
    private LocalDateTime updatedAt;

    /**
     * 엔티티 최초 저장 시 기본값 세팅
     *  - 상태: DRAFT
     *  - 활성 여부: Y
     *  - 생성/수정 시각 자동 설정
     */
    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = PolicyStatus.DRAFT;
        if (this.activeYn == null) this.activeYn = "Y";
    }

    /**
     * 엔티티 수정 시 수정 시각 갱신
     */
    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 정책 활성화
     *  - 상태를 ACTIVE로 전환
     *  - 적용 시작/종료 급여월을 확정
     */
    public void activate(String from, String to) {
        this.status = PolicyStatus.ACTIVE;
        this.salaryMonthFrom = from;
        this.salaryMonthTo = to;
        this.activeYn = "Y";
    }

    /**
     * 정책 만료 처리
     *  - 상태를 EXPIRED로 전환
     *  - 더 이상 급여 계산에 사용되지 않음
     */
    public void expire() {
        this.status = PolicyStatus.EXPIRED;
        this.activeYn = "N";
    }

    /**
     * 정책 기본 정보 수정
     * - null 또는 빈 값은 기존 값을 유지
     * - 상태 전이는 포함하지 않음
     */
    public void update(String name, String from, String to, String activeYn) {
        if (name != null && !name.isBlank()) this.policyName = name;
        if (from != null && !from.isBlank()) this.salaryMonthFrom = from;
        // to는 null 허용(무기한)
        if (to != null || this.salaryMonthTo != null) this.salaryMonthTo = to;
        if (activeYn != null && !activeYn.isBlank()) this.activeYn = activeYn;
    }
}