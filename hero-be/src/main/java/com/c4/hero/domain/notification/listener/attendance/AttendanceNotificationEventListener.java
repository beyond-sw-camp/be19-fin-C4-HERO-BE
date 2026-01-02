package com.c4.hero.domain.notification.listener.attendance;

import com.c4.hero.common.event.NotificationEvent;
import com.c4.hero.domain.notification.event.attendance.AttendanceNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

/**
 * <pre>
 * Class Name: AttendanceNotificationEventListener
 * Description: 근태 이벤트 리스너
 *
 * History
 * 2026/01/02 (혜원) 최초 작성
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AttendanceNotificationEventListener {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 출근 미체크 이벤트 → 알림 발송
     */
    @Async
    @EventListener
    @Transactional
    public void handleClockInMissing(AttendanceNotificationEvent.ClockInMissingEvent event) {
        log.info("[자동알림] 출근 미체크 - employeeId: {}", event.getEmployeeId());

        NotificationEvent notification = NotificationEvent.builder()
                .employeeId(event.getEmployeeId())
                .type("attendance")
                .title("출근 미체크")
                .message(event.getWorkDate() + " 출근 체크가 없습니다. 근태를 확인해주세요.")
                .link("/attendance")
                .attendanceId(event.getAttendanceId())
                .build();

        eventPublisher.publishEvent(notification);
    }

    /**
     * 지각 이벤트 → 알림 발송
     */
    @Async
    @EventListener
    @Transactional
    public void handleLate(AttendanceNotificationEvent.LateEvent event) {
        log.info("[실시간 알림 전송] 사원ID: {}, {}분 지각", event.getEmployeeId(), event.getLateMinutes());

        // 시간 포맷팅
        String formattedTime = "";
        if (event.getStartTime() != null) {
            formattedTime = event.getStartTime().format(DateTimeFormatter.ofPattern("HH시 mm분"));
        }

        NotificationEvent notification = NotificationEvent.builder()
                .employeeId(event.getEmployeeId())
                .type("attendance")
                .title("지각 알림")
                .message(formattedTime + "에 출근하여 지각 처리되었습니다.")
                .link("/attendance")
                .attendanceId(event.getAttendanceId())
                .build();

        eventPublisher.publishEvent(notification);
    }

    /**
     * 주간 근무시간 50시간 초과 알림
     */
    @Async
    @EventListener
    @Transactional
    public void handleOvertimeWarning(AttendanceNotificationEvent.OvertimeWarningEvent event) {
        String title;
        String message;

        switch (event.getWarningLevel()) {
            case "CRITICAL":
                title = "⚠ 법정 근무시간 초과";
                message = String.format("이번 주 누적 근무시간이 %.1f시간으로 법정 상한(52시간)을 초과했습니다! 즉시 조치가 필요합니다.",
                        event.getTotalWorkHours());
                break;
            case "WARNING":
                title = "⚠ 법정 근무시간 임박";
                message = String.format("이번 주 누적 근무시간이 %.1f시간입니다. 법정 상한(52시간)이 임박했습니다!",
                        event.getTotalWorkHours());
                break;
            case "NOTICE":
            default:
                title = "⚠ 근무시간 초과 주의";
                message = String.format("이번 주 누적 근무시간이 %.1f시간입니다. 52시간을 초과하지 않도록 주의해주세요!",
                        event.getTotalWorkHours());
                break;
        }

        log.info("[근무시간 {}] 사원ID: {}, 누적시간: {}h",
                event.getWarningLevel(), event.getEmployeeId(), event.getTotalWorkHours());

        NotificationEvent notification = NotificationEvent.builder()
                .employeeId(event.getEmployeeId())
                .type("attendance")
                .title(title)
                .message(message)
                .link("/attendance")
                .build();

        eventPublisher.publishEvent(notification);
    }
}