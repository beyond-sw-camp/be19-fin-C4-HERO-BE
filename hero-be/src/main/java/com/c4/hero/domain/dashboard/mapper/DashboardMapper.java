package com.c4.hero.domain.dashboard.mapper;

import com.c4.hero.domain.dashboard.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

/**
 * <pre>
 * Interface Name : DashboardMapper
 * Description    : 대시보드 통계 관련 MyBatis Mapper 인터페이스
 *
 * History
 * 2025/12/26 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Mapper
public interface DashboardMapper {

    /**
     * 출근 기록 INSERT
     * @param employeeId 사원 ID
     * @param departmentId 부서 ID
     * @param dto 출근 요청 정보
     * @return 삽입된 행 수
     */
    int insertClockIn(
            @Param("employeeId") Integer employeeId,
            @Param("departmentId") Integer departmentId,
            @Param("dto") ClockInRequestDTO dto
    );

    /**
     * 퇴근 시각 UPDATE
     * @param employeeId 사원 ID
     * @param dto 퇴근 요청 정보
     * @return 수정된 행 수
     */
    int updateClockOut(
            @Param("employeeId") Integer employeeId,
            @Param("dto") ClockOutRequestDTO dto
    );

    /**
     * 오늘 출퇴근 상태 조회
     * @param employeeId 사원 ID
     * @param workDate 근무 일자
     * @return 출퇴근 상태 정보
     */
    ClockStatusDTO selectTodayStatus(
            @Param("employeeId") Integer employeeId,
            @Param("workDate") LocalDate workDate
    );

    /**
     * 이번 주 근무 통계 조회 (실시간 근무 중인 시간 포함)
     * @param employeeId 사원 ID
     * @param startDate 시작 날짜 (이번 주 월요일)
     * @param endDate 종료 날짜 (이번 주 일요일)
     * @return 주간 근무 통계
     */
    WeeklyStatsDTO selectWeeklyStats(
            @Param("employeeId") Integer employeeId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 이번 달 요약 통계 조회
     * @param employeeId 사원 ID
     * @param startDate 시작 날짜 (이번 달 1일)
     * @param endDate 종료 날짜 (이번 달 말일)
     * @return 월간 요약 통계
     */
    MonthlySummaryDTO selectMonthlySummary(
            @Param("employeeId") Integer employeeId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 출근 통계 조회 (이번 달)
     * @param employeeId 사원 ID
     * @param startDate 시작 날짜 (이번 달 1일)
     * @param endDate 종료 날짜 (이번 달 말일)
     * @return 출근 통계
     */
    AttendanceStatsDTO selectAttendanceStats(
            @Param("employeeId") Integer employeeId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 휴가 현황 조회 (이번 달)
     * @param employeeId 사원 ID
     * @param startDate 시작 날짜 (이번 달 1일)
     * @param endDate 종료 날짜 (이번 달 말일)
     * @return 휴가 현황 통계
     */
    VacationStatsDTO selectVacationStats(
            @Param("employeeId") Integer employeeId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 결재 현황 조회
     * @param employeeId 사원 ID
     * @return 결재 현황 통계
     */
    ApprovalStatsDTO selectApprovalStats(
            @Param("employeeId") Integer employeeId
    );
}