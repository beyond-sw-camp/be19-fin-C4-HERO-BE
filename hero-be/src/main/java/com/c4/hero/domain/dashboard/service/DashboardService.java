package com.c4.hero.domain.dashboard.service;

import com.c4.hero.domain.dashboard.dto.ApprovalStatsDTO;
import com.c4.hero.domain.dashboard.dto.AttendanceStatsDTO;
import com.c4.hero.domain.dashboard.dto.ClockInRequestDTO;
import com.c4.hero.domain.dashboard.dto.ClockOutRequestDTO;
import com.c4.hero.domain.dashboard.dto.ClockStatusDTO;
import com.c4.hero.domain.dashboard.dto.MonthlySummaryDTO;
import com.c4.hero.domain.dashboard.dto.VacationStatsDTO;
import com.c4.hero.domain.dashboard.dto.WeeklyStatsDTO;

import java.time.LocalDate;

/**
 * <pre>
 * Interface Name : DashboardService
 * Description    : 대시보드 통계 비즈니스 로직 인터페이스
 *
 * History
 * 2025/12/26 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
public interface DashboardService {

    /**
     * 출근 처리
     * @param employeeId 사원 ID
     * @param departmentId 부서 ID
     * @param dto 출근 요청 정보
     */
    void clockIn(Integer employeeId, Integer departmentId, ClockInRequestDTO dto);

    /**
     * 퇴근 처리
     * @param employeeId 사원 ID
     * @param dto 퇴근 요청 정보
     */
    void clockOut(Integer employeeId, ClockOutRequestDTO dto);

    /**
     * 오늘 출퇴근 상태 조회
     * @param employeeId 사원 ID
     * @param workDate 근무 일자
     * @return 출퇴근 상태 정보
     */
    ClockStatusDTO getTodayStatus(Integer employeeId, LocalDate workDate);

    /**
     * 이번 주 근무 통계 조회 (실시간 근무 중 시간 포함)
     * @param employeeId 사원 ID
     * @return 주간 근무 통계
     */
    WeeklyStatsDTO getWeeklyStats(Integer employeeId);

    /**
     * 이번 달 요약 통계 조회
     * @param employeeId 사원 ID
     * @return 월간 요약 통계
     */
    MonthlySummaryDTO getMonthlySummary(Integer employeeId);

    /**
     * 출근 통계 조회 (이번 달)
     * @param employeeId 사원 ID
     * @return 출근 통계
     */
    AttendanceStatsDTO getAttendanceStats(Integer employeeId);

    /**
     * 휴가 현황 조회 (이번 달)
     * @param employeeId 사원 ID
     * @return 휴가 현황 통계
     */
    VacationStatsDTO getVacationStats(Integer employeeId);

    /**
     * 결재 현황 조회
     * @param employeeId 사원 ID
     * @return 결재 현황 통계
     */
    ApprovalStatsDTO getApprovalStats(Integer employeeId);

}