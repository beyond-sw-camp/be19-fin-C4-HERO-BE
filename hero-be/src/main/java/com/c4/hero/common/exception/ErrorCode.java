package com.c4.hero.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * <pre>
 * Enum Name: ErrorCode
 * Description: 애플리케이션 전역 에러 코드 정의
 *
 * - HTTP 상태 코드
 * - 커스텀 에러 코드
 * - 에러 메시지
 *
 * History
 * 2025/11/28 (혜원) 최초 작성
 * 2025/12/09 (승건) 사원 추가 시 발생할 수 있는 에러 추가
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ===== 공통 에러 =====
    /**
     * 잘못된 입력값
     */
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),

    /**
     * 서버 내부 오류
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 오류가 발생했습니다."),

    /**
     * 엔티티를 찾을 수 없음
     */
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "엔티티를 찾을 수 없습니다."),

    /**
     * 인증 필요
     */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C004", "인증이 필요합니다."),

    /**
     * 권한 없음
     */
    FORBIDDEN(HttpStatus.FORBIDDEN, "C005", "권한이 없습니다."),

    // ===== 사원(Employee) 관련 에러 =====
    /**
     * 부서를 찾을 수 없음
     */
    DEPARTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "E001", "해당 부서를 찾을 수 없습니다."),

    /**
     * 직급을 찾을 수 없음
     */
    GRADE_NOT_FOUND(HttpStatus.NOT_FOUND, "E002", "해당 직급을 찾을 수 없습니다."),

    /**
     * 직책을 찾을 수 없음
     */
    JOB_TITLE_NOT_FOUND(HttpStatus.NOT_FOUND, "E003", "해당 직책을 찾을 수 없습니다."),

    /**
     * 중복된 사번
     */
    DUPLICATE_EMPLOYEE_NUMBER(HttpStatus.CONFLICT, "E004", "이미 사용 중인 사번입니다."),

    /**
     * 중복된 이메일
     */
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "E005", "이미 사용 중인 이메일입니다."),

    /**
     * 중복된 전화번호
     */
    DUPLICATE_PHONE(HttpStatus.CONFLICT, "E006", "이미 사용 중인 전화번호입니다."),

    /**
     * 역할을 찾을 수 없음
     */
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "E007", "해당 역할을 찾을 수 없습니다."),

    // ===== 메일 관련 에러 =====
    /**
     * 이메일 발송 실패
     */
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "M001", "이메일 발송에 실패했습니다.");


    /** HTTP 상태 코드 */
    private final HttpStatus status;

    /** 커스텀 에러 코드 */
    private final String code;

    /** 에러 메시지 */
    private final String message;
}
