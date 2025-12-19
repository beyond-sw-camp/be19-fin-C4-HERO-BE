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
 * 2025-11-28 (혜원) 최초 작성
 * 2025-12-09 (승건) 사원 추가 시 발생할 수 있는 에러 추가
 * 2025-12-09 (이승건) 토큰 재발급 관련 에러 추가
 * 2025-12-09 (이승건) Access Token 만료 에러 추가
 * 2025-12-16 (동근) 급여 관련 에러 코드 추가
 * 2025-12-16 (승건) 부서장 관련 에러 코드 추가
 * </pre>
 *
 * @author 혜원
 * @version 1.4
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

    /**
     * 유효하지 않은 리프레시 토큰
     */
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "C006", "유효하지 않은 리프레시 토큰입니다."),

    /**
     * Access Token 만료
     */
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "C007", "Access Token이 만료되었습니다."),

    /**
     * 관리자 데이터 수정/삭제 불가
     */
    CANNOT_MODIFY_ADMIN_DATA(HttpStatus.FORBIDDEN, "C008", "관리자 데이터는 수정/삭제할 수 없습니다."),

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

    /**
     * 직원을 찾을 수 없음
     */
    EMPLOYEE_NOT_FOUND(HttpStatus.NOT_FOUND, "E008", "해당 직원을 찾을 수 없습니다."),

    /**
     * 부서장은 해당 부서 소속이어야 함
     */
    MANAGER_NOT_IN_DEPARTMENT(HttpStatus.BAD_REQUEST, "E009", "부서장은 해당 부서의 소속이어야 합니다."),
    
    
    // ===== 메일 관련 에러 =====
    /**
     * 이메일 발송 실패
     */
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "M001", "이메일 발송에 실패했습니다."),


    // ===== 급여(Payroll) - 계좌 관련 에러 =====

    /**
     * 대표(기본) 계좌 삭제 불가
     */
    BANK_PRIMARY_DELETE_NOT_ALLOWED(HttpStatus.CONFLICT, "P001", "대표(기본) 계좌는 삭제할 수 없습니다."),

    /**
     * 계좌가 1개뿐이라 삭제 불가
     */
    BANK_MIN_ONE_REQUIRED(HttpStatus.CONFLICT, "P002", "계좌가 1개뿐이라 삭제할 수 없습니다. (대표 계좌는 반드시 필요)"),

    /**
     * 급여 계좌를 찾을 수 없음
     */
    BANK_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "P003", "급여 계좌를 찾을 수 없습니다."),

    /**
     * 지급 이력 있으면 급여 계좌 삭제 불가
     */
    BANK_ACCOUNT_HAS_PAYMENT_HISTORY(HttpStatus.CONFLICT, "P004", "지급 이력이 있는 계좌는 삭제할 수 없습니다."),


    // ===== 급여(Payroll) - 배치 관련 에러 =====
    /**
     * 해당 급여월에 대한 급여 배치가 이미 존재하는 경우 생성 불가
     */
    PAYROLL_BATCH_DUPLICATED(HttpStatus.BAD_REQUEST, "P101", "해당 급여월 배치가 이미 존재합니다."),

    /**
     * 요청한 급여 배치를 찾을 수 없는 경우(존재하지 않는 batchId 조회 시)
     */
    PAYROLL_BATCH_NOT_FOUND(HttpStatus.NOT_FOUND, "P102", "급여 배치를 찾을 수 없습니다."),

    /**
     * 급여 계산이 완료되지 않은 상태의 배치 (CALCULATED 상태가 아닌 배치에 대해 확정/후속 처리를 시도한 경우)
     */
    PAYROLL_BATCH_NOT_CALCULATED(HttpStatus.BAD_REQUEST, "P103", "급여 계산이 완료되지 않은 배치입니다."),

    /**
     * 이미 확정(CONFIRMED)된 급여 배치 (확정 이후 수정 또는 재계산을 시도한 경우)
     */
    PAYROLL_BATCH_LOCKED(HttpStatus.CONFLICT, "P104", "확정된 배치는 수정/재계산할 수 없습니다."),

    /**
     * 급여 배치 상태(정의된 흐름)가 유효하지 않은 경우
     */
    PAYROLL_BATCH_INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "P105", "배치 상태 변경 순서가 올바르지 않습니다."),

    /**
     * 급여 계산에 필요한 근태 데이터가 없거나 이상치가 존재하는 경우
     */
    PAYROLL_ATTENDANCE_INVALID(HttpStatus.BAD_REQUEST, "P106", "근태 데이터가 없거나 이상치가 있어 급여 계산이 불가합니다."),

    /**
     * 급여 계산에 필요한 근태 로그 자체가 존재하지 않는 경우
     */
    PAYROLL_ATTENDANCE_LOG_NOT_FOUND(HttpStatus.BAD_REQUEST, "P107", "근태 로그가 없어 급여 계산이 불가합니다."),

    /**
     * 배치 확정 불가 - 실패 건 존재
     */
    PAYROLL_BATCH_HAS_FAILED(HttpStatus.CONFLICT, "P108", "실패(FAILED) 처리된 사원이 있어 배치를 확정할 수 없습니다.");

    /** HTTP 상태 코드 */
    private final HttpStatus status;

    /** 커스텀 에러 코드 */
    private final String code;

    /** 에러 메시지 */
    private final String message;
}
