package com.c4.hero.common.exception;

import com.c4.hero.common.response.CustomResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <pre>
 * Class Name: GlobalExceptionHandler
 * Description: 전역 예외 처리 핸들러
 *
 * - 애플리케이션 전역에서 발생하는 예외를 일관된 형태로 처리
 * - 예외 로깅 및 클라이언트에게 적절한 에러 응답 반환
 * - @RestControllerAdvice를 통해 모든 컨트롤러에 적용
 *
 * History
 * 2025/11/28 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리
     *
     * - 비즈니스 로직 처리 중 발생한 예외
     * - ErrorCode에 정의된 상태 코드와 메시지 반환
     *
     * @param e BusinessException
     * @return 에러 응답 (ErrorCode에 정의된 상태 코드)
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<CustomResponse<Void>> handleBusinessException(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage(), e);
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(CustomResponse.error(errorCode.getCode(), e.getMessage()));
    }

    /**
     * @Valid 유효성 검증 실패 예외 처리
     *
     * - 요청 DTO의 @Valid 검증 실패 시 발생
     * - @NotNull, @NotBlank, @Size 등의 어노테이션 검증 실패
     *
     * @param e MethodArgumentNotValidException
     * @return 400 Bad Request와 유효성 검증 실패 메시지
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<CustomResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: {}", e.getMessage());
        // 첫 번째 유효성 검증 실패 메시지 반환
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity
                .badRequest()
                .body(CustomResponse.error(ErrorCode.INVALID_INPUT_VALUE.getCode(), message));
    }

    /**
     * 데이터 바인딩 실패 예외 처리
     *
     * - 요청 파라미터를 객체로 바인딩할 때 실패한 경우
     * - 타입 불일치, 형식 오류 등
     *
     * @param e BindException
     * @return 400 Bad Request와 바인딩 실패 메시지
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<CustomResponse<Void>> handleBindException(BindException e) {
        log.error("BindException: {}", e.getMessage());
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity
                .badRequest()
                .body(CustomResponse.error(ErrorCode.INVALID_INPUT_VALUE.getCode(), message));
    }

    /**
     * 예상치 못한 예외 처리
     *
     * - 위에서 처리되지 않은 모든 예외
     * - 시스템 레벨 오류, 알 수 없는 예외 등
     * - 500 Internal Server Error 반환
     *
     * @param e Exception
     * @return 500 Internal Server Error와 서버 오류 메시지
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<CustomResponse<Void>> handleException(Exception e) {
        log.error("Exception: {}", e.getMessage(), e);
        return ResponseEntity
                .internalServerError()
                .body(CustomResponse.error(
                        ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                        ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
                ));
    }
}