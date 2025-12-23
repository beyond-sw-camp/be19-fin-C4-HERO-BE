package com.c4.hero.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

/**
 * <pre>
 * Class Name: ApiResponse
 * Description: API 공통 응답 포맷
 *
 * - 성공/실패 여부
 * - 응답 데이터
 * - 에러 코드 및 메시지
 *
 * 사용 예시:
 * - 성공: ApiResponse.success(data)
 * - 실패: ApiResponse.error(errorCode, message)
 *
 * History
 * 2025/11/28 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 * @param <T> 응답 데이터 타입
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값은 JSON에 포함하지 않음
public class CustomResponse<T> {

    /** 성공 여부 (true: 성공, false: 실패) */
    private final boolean success;

    /** 응답 데이터 (성공 시에만 포함) */
    private final T data;

    /** 에러 코드 (실패 시에만 포함) */
    private final String errorCode;

    /** 에러 메시지 (실패 시에만 포함) */
    private final String message;

    /**
     * private 생성자
     * 정적 팩토리 메서드를 통해서만 생성 가능
     */
    private CustomResponse(boolean success, T data, String errorCode, String message) {
        this.success = success;
        this.data = data;
        this.errorCode = errorCode;
        this.message = message;
    }

    /**
     * 성공 응답 생성 (데이터 포함)
     *
     * @param data 응답 데이터
     * @param <T> 응답 데이터 타입
     * @return 성공 응답
     *
     * 예시: ApiResponse.success(employee)
     * 결과: { "success": true, "data": {...} }
     */
    public static <T> CustomResponse<T> success(T data) {
        return new CustomResponse<>(true, data, null, null);
    }

    /**
     * 성공 응답 생성 (데이터 + 메시지 포함)
     *
     * @param data 응답 데이터
     * @param message 성공 메시지
     * @param <T> 응답 데이터 타입
     * @return 성공 응답
     *
     * 예시: ApiResponse.success(employee, "사원 등록 완료")
     * 결과: { "success": true, "data": {...}, "message": "사원 등록 완료" }
     */
    public static <T> CustomResponse<T> success(T data, String message) {
        return new CustomResponse<>(true, data, null, message);
    }

    /**
     * 성공 응답 생성 (데이터 없음)
     *
     * @return 성공 응답
     *
     * 예시: ApiResponse.success()
     * 결과: { "success": true }
     */
    public static CustomResponse<Void> success() {
        return new CustomResponse<>(true, null, null, null);
    }

    /**
     * 실패 응답 생성
     *
     * @param errorCode 에러 코드
     * @param message 에러 메시지
     * @param <T> 응답 데이터 타입
     * @return 실패 응답
     *
     * 예시: ApiResponse.error("E001", "사원을 찾을 수 없습니다")
     * 결과: { "success": false, "errorCode": "E001", "message": "사원을 찾을 수 없습니다" }
     */
    public static <T> CustomResponse<T> error(String errorCode, String message) {
        return new CustomResponse<>(false, null, errorCode, message);
    }
}