package com.c4.hero.domain.employee.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 * Class Name: ChangeType
 * Description: 사원 정보 변경 이력의 유형을 나타내는 Enum
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
public enum ChangeType {
    /**
     * 신규 생성
     */
    CREATE("C", "신규 사원 등록"),

    /**
     * 정보 수정
     */
    UPDATE("U", "정보 수정"),

    /**
     * 퇴사/해고
     */
    TERMINATE("TE", "퇴사/해고"),

    /**
     * 승진
     */
    PROMOTION("P", "승진"),

    /**
     * 부서 이동/전근
     */
    TRANSFER("TR", "부서이동/전근"),

    /**
     * 기타
     */
    OTHER("O", "기타");

    /**
     * DB에 저장될 코드 값
     */
    private final String code;

    /**
     * 화면에 표시될 설명
     */
    private final String description;
}
