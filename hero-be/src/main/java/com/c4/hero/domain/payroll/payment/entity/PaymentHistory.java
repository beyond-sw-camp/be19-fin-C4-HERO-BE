package com.c4.hero.domain.payroll.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <pre>
 * Entity Name : PaymentHistory
 * Description : 급여 지급 이력 엔티티
 *
 * History
 *   2025/12/15 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Entity
@Table(name = "tbl_payment_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "payment_amount", nullable = false)
    private Integer paymentAmount;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "bank_account_id")
    private Integer bankAccountId;

    @Column(name = "payroll_id")
    private Integer payrollId;

    /**
     * 지급 완료 상태(COMPLETED) PaymentHistory 생성 팩토리 메서드
     *
     * @param payrollId     급여 ID
     * @param amount        지급 금액
     * @param bankAccountId 지급 계좌 ID (MVP에서는 null 가능)
     * @return PaymentHistory 지급 이력 엔티티
     */
    public static PaymentHistory completed(Integer payrollId, Integer amount, Integer bankAccountId) {
        PaymentHistory p = new PaymentHistory();
        p.payrollId = payrollId;
        p.paymentAmount = amount;
        p.bankAccountId = bankAccountId;
        p.paymentDate = LocalDate.now();
        p.paymentMethod = "BANK_TRANSFER";
        p.status = "COMPLETED";
        p.createdAt = LocalDateTime.now();
        return p;
    }
}