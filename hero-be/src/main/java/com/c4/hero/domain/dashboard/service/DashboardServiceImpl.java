package com.c4.hero.domain.dashboard.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.domain.dashboard.dto.*;
import com.c4.hero.domain.dashboard.mapper.DashboardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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

    private final DashboardMapper dashboardMapper;

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
        ClockStatusDTO status = dashboardMapper.selectTodayStatus(employeeId, dto.getWorkDate());
        if (status != null && status.getIsClockedIn()) {
            throw new BusinessException(ErrorCode.ALREADY_CLOCKED_IN);
        }

        // 2. ✅ null 체크 및 기본값 설정
        if (dto.getWorkSystemTypeId() == null) {
            log.warn("⚠️ workSystemTypeId가 null! 기본값 1 설정");
            dto.setWorkSystemTypeId(1);
        }

        if (dto.getWorkSystemTemplateId() == null) {
            log.warn("⚠️ workSystemTemplateId가 null! 기본값 1 설정");
            dto.setWorkSystemTemplateId(1);
        }

        // 3. ✅ 최종 값 로그 출력
        log.info("✅ INSERT 직전 - typeId: {}, templateId: {}",
                dto.getWorkSystemTypeId(), dto.getWorkSystemTemplateId());

        // 4. 출근 시각을 초 단위로 자르기
        dto.setStartTime(dto.getStartTime().withNano(0));

        // 5. 출근 기록 INSERT
        int result = dashboardMapper.insertClockIn(employeeId, departmentId, dto);
        if (result != 1) {
            log.error("❌ 출근 INSERT 실패. result: {}", result);
            throw new BusinessException(ErrorCode.CLOCK_IN_FAILED);
        }

        log.info("=== ✅ 출근 처리 완료 === templateId: {}", dto.getWorkSystemTemplateId());
    }

    /**
     * 퇴근 처리
     * @param employeeId 사원 ID
     * @param dto 퇴근 요청 정보
     */
    @Override
    @Transactional
    public void clockOut(Integer employeeId, ClockOutRequestDTO dto) {
        log.info("=== 퇴근 처리 시작 === employeeId: {}, workDate: {}, endTime: {}, includeBreakTime: {}",
                employeeId, dto.getWorkDate(), dto.getEndTime(), dto.getIncludeBreakTime());

        // 1. 오늘 출근 기록 확인
        ClockStatusDTO status = dashboardMapper.selectTodayStatus(employeeId, dto.getWorkDate());
        if (status == null || !status.getIsClockedIn()) {
            log.warn("출근 기록이 없습니다.");
            throw new BusinessException(ErrorCode.NOT_CLOCKED_IN);
        }

        // 2. 이미 퇴근 처리되었는지 확인
        if (status.getIsClockedOut()) {
            log.warn("이미 퇴근 처리되었습니다. attendanceId: {}", status.getAttendanceId());
            throw new BusinessException(ErrorCode.ALREADY_CLOCKED_OUT);
        }

        // 3. 근무제 템플릿 정보 조회 (반차 여부 확인)
        Integer workSystemTemplateId = status.getWorkSystemTemplateId();
        WorkSystemTemplateDTO template = null;
        if (workSystemTemplateId != null) {
            template = dashboardMapper.selectWorkSystemTemplate(workSystemTemplateId);
        }

        // 4. DTO에 attendanceId 설정
        dto.setAttendanceId(status.getAttendanceId());

        // 5. 퇴근 시각을 초 단위로 자르기 (마이크로초 제거)
        dto.setEndTime(dto.getEndTime().withNano(0));

        // 6. 근무시간 계산 (분 단위)
        LocalTime startTime = status.getStartTime();
        LocalTime endTime = dto.getEndTime();

        long workMinutes = java.time.Duration.between(startTime, endTime).toMinutes();

        // 7. 휴게시간 차감 로직
        if (Boolean.TRUE.equals(dto.getIncludeBreakTime())) {
            // 오후 1시 이후 퇴근 → 휴게시간 차감
            Integer breakMinutes = (template != null && template.getBreakMinMinutes() != null)
                    ? template.getBreakMinMinutes()
                    : 60; // 기본 60분

            workMinutes -= breakMinutes;
            log.info("휴게시간 차감: {}분, 최종 근무시간: {}분", breakMinutes, workMinutes);
        }

        // 음수 방지
        if (workMinutes < 0) {
            workMinutes = 0;
        }

        // 8. DTO에 근무시간 설정
        dto.setWorkDuration((int) workMinutes);

        // 9. 퇴근 시각 및 근무시간 UPDATE
        int result = dashboardMapper.updateClockOut(employeeId, dto);
        if (result != 1) {
            log.error("퇴근 UPDATE 실패. result: {}", result);
            throw new BusinessException(ErrorCode.CLOCK_OUT_FAILED);
        }

        log.info("=== 퇴근 처리 완료 === 근무시간: {}분 ({}시간 {}분)",
                workMinutes, workMinutes / 60, workMinutes % 60);
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

        ClockStatusDTO status = dashboardMapper.selectTodayStatus(employeeId, workDate);

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
        WeeklyStatsDTO stats = dashboardMapper.selectWeeklyStats(employeeId, startDate, endDate);

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

        MonthlySummaryDTO summary = dashboardMapper.selectMonthlySummary(employeeId, startDate, endDate);

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

        AttendanceStatsDTO stats = dashboardMapper.selectAttendanceStats(employeeId, startDate, endDate);

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

        VacationStatsDTO stats = dashboardMapper.selectVacationStats(employeeId, startDate, endDate);

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

        ApprovalStatsDTO stats = dashboardMapper.selectApprovalStats(employeeId);

        log.info("=== 결재 현황 조회 완료 === 대기: {}건, 완료: {}건",
                stats.getPendingCount(), stats.getApprovedCount());

        return stats;
    }

    @Override
    public WorkSystemTemplateDTO getWorkSystemTemplate(Integer templateId) {
        return dashboardMapper.selectWorkSystemTemplate(templateId);
    }

    @Override
    public WorkSystemTemplateDTO getEmployeeDefaultTemplate(Integer employeeId) {
        log.info("=== 사원 기본 템플릿 조회 === employeeId: {}", employeeId);

        // 사원의 기본 템플릿 ID 조회
        Integer templateId = dashboardMapper.selectEmployeeDefaultTemplateId(employeeId);

        if (templateId == null) {
            log.warn("⚠️ 사원의 기본 템플릿이 없습니다. 기본값 1 사용");
            templateId = 1; // 기본 템플릿
        }

        return dashboardMapper.selectWorkSystemTemplate(templateId);
    }
}