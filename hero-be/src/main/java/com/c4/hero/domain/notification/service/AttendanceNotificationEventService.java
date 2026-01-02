package com.c4.hero.domain.notification.service;

import com.c4.hero.domain.notification.event.attendance.AttendanceNotificationEvent;
import com.c4.hero.domain.attendance.mapper.AttendanceMapper;
import com.c4.hero.domain.dashboard.dto.WeeklyStatsDTO;
import com.c4.hero.domain.dashboard.mapper.DashboardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Class Name: AttendanceNotificationService
 * Description: 근태 알림 및 상태 처리 서비스
 *
 * History
 * 2026/01/02 (혜원) 지각 즉시 알림 및 결근 스케줄러 로직 완성
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceNotificationEventService {

    private final AttendanceMapper attendanceMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final DashboardMapper dashboardMapper;

    /**
     * 출근 미체크 직원 조회 (스케줄러에서 미체크 알림 발송용으로 사용)
     */
    public List<Map<String, Object>> findClockInMissingEmployees(LocalDate workDate) {
        log.info("[알림 조회] 출근 미체크 직원 조회 시작 - 날짜: {}", workDate);
        return attendanceMapper.selectClockInMissingEmployees(workDate);
    }

    /**
     * 출근 처리 및 지각 즉시 알림 (사원이 출근 버튼 클릭 시 호출)
     */
    @Transactional
    public void clockIn(Integer employeeId) {
        LocalTime now = LocalTime.now();
        LocalDate today = LocalDate.now();

        // 1. 오늘 적용된 근무제 시작 시간과 근태 기록 ID 조회
        Map<String, Object> workSystem = attendanceMapper.selectCurrentWorkSystem(employeeId, today);
        if (workSystem == null) {
            log.warn("해당 직원의 근태 생성 데이터가 없습니다. employeeId: {}", employeeId);
            return;
        }

        Object startTimeObj = workSystem.get("scheduledStartTime");
        LocalTime scheduledStartTime;

        if (startTimeObj instanceof String) {
            scheduledStartTime = LocalTime.parse((String) startTimeObj);
        } else {
            scheduledStartTime = (LocalTime) startTimeObj;
        }

        Integer attendanceId = (Integer) workSystem.get("attendanceId");

        // 2. 지각 여부 판단
        String status = "정상";
        int lateMinutes = 0;

        if (now.isAfter(scheduledStartTime)) {
            status = "지각";
            lateMinutes = (int) Duration.between(scheduledStartTime, now).toMinutes();
            log.info("[지각 감지] employeeId: {}, {}분 지각", employeeId, lateMinutes);
        }

        // 3. 지각일 경우 '즉시' 이벤트 발행 -> Listener를 통해 알림 발송
        if ("지각".equals(status)) {
            eventPublisher.publishEvent(AttendanceNotificationEvent.LateEvent.builder()
                    .employeeId(employeeId)
                    .attendanceId(attendanceId)
                    .lateMinutes(lateMinutes)
                    .workDate(today.toString())
                    .startTime(now)
                    .build());
        }
    }

    @Transactional
    public void clockOut(Integer employeeId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(java.time.DayOfWeek.MONDAY);
        LocalDate weekEnd = today.with(java.time.DayOfWeek.SUNDAY);

        WeeklyStatsDTO stats = dashboardMapper.selectWeeklyStats(
                employeeId,
                weekStart.toString(),
                weekEnd.toString()
        );

        if (stats != null) {
            int totalMinutes = stats.getTotalWorkMinutes();

            // 52시간 초과 - 법정 상한 위반 (빨강)
            if (totalMinutes >= 3120) {
                log.warn("[위험] 법정 근무시간 초과! employeeId: {}, {}분", employeeId, totalMinutes);
                eventPublisher.publishEvent(
                        AttendanceNotificationEvent.OvertimeWarningEvent.builder()
                                .employeeId(employeeId)
                                .totalWorkHours(stats.getTotalWorkHours())
                                .warningLevel("CRITICAL")  // 위험
                                .build()
                );
            }
            // 51시간 이상 - 초과 임박 (주황)
            else if (totalMinutes >= 3060) {
                log.warn("[경고] 법정 상한 임박! employeeId: {}, {}분", employeeId, totalMinutes);
                eventPublisher.publishEvent(
                        AttendanceNotificationEvent.OvertimeWarningEvent.builder()
                                .employeeId(employeeId)
                                .totalWorkHours(stats.getTotalWorkHours())
                                .warningLevel("WARNING")  // 경고
                                .build()
                );
            }
            // 50시간 이상 - 주의 필요 (노랑)
            else if (totalMinutes >= 3000) {
                log.info("[주의] 근무시간 50시간 도달 employeeId: {}, {}분", employeeId, totalMinutes);
                eventPublisher.publishEvent(
                        AttendanceNotificationEvent.OvertimeWarningEvent.builder()
                                .employeeId(employeeId)
                                .totalWorkHours(stats.getTotalWorkHours())
                                .warningLevel("NOTICE")  // 주의
                                .build()
                );
            }
        }
    }
}