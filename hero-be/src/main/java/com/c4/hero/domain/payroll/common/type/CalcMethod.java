package com.c4.hero.domain.payroll.common.type;

/**
 * <pre>
 * Class Name : CalcMethod
 * Description : 급여 항목 계산 방식을 정의하는 enum
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public enum CalcMethod {

    /** 고정금액 */
    FIXED,

    /** 비율(예: 과세표준의 %) */
    RATE,

    /** 추후 확장(문자열 수식) */
    FORMULA
}
