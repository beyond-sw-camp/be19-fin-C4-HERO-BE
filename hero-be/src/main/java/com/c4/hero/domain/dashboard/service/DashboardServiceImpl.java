package com.c4.hero.domain.dashboard.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.domain.dashboard.dto.ApprovalStatsDTO;
import com.c4.hero.domain.dashboard.dto.AttendanceStatsDTO;
import com.c4.hero.domain.dashboard.dto.ClockInRequestDTO;
import com.c4.hero.domain.dashboard.dto.ClockOutRequestDTO;
import com.c4.hero.domain.dashboard.dto.ClockStatusDTO;
import com.c4.hero.domain.dashboard.dto.MonthlySummaryDTO;
import com.c4.hero.domain.dashboard.dto.VacationStatsDTO;
import com.c4.hero.domain.dashboard.dto.WeeklyStatsDTO;
import com.c4.hero.domain.dashboard.mapper.DashboardMapper;
import com.c4.hero.domain.dashboard.util.MapperResultUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * <pre>
 * Class Name  : DashboardServiceImpl
 * Description : 대시보드 통계 비즈니스 로직 구현체
 *
 * History
 * 2025/12/26 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DashboardMapper timeClockMapper;

    /**
     * 출근 처리
     * @param employeeId 사원 ID
     * @param departmentId 부서 ID
     * @param dto 출근 요청 정보
     */
    @Override
    @Transactional
    public void clockIn(Integer employeeId, Integer departmentId, ClockInRequestDTO dto) {
        log.info("=== 출근 처리 시작 === employeeId: {}, workDate: {}, startTime: {}",
                employeeId, dto.getWorkDate(), dto.getStartTime());

        // 1. 오늘 이미 출근 기록이 있는지 확인
        ClockStatusDTO status = timeClockMapper.selectTodayStatus(employeeId, dto.getWorkDate());
        if (status != null && status.getIsClockedIn()) {
            log.warn("이미 출근 처리되었습니다. attendanceId: {}", status.getAttendanceId());
            throw new BusinessException(ErrorCode.ALREADY_CLOCKED_IN);
        }

        // 2. 출근 시각을 초 단위로 자르기 (마이크로초 제거)
        dto.setStartTime(dto.getStartTime().withNano(0));

        // 3. 출근 기록 INSERT
        int result = timeClockMapper.insertClockIn(employeeId, departmentId, dto);
        if (result != 1) {
            log.error("출근 INSERT 실패. result: {}", result);
            throw new BusinessException(ErrorCode.CLOCK_IN_FAILED);
        }

        log.info("=== 출근 처리 완료 ===");
    }

    /**
     * 퇴근 처리
     * @param employeeId 사원 ID
     * @param dto 퇴근 요청 정보
     */
    @Override
    @Transactional
    public void clockOut(Integer employeeId, ClockOutRequestDTO dto) {
        log.info("=== 퇴근 처리 시작 === employeeId: {}, workDate: {}, endTime: {}",
                employeeId, dto.getWorkDate(), dto.getEndTime());

        // 1. 오늘 출근 기록 확인
        ClockStatusDTO status = timeClockMapper.selectTodayStatus(employeeId, dto.getWorkDate());
        if (status == null || !status.getIsClockedIn()) {
            log.warn("출근 기록이 없습니다.");
            throw new BusinessException(ErrorCode.NOT_CLOCKED_IN);
        }

        // 2. 이미 퇴근 처리되었는지 확인
        if (status.getIsClockedOut()) {
            log.warn("이미 퇴근 처리되었습니다. attendanceId: {}", status.getAttendanceId());
            throw new BusinessException(ErrorCode.ALREADY_CLOCKED_OUT);
        }

        // 3. DTO에 attendanceId 설정
        dto.setAttendanceId(status.getAttendanceId());

        // 4. 퇴근 시각을 초 단위로 자르기 (마이크로초 제거)
        dto.setEndTime(dto.getEndTime().withNano(0));

        // 5. 퇴근 시각 UPDATE
        int result = timeClockMapper.updateClockOut(employeeId, dto);
        if (result != 1) {
            log.error("퇴근 UPDATE 실패. result: {}", result);
            throw new BusinessException(ErrorCode.CLOCK_OUT_FAILED);
        }

        log.info("=== 퇴근 처리 완료 ===");
    }

    /**
     * 오늘 출퇴근 상태 조회
     * @param employeeId 사원 ID
     * @param workDate 근무 일자
     * @return 출퇴근 상태 정보
     */
    @Override
    @Transactional(readOnly = true)
    public ClockStatusDTO getTodayStatus(Integer employeeId, LocalDate workDate) {
        log.debug("=== 출퇴근 상태 조회 === employeeId: {}, workDate: {}", employeeId, workDate);

        ClockStatusDTO status = timeClockMapper.selectTodayStatus(employeeId, workDate);

        // 출근 기록이 없으면 기본값 반환
        if (status == null) {
            status = new ClockStatusDTO();
            status.setWorkDate(workDate);
            status.setIsClockedIn(false);
            status.setIsClockedOut(false);
        }

        return status;
    }

    /**
     * 이번 주 근무 통계 조회 (실시간 근무 중 시간 포함)
     * @param employeeId 사원 ID
     * @return 주간 근무 통계
     */
    @Override
    @Transactional(readOnly = true)
    public WeeklyStatsDTO getWeeklyStats(Integer employeeId) {
        log.info("=== 주간 통계 조회 시작 === employeeId: {}", employeeId);

        // 이번 주 월요일 ~ 일요일 계산
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(java.time.DayOfWeek.MONDAY);
        LocalDate sunday = today.with(java.time.DayOfWeek.SUNDAY);

        String startDate = monday.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String endDate = sunday.format(DateTimeFormatter.ISO_LOCAL_DATE);

        log.info("조회 기간: {} ~ {}", startDate, endDate);

        // Mapper 호출
        Map<String, Object> result = timeClockMapper.selectWeeklyStats(
                employeeId,
                startDate,
                endDate
        );

        // DTO 변환
        WeeklyStatsDTO stats = new WeeklyStatsDTO();
        stats.setTotalWorkMinutes(MapperResultUtil.getIntValue(result, "total_work_minutes"));
        stats.setTotalWorkHours(MapperResultUtil.getDoubleValue(result, "total_work_hours"));
        stats.setLegalWeeklyHours(52);
        stats.setAchievementRate(MapperResultUtil.getDoubleValue(result, "achievement_rate"));
        stats.setIsWorkingToday(MapperResultUtil.getBooleanValue(result, "is_working_today"));
        stats.setTodayWorkMinutes(MapperResultUtil.getIntValue(result, "today_work_minutes"));

        // 오늘 근무 중이면 실시간 시간 추가
        if (Boolean.TRUE.equals(stats.getIsWorkingToday())) {
            Integer totalWithToday = stats.getTotalWorkMinutes() + stats.getTodayWorkMinutes();
            stats.setTotalWorkMinutes(totalWithToday);
            stats.setTotalWorkHours(Math.round(totalWithToday / 60.0 * 10.0) / 10.0);
            stats.setAchievementRate(Math.round((totalWithToday / 60.0) / 52 * 1000.0) / 10.0);
        }

        log.info("=== 주간 통계 조회 완료 === 총 근무시간: {}시간, 달성률: {}%",
                stats.getTotalWorkHours(), stats.getAchievementRate());

        return stats;
    }

    /**
     * 이번 달 요약 통계 조회
     * @param employeeId 사원 ID
     * @return 월간 요약 통계
     */
    @Override
    @Transactional(readOnly = true)
    public MonthlySummaryDTO getMonthlySummary(Integer employeeId) {
        log.info("=== 월간 요약 조회 시작 === employeeId: {}", employeeId);

        LocalDate today = LocalDate.now();
        LocalDate firstDay = today.withDayOfMonth(1);
        LocalDate lastDay = today.withDayOfMonth(today.lengthOfMonth());

        String startDate = firstDay.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String endDate = lastDay.format(DateTimeFormatter.ISO_LOCAL_DATE);

        Map<String, Object> result = timeClockMapper.selectMonthlySummary(
                employeeId, startDate, endDate
        );

        MonthlySummaryDTO summary = new MonthlySummaryDTO();
        summary.setWorkDays(MapperResultUtil.getIntValue(result, "work_days"));
        summary.setRemainingAnnualLeave(MapperResultUtil.getDoubleValue(result, "remaining_annual_leave"));
        summary.setUsedVacationDays(MapperResultUtil.getDoubleValue(result, "used_vacation_days"));

        log.info("=== 월간 요약 조회 완료 === 근무일수: {}일", summary.getWorkDays());

        return summary;
    }

    /**
     * 출근 통계 조회 (이번 달)
     * @param employeeId 사원 ID
     * @return 출근 통계
     */
    @Override
    @Transactional(readOnly = true)
    public AttendanceStatsDTO getAttendanceStats(Integer employeeId) {
        log.info("=== 출근 통계 조회 시작 === employeeId: {}", employeeId);

        LocalDate today = LocalDate.now();
        LocalDate firstDay = today.withDayOfMonth(1);
        LocalDate lastDay = today.withDayOfMonth(today.lengthOfMonth());

        String startDate = firstDay.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String endDate = lastDay.format(DateTimeFormatter.ISO_LOCAL_DATE);

        Map<String, Object> result = timeClockMapper.selectAttendanceStats(
                employeeId, startDate, endDate
        );

        AttendanceStatsDTO stats = new AttendanceStatsDTO();
        stats.setNormalDays(MapperResultUtil.getIntValue(result, "normal_days"));
        stats.setLateDays(MapperResultUtil.getIntValue(result, "late_days"));
        stats.setAbsentDays(MapperResultUtil.getIntValue(result, "absent_days"));
        stats.setEarlyLeaveDays(MapperResultUtil.getIntValue(result, "early_leave_days"));

        log.info("=== 출근 통계 조회 완료 === 정상: {}일, 지각: {}일",
                stats.getNormalDays(), stats.getLateDays());

        return stats;
    }

    /**
     * 휴가 현황 조회 (이번 달)
     * @param employeeId 사원 ID
     * @return 휴가 현황 통계
     */
    @Override
    @Transactional(readOnly = true)
    public VacationStatsDTO getVacationStats(Integer employeeId) {
        log.info("=== 휴가 현황 조회 시작 === employeeId: {}", employeeId);

        LocalDate today = LocalDate.now();
        LocalDate firstDay = today.withDayOfMonth(1);
        LocalDate lastDay = today.withDayOfMonth(today.lengthOfMonth());

        String startDate = firstDay.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String endDate = lastDay.format(DateTimeFormatter.ISO_LOCAL_DATE);

        Map<String, Object> result = timeClockMapper.selectVacationStats(
                employeeId, startDate, endDate
        );

        VacationStatsDTO stats = new VacationStatsDTO();
        stats.setAnnualLeaveDays(MapperResultUtil.getDoubleValue(result, "annual_leave_days"));
        stats.setHalfDayDays(MapperResultUtil.getDoubleValue(result, "half_day_days"));
        stats.setSickLeaveDays(MapperResultUtil.getDoubleValue(result, "sick_leave_days"));
        stats.setOtherLeaveDays(MapperResultUtil.getDoubleValue(result, "other_leave_days"));

        log.info("=== 휴가 현황 조회 완료 === 연차: {}일", stats.getAnnualLeaveDays());

        return stats;
    }

    /**
     * 결재 현황 조회
     * @param employeeId 사원 ID
     * @return 결재 현황 통계
     */
    @Override
    @Transactional(readOnly = true)
    public ApprovalStatsDTO getApprovalStats(Integer employeeId) {
        log.info("=== 결재 현황 조회 시작 === employeeId: {}", employeeId);

        Map<String, Object> result = timeClockMapper.selectApprovalStats(employeeId);

        ApprovalStatsDTO stats = new ApprovalStatsDTO();
        stats.setPendingCount(MapperResultUtil.getIntValue(result, "pending_count"));
        stats.setApprovedCount(MapperResultUtil.getIntValue(result, "approved_count"));
        stats.setRejectedCount(MapperResultUtil.getIntValue(result, "rejected_count"));

        log.info("=== 결재 현황 조회 완료 === 대기: {}건, 완료: {}건",
                stats.getPendingCount(), stats.getApprovedCount());

        return stats;
    }
}