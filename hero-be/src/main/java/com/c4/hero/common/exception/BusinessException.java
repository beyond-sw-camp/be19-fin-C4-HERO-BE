package com.c4.hero.common.exception;

import lombok.Getter;

/**
 * <pre>
 * Class Name: BusinessException
 * Description: 비즈니스 로직 처리 중 발생하는 예외
 *
 * - ErrorCode를 포함한 커스텀 예외
 * - 전역 예외 핸들러에서 처리됨
 *
 * History
 * 2025/11/28 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 에러 코드 정보 */
    private final ErrorCode errorCode;

    /**
     * 에러 코드만으로 예외 생성
     *
     * @param errorCode 에러 코드
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 에러 코드와 커스텀 메시지로 예외 생성
     *
     * @param errorCode 에러 코드
     * @param message 커스텀 에러 메시지
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}