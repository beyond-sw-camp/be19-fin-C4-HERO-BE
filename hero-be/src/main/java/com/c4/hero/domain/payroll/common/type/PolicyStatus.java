package com.c4.hero.domain.payroll.common.type;

/**
 * <pre>
 * Class Name : PolicyStatus
 * Description : 급여 정책(PayrollPolicy)의 상태를 정의하는 enum
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public enum PolicyStatus {

    /** 정책 생성 직후 상태 */
    DRAFT,

    /** 현재 적용 중인 정책 */
    ACTIVE,

    /** 만료된 정책 */
    EXPIRED
}
