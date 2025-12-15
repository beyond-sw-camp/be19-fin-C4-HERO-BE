package com.c4.hero.domain.employee.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

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

    /**
     * DB 코드 값으로 Enum 상수를 찾는 정적 메서드
     */
    public static EmployeeStatus fromCode(String code) {
        return Stream.of(EmployeeStatus.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                // 예외 처리를 명확히 하는 것이 좋습니다.
                .orElseThrow(() -> new IllegalArgumentException("Invalid EmployeeStatus code: " + code));
    }
}
