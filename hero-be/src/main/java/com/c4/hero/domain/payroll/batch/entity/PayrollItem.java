package com.c4.hero.domain.payroll.batch.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Entity Name : PayrollItem
 * Description : 급여 항목 엔티티
 *
 * 역할
 *  - 급여(Payroll)에 포함되는 개별 항목 표현
 *  - 수당/공제/세금 등의 금액 정보를 관리
 *
 * History
 *  2025/12/15 - 동근 최초 작성
 * </pre>
 *
 *  @author 동근
 *  @version 1.0
 */

@Entity
@Table(name = "tbl_payroll_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PayrollItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer payrollItemId;

    /**
     * 항목 유형
     *  - ALLOWANCE : 수당
     *  - DEDUCTION : 공제
     *  - TAX       : 세금
     */
    private String itemType;

    /**
     * 항목 명 (화면 표시용)
     * 예: 연장근무수당, 식대, 소득세
     */
    private String itemCode;
    private String itemName;
    private Integer amount;

    /**
     * 과세 여부
     *  - Y : 과세 대상
     *  - N : 비과세 대상
     */
    private String taxableYn;

    private Integer payrollId;

    /**
     * 급여 항목 생성 팩토리 메서드
     *
     * @param payrollId 연관 급여 ID
     * @param type      항목 유형 (ALLOWANCE / DEDUCTION / TAX)
     * @param code      항목 코드
     * @param name      항목 명
     * @param amount    금액 (원 단위)
     * @param taxableYn 과세 여부 (Y / N)
     * @return 생성된 PayrollItem 엔티티
     */
    public static PayrollItem of(
            Integer payrollId,
            String type,
            String code,
            String name,
            int amount,
            String taxableYn
    ) {
        PayrollItem i = new PayrollItem();
        i.payrollId = payrollId;
        i.itemType = type;
        i.itemCode = code;
        i.itemName = name;
        i.amount = amount;
        i.taxableYn = taxableYn;
        return i;
    }
}

