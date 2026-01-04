package com.c4.hero.domain.payroll.common.type;

/**
 * <pre>
 * Class Name : BaseAmountType
 * Description : 급여 계산 시 기준 금액 유형을 정의하는 enum
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public enum BaseAmountType {

    /** 기본급 기준 */
    BASE_SALARY,

    /** 지급총액 기준(확장용) */
    GROSS_PAY,

    /** 과세대상 기준(확장용) */
    TAXABLE_PAY
}
