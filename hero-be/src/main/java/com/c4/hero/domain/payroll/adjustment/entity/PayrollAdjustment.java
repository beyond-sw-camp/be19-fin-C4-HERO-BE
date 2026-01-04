package com.c4.hero.domain.payroll.adjustment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
 * Entity Name : PayrollAdjustment
 * Description : 급여 조정 내역 엔티티
 *
 * History
 *  2025/12/31 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Entity
@Table(name = "tbl_payroll_adjustment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PayrollAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adjustment_id")
    private Integer adjustmentId;

    @Column(nullable = false, length = 255)
    private String reason;

    @Column(nullable = false, length = 1)
    private String sign;

    @Column(nullable = false)
    private Integer amount;

    @Column(name = "effective_month", nullable = false, length = 7)
    private String effectiveMonth;

    @Column(nullable = false, length = 20)
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "payroll_id")
    private Integer payrollId;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "approval_doc_id")
    private Integer approvalDocId;
}