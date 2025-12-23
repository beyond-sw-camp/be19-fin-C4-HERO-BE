package com.c4.hero.domain.payroll.deduction.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <pre>
 * Entity Name : Deduction
 * Description : 급여 공제 항목 마스터 엔티티
 *
 * 역할
 *  - 회사에서 사용하는 공제 항목(소득세, 국민연금, 건강보험 등)을 관리하는 마스터 테이블
 *
 * History
 *   2025/12/22 - 동근 최초 작성 (정책 기반 공제 관리 기능 강화)
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Entity
@Table(name = "tbl_deduction")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Deduction {

    /**
     * 공제 코드 (PK)
     * 예: INCOME_TAX, NPS, HI, UNION_FEE 등
     */
    @Id
    @Column(name = "deduction_id", length = 30)
    private String deductionId;

    /**
     * 화면 표시용 이름(공제 명)
     */
    @Column(nullable = false, length = 100)
    private String deductionName;

    /**
     * 부가 설명
     */
    private String description;

    /**
     * TAX / INSURANCE / ETC (공제 분류)
     */
    @Column(nullable = false, length = 20)
    private String deductionType;

    /**
     * FIXED(정액) / RATE(%) (계산 방식)
     */
    @Column(nullable = false, length = 20)
    private String calculationType;

    /**
     * RATE 타입일 때 사용 (%)
     */
    private Double rate;

    /**
     * FIXED 타입일 때 사용 (정액)
     */
    private Integer fixedAmount;

    /**
     * 사용 여부 (Y/N)
     */
    @Column(nullable = false, length = 1)
    private String activeYn;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // ===== 비즈니스 메서드 =====

    /**
     * 공제 항목 정보 수정 메서드
     *
     * @param name            공제명 (예: 소득세, 건강보험료 등)
     * @param description     공제 항목 설명
     * @param deductionType   TAX / INSURANCE / ETC
     * @param calculationType FIXED(정액) 또는 RATE(%)
     * @param rate            RATE 방식일 때 공제 비율(%)
     * @param fixedAmount     FIXED 방식일 때 공제 금액(정액)
     */
    public void update(
            String name,
            String description,
            String deductionType,
            String calculationType,
            Double rate,
            Integer fixedAmount
    ) {
        this.deductionName = name;
        this.description = description;
        this.deductionType = deductionType;
        this.calculationType = calculationType;
        this.rate = rate;
        this.fixedAmount = fixedAmount;
    }

    /**
     * 공제 항목 비활성화
     */
    public void deactivate() {
        this.activeYn = "N";
    }

    /**
     * 공제 항목 활성화
     */
    public void activate() {
        this.activeYn = "Y";
    }
}
