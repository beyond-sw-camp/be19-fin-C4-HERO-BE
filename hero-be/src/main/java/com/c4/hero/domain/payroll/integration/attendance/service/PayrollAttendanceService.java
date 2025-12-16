package com.c4.hero.domain.payroll.integration.attendance.service;

/**
 * <pre>
 * Interface Name : PayrollAttendanceService
 * Description    : 급여 계산을 위한 근태 연계 서비스 인터페이스
 *
 * 역할
 *  - 사원의 기본급 조회
 *  - 월 기준 근태 데이터를 기반으로 초과근무 수당 계산
 *
 * History
 *  2025/12/15 - 동근 최초 작성
 *
 *  @author 동근
 *  @version 1.0
 * </pre>
 */
public interface PayrollAttendanceService {

    /**
     * 사원의 기본급 조회
     * @param employeeId 사원 ID
     * @return 기본급 금액 (원 단위)
     */
    int getBaseSalary(Integer employeeId);

    /**
     * 특정 월의 초과근무 수당 계산
     * @param salaryMonth 급여월 (YYYY-MM)
     * @param employeeId 사원 ID
     * @return 초과근무 수당 금액 (원 단위)
     */
    int calculateOvertime(String salaryMonth, Integer employeeId);
}