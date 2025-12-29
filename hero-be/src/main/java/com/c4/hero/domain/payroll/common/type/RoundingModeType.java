package com.c4.hero.domain.payroll.common.type;

/**
 * <pre>
 * Class Name : RoundingModeType
 * Description : 급여 금액 계산 시 반올림 규칙을 정의하는 enum
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public enum RoundingModeType {

    /** 반올림 (0.5 이상 올림) */
    HALF_UP,

    /** 버림 */
    FLOOR,

    /** 올림 */
    CEIL
}
