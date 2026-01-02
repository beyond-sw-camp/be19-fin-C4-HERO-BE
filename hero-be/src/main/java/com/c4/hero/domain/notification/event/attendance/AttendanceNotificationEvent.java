package com.c4.hero.domain.notification.event.attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * <pre>
 * Class Name: AttendanceNotificationEvent
 * Description: 근태 도메인 이벤트 정의
 *              알림 자동 발송을 위한 이벤트
 *
 * History
 * 2026/01/02 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
public class AttendanceNotificationEvent {

    /**
     * 로그인 시 출근 체크 확인 이벤트
     */
    @Getter
    @Builder
    @AllArgsConstructor
    public static class LoginClockInCheckEvent {
        private Integer employeeId;
        private LocalTime loginTime;
        private LocalDate workDate;
    }

    /**
     * 출근 미체크 이벤트
     */
    @Getter
    @Builder
    @AllArgsConstructor
    public static class ClockInMissingEvent {
        private Integer employeeId;
        private Integer attendanceId;
        private String workDate;
    }

    /**
     * 지각 이벤트
     */
    @Getter
    @Builder
    @AllArgsConstructor
    public static class LateEvent {
        private Integer employeeId;
        private Integer attendanceId;
        private Integer lateMinutes;
        private String workDate;
        private LocalTime startTime;
    }

    /**
     * 주간 근무시간 50시간 초과 경고 이벤트
     */
    @Getter
    @Builder
    public static class OvertimeWarningEvent {
        private Integer employeeId;
        private Double totalWorkHours;  // 누적 근무시간 (소수점 포함)
        private String warningLevel;  // NOTICE, WARNING, CRITICAL

    }
}