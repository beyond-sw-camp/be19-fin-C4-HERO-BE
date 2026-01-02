package com.c4.hero.domain.payroll.common.type;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * <pre>
 * Class Name : PayrollConfigKey
 * Description : 급여 정책 공통 설정에서 사용되는 설정 키(enum)
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public enum PayrollConfigKey {
    /** 급여 지급일 (1~31) */
    PAYDAY_DAY,

    /** 급여 마감일 (1~31) */
    CLOSE_DAY,

    /** 반올림 방식 (HALF_UP / FLOOR / CEIL) */
    ROUNDING_MODE,

    /** 반올림 단위 (1 / 10 / 100 / 1000 등) */
    ROUNDING_UNIT,

    /** 기본금액 기준 타입 (예: BASE_SALARY / CONTRACT / FIXED 등) */
    BASE_AMOUNT_TYPE,

    /** 휴일 처리 규칙 */
    HOLIDAY_RULE,

    /** 급여 명세서 발송일 */
    PAYSLIP_SEND_DAY;


    /**
     * 허용 가능한 설정 키 집합
     */
    public static final Set<String> ALLOWED =
            Arrays.stream(values()).map(Enum::name).collect(Collectors.toUnmodifiableSet());
}