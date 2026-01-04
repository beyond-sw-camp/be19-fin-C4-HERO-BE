package com.c4.hero.domain.payroll.integration.attendance.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <pre>
 * Mapper Name : PayrollAttendanceMapper
 * Description : 급여 계산을 위한 근태 연계 조회 매퍼
 *
 * 역할
 *  - 사원의 기본급 조회
 *  - 특정 월 기준 근태 데이터 집계
 *    · 근태 기록 건수
 *    · 총 근무 시간(분 단위)
 *
 * 설계 의도
 *  - 급여 계산 시 필요한 최소한의 근태 정보만 조회
 *
 *
 * History
 *  2025/12/15 - 동근 최초 작성
 * </pre>
 *
 *  @author 동근
 *  @version 1.0
 */

@Mapper
public interface PayrollAttendanceMapper {

    /**
     * 사원의 기본급 조회
     * @param employeeId 사원 ID
     * @return 기본급 금액
     */
    Integer selectBaseSalary(@Param("employeeId") Integer employeeId);

    /**
     * 특정 월의 근태 기록 건수 조회
     * @param employeeId 사원 ID
     * @param start 조회 시작일 (YYYY-MM-01)
     * @param end 조회 종료일 (YYYY-MM-31)
     * @return 근태 기록 건수
     * 사용 목적
     *   - 급여 계산 전 근태 데이터 누락 여부 검증
     *   - 근태 기록이 전혀 없는 사원에 대한 예외 처리 판단
     */
    int countAttendanceInMonth(@Param("employeeId") Integer employeeId,
                               @Param("start") String start,
                               @Param("end") String end);


    /**
     * 특정 월의 총 근무 시간 합계 조회 (분 단위)
     * @param employeeId 사원 ID
     * @param start 조회 시작일 (YYYY-MM-01)
     * @param end 조회 종료일 (YYYY-MM-31)
     * @return  총 근무 시간(분), 데이터가 없을 경우 NULL 가능
     */
    Integer sumWorkedMinutesInMonth(@Param("employeeId") Integer employeeId,
                                    @Param("start") String start,
                                    @Param("end") String end);


    /**
     * 특정 월의 근태 '근무일수'(work_date distinct) 조회 (표시/검증용)
     * @param employeeId 사원 ID
     * @param start 조회 시작일 (YYYY-MM-01)
     * @param end 조회 종료일 (YYYY-MM-DD)
     * @return 근무일수
     */
    int countWorkDaysInMonth(@Param("employeeId") Integer employeeId,
                             @Param("start") String start,
                             @Param("end") String end);
}