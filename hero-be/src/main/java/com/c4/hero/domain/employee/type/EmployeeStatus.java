package com.c4.hero.domain.employee.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 * Class Name: EmployeeStatus
 * Description: 직원의 재직 상태를 나타내는 Enum
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
public enum EmployeeStatus {
    /**
     * 재직 중
     */
    ACTIVE("A", "재직"),

    /**
     * 휴직 중
     */
    ON_LEAVE("O", "휴직"),

    /**
     * 퇴사
     */
    RETIRED("R", "퇴직");

    /**
     * DB에 저장될 코드 값
     */
    private final String code;

    /**
     * 화면에 표시될 설명
     */
    private final String description;
}
