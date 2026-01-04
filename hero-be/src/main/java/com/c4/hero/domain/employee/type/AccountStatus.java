package com.c4.hero.domain.employee.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 * Class Name: AccountStatus
 * Description: 계정 상태를 나타내는 Enum
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
public enum AccountStatus {
    /**
     * 정상 사용 가능한 계정
     */
    ACTIVE("A", "정상"),

    /**
     * 관리자에 의해 비활성화된 계정
     */
    DISABLED("D", "비활성화");

    /**
     * DB에 저장될 코드 값
     */
    private final String code;

    /**
     * 화면에 표시될 설명
     */
    private final String description;
}
