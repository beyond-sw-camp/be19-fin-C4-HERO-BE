package com.c4.hero.domain.payroll.common.type;


/**
 * <pre>
 * Class Name : PayrollTargetType
 * Description : 급여 항목 정책 적용 대상 유형을 정의하는 enum
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public enum PayrollTargetType {
    /** 전체 사원 적용 */
    ALL,

    /** 특정 부서 적용 */
    DEPARTMENT,

    /** 특정 직급 적용 */
    POSITION,

    /** 특정 사원 적용 */
    EMPLOYEE
}
