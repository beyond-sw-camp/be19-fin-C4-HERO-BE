package com.c4.hero.domain.payroll.batch.entity;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.domain.payroll.common.type.PayrollBatchStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <pre>
 * Entity Name : PayrollBatch
 * Description : 월별 급여 배치 엔티티
 *
 * 역할
 *  - 월 단위 급여 처리의 기준 단위
 *  - 급여 계산 -> 확정 -> 지급의 상태 전이를 관리
 *
 * 상태 흐름
 *  READY -> CALCULATED -> CONFIRMED -> PAID
 *
 * 도메인 규칙
 *  - salaryMonth(YYYY-MM)는 유일해야 함
 *  - CONFIRMED 이후에는 재계산/수정 불가
 *  - PAID는 급여 지급이 완료된 최종 상태
 *
 * History
 *  2025/12/15 - 동근 최초 작성
 * </pre>
 *
 *  @author 동근
 *  @version 1.0
 */

@Entity
@Table(name = "tbl_payroll_batch")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PayrollBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Integer batchId;

    @Column(name = "salary_month", nullable = false, unique = true)
    private String salaryMonth;

    @Enumerated(EnumType.STRING)
    private PayrollBatchStatus status;

       @Column(name = "created_by")
   private Integer createdBy;

           @Column(name = "approved_by")
   private Integer approvedBy;

           @Column(name = "approved_at")
   private LocalDateTime approvedAt;

           @Column(name = "paid_by")
   private Integer paidBy;

           @Column(name = "paid_at")
   private LocalDateTime paidAt;

    private LocalDateTime closedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 급여 배치 생성 팩토리 메서드
     *
     * @param month 급여월 (YYYY-MM)
     * @return 생성된 PayrollBatch 엔티티
     */
    public static PayrollBatch create(String month, Integer employeeId) {
        PayrollBatch b = new PayrollBatch();
        b.salaryMonth = month;
        b.status = PayrollBatchStatus.READY;
        b.createdAt = LocalDateTime.now();
        b.createdBy = employeeId;
        return b;
    }

    /**
     * 급여 배치 계산 완료 처리
     */
    public void markCalculated() {
        if (status != PayrollBatchStatus.READY) {
            throw new BusinessException(ErrorCode.PAYROLL_BATCH_INVALID_STATUS_TRANSITION);
        }
        this.status = PayrollBatchStatus.CALCULATED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 급여 배치 확정 처리
     */
    public void confirm(Integer employeeId) {
        if (status != PayrollBatchStatus.CALCULATED) {
            throw new BusinessException(ErrorCode.PAYROLL_BATCH_NOT_CALCULATED);
        }
        this.status = PayrollBatchStatus.CONFIRMED;
        this.approvedBy = employeeId;
        this.approvedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 급여 배치 지급 완료 처리
     */
    public void markPaid(Integer employeeId) {
        if (status != PayrollBatchStatus.CONFIRMED) {
            throw new BusinessException(ErrorCode.PAYROLL_BATCH_INVALID_STATUS_TRANSITION);
        }
        this.status = PayrollBatchStatus.PAID;
        this.paidBy = employeeId;
        this.paidAt = LocalDateTime.now();
        this.closedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
