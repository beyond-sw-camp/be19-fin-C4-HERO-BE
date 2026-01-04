package com.c4.hero.domain.payroll.policy.entity;

import com.c4.hero.domain.payroll.common.type.BaseAmountType;
import com.c4.hero.domain.payroll.common.type.CalcMethod;
import com.c4.hero.domain.payroll.common.type.ItemType;
import com.c4.hero.domain.payroll.common.type.RoundingModeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * <pre>
 * Class Name : PayrollItemPolicy
 * Description : 급여 정책(Policy) 하위의 수당/공제 항목 정책(Item Policy) 엔티티
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Entity
@Table(name = "tbl_payroll_item_policy")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PayrollItemPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_policy_id")
    private Integer itemPolicyId;

    @Column(name = "policy_id", nullable = false)
    private Integer policyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 20)
    private ItemType itemType;

    @Column(name = "item_code", nullable = false, length = 30)
    private String itemCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "calc_method", nullable = false, length = 20)
    private CalcMethod calcMethod;

    @Column(name = "fixed_amount")
    private Integer fixedAmount;

    @Column(name = "rate", precision = 10, scale = 4)
    private BigDecimal rate;

    @Enumerated(EnumType.STRING)
    @Column(name = "base_amount_type", nullable = false, length = 20)
    private BaseAmountType baseAmountType;

    @Column(name = "rounding_unit", nullable = false)
    private Integer roundingUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "rounding_mode", nullable = false, length = 20)
    private RoundingModeType roundingMode;

    @Column(name = "salary_month_from", nullable = false, length = 7)
    private String salaryMonthFrom;

    @Column(name = "salary_month_to", length = 7)
    private String salaryMonthTo;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    @Column(name = "active_yn", nullable = false, length = 1)
    private String activeYn;

    /**
     * 엔티티 최초 저장 시 기본값 자동 세팅
     *  - 값이 명시되지 않은 경우에도 계산/조회 로직이 안정적으로 동작하도록 보정
     */
    @PrePersist
    void prePersist() {
        if (this.baseAmountType == null) this.baseAmountType = BaseAmountType.BASE_SALARY;
        if (this.roundingUnit == null) this.roundingUnit = 1;
        if (this.roundingMode == null) this.roundingMode = RoundingModeType.HALF_UP;
        if (this.priority == null) this.priority = 0;
        if (this.activeYn == null) this.activeYn = "Y";
    }

    /**
     * 항목 정책의 주요 속성을 일괄 반영
     *  - 내부적으로 규칙 메서드(changeCalculation/changePeriod/changePriority)를 호출하여
     *    유효성 검증 및 값 정규화를 보장한다.
     */
    public void applyAll(
            CalcMethod calcMethod, Integer fixedAmount, BigDecimal rate,
            BaseAmountType baseAmountType, Integer roundingUnit, RoundingModeType roundingMode,
            String salaryMonthFrom, String salaryMonthTo,
            Integer priority, String activeYn
    ) {
        changeCalculation(calcMethod, fixedAmount, rate);

        this.baseAmountType = Objects.requireNonNull(baseAmountType, "baseAmountType는 필수입니다.");
        if (roundingUnit == null || roundingUnit <= 0) {
            throw new IllegalArgumentException("roundingUnit은 1 이상이어야 합니다.");
        }
        this.roundingUnit = roundingUnit;
        this.roundingMode = Objects.requireNonNull(roundingMode, "roundingMode는 필수입니다.");

        changePeriod(salaryMonthFrom, salaryMonthTo);
        changePriority(priority);

        if (activeYn != null) {
            String v = activeYn.toUpperCase();
            if (!v.equals("Y") && !v.equals("N")) throw new IllegalArgumentException("activeYn은 Y/N만 가능합니다.");
            this.activeYn = v;
        }
    }

    /**
     * 계산 방식 변경 및 금액/비율 값 정합성 보장
     *  - FIXED: fixedAmount 필수(0 이상), rate는 null
     *  - RATE : rate 필수(0 이상), fixedAmount는 null
     *  - FORMULA: MVP 범위에서는 별도 수식 컬럼이 없어 값 미사용(null 처리)
     */
    public void changeCalculation(CalcMethod calcMethod, Integer fixedAmount, BigDecimal rate) {
        this.calcMethod = Objects.requireNonNull(calcMethod, "calcMethod는 필수입니다.");

        switch (calcMethod) {
            case FIXED -> {
                if (fixedAmount == null || fixedAmount < 0) {
                    throw new IllegalArgumentException("FIXED는 fixedAmount(0 이상)가 필요합니다.");
                }
                this.fixedAmount = fixedAmount;
                this.rate = null;
            }
            case RATE -> {
                if (rate == null || rate.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("RATE는 rate(0 이상)가 필요합니다.");
                }
                this.rate = rate;
                this.fixedAmount = null;
            }
            case FORMULA -> {
                this.fixedAmount = null;
                this.rate = null;
            }
        }
    }

    /**
     * 적용 기간 변경
     *  - from은 필수(YYYY-MM), to는 선택(null 가능)
     */
    public void changePeriod(String from, String to) {
        if (from == null || from.isBlank()) {
            throw new IllegalArgumentException("salaryMonthFrom(YYYY-MM)은 필수입니다.");
        }
        this.salaryMonthFrom = from;
        this.salaryMonthTo = to;
    }

    /**
     * 우선순위 변경 (0 이상)
     *  - null 입력 시 기본값 0으로 보정
     */
    public void changePriority(Integer priority) {
        int p = (priority == null) ? 0 : priority;
        if (p < 0) throw new IllegalArgumentException("priority는 0 이상이어야 합니다.");
        this.priority = p;
    }

    /** 항목 정책 활성화 */
    public void activate() { this.activeYn = "Y"; }

    /** 항목 정책 비활성화 */
    public void deactivate() { this.activeYn = "N"; }
}
