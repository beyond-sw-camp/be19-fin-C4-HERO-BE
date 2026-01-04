package com.c4.hero.domain.payroll.adjustment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

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
 * Entity Name : PayrollRaise
 * Description : 급여 인상 내역 엔티티
 *
 * History
 *  2025/12/31 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */

@Entity
@Table(
        name = "tbl_payroll_raise",
                uniqueConstraints = {
                        @UniqueConstraint(columnNames = {"approval_doc_id"})
                }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PayrollRaise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "raise_id")
    private Integer raiseId;

    @Column(nullable = false, length = 255)
    private String reason;

    @Column(name = "employee_id", nullable = false)
    private Integer employeeId;

    @Column(name = "before_salary", nullable = false)
    private Integer beforeSalary;

    @Column(name = "after_salary", nullable = false)
    private Integer afterSalary;

    @Column(name = "raise_percent", precision = 5, scale = 1)
    private java.math.BigDecimal raisePercent;

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

    @Column(name = "requested_by")
    private Integer requestedBy;

    @Column(name = "approval_doc_id")
    private Integer approvalDocId;
}
