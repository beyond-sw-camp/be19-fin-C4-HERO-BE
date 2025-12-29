package com.c4.hero.domain.payroll.policy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name : BatchPolicySnapshot
 * Description : 급여 배치 실행 시점의 급여 정책 스냅샷 엔티티
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Entity
@Table(
        name = "tbl_payroll_batch_policy_snapshot",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_batch_snapshot", columnNames = {"batch_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BatchPolicySnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "snapshot_id")
    private Integer snapshotId;

    @Column(name = "batch_id", nullable = false)
    private Integer batchId;

    @Column(name = "policy_id", nullable = false)
    private Integer policyId;

    @Column(name = "salary_month", nullable = false, length = 7)
    private String salaryMonth;

    @Lob
    @Column(name = "snapshot_json", nullable = false)
    private String snapshotJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 엔티티 최초 저장 시 생성 시각 자동 세팅
     */
    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
