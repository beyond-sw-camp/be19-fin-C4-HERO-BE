package com.c4.hero.domain.employee.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 * Class Name: RoleType
 * Description: 시스템 내 사용자의 역할(권한)을 나타내는 Enum
 *
 * History
 * 2025/12/09 이승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
@Getter
@RequiredArgsConstructor
public enum RoleType {
    /**
     * 시스템 전체를 관리하는 최고 권한
     */
    SYSTEM_ADMIN("SYSTEM_ADMIN", "시스템 관리자"),

    /**
     * 인사 관련 모든 기능을 총괄하는 관리자
     */
    HR_MANAGER("HR_MANAGER", "인사 관리자"),

    /**
     * 급여 관련 기능을 담당하는 관리자
     */
    HR_PAYROLL("HR_PAYROLL", "인사 급여 담당"),

    /**
     * 평가 관련 기능을 담당하는 관리자
     */
    HR_EVALUATION("HR_EVALUATION", "인사 평가 담당"),

    /**
     * 발령 관련 기능을 담당하는 관리자
     */
    HR_TRANSFER("HR_TRANSFER", "인사 발령 담당"),

    /**
     * 근태 관련 기능을 담당하는 관리자
     */
    HR_ATTENDANCE("HR_ATTENDANCE", "인사 근태 담당"),

    /**
     * 자신의 부서원 정보를 관리하는 관리자
     */
    DEPT_MANAGER("DEPT_MANAGER", "부서 관리자"),

    /**
     * 일반 직원
     */
    EMPLOYEE("EMPLOYEE", "일반 사원");

    /**
     * DB에 저장될 코드 값
     */
    private final String code;

    /**
     * 화면에 표시될 설명
     */
    private final String description;
}
