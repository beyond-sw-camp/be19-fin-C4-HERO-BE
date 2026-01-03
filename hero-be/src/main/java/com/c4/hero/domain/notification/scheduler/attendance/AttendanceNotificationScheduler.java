package com.c4.hero.domain.notification.scheduler.attendance;

import com.c4.hero.domain.notification.event.attendance.AttendanceNotificationEvent;
import com.c4.hero.domain.attendance.mapper.AttendanceMapper;
import com.c4.hero.domain.notification.service.AttendanceNotificationEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttendanceNotificationScheduler {

    private final AttendanceNotificationEventService attendanceNotificationEventService;
    private final ApplicationEventPublisher eventPublisher;
    private final AttendanceMapper attendanceMapper;

    /**
     * 테스트용: 매 1분마다 출근 미체크 확인
     * 운영 시 주석 처리
     */
    /*
    @Scheduled(cron = "0 * * * * *")
    public void testCheckClockInMissing() {
        checkAndNotifyClockInMissing("테스트-매1분");
    }
    */

    /**
     * 매일 10:00 출근 미체크자 확인 (월~금)
     */
    @Scheduled(cron = "0 0 10 * * MON-FRI")
    public void checkClockInMissing10AM() {
        checkAndNotifyClockInMissing("10:00");
    }

    /**
     * 매일 11:00 출근 미체크자 확인 (월~금)
     */
    @Scheduled(cron = "0 0 11 * * MON-FRI")
    public void checkClockInMissing11AM() {
        checkAndNotifyClockInMissing("11:00");
    }

    /**
     * 매일 14:00(오후 2시) 출근 미체크자 확인 (월~금)
     */
    @Scheduled(cron = "0 0 14 * * MON-FRI")
    public void checkClockInMissing2PM() {
        checkAndNotifyClockInMissing("14:00");
    }

    /**
     * 출근 미체크 확인 및 알림 발송 공통 메서드
     */
    private void checkAndNotifyClockInMissing(String time) {
        log.info("[스케줄러 {}] 출근 미체크 확인 시작", time);

        LocalDate today = LocalDate.now();
        List<Map<String, Object>> missingList = attendanceNotificationEventService.findClockInMissingEmployees(today);

        log.info("[{}] 출근 미체크: {}명", time, missingList.size());

        if (missingList.isEmpty()) {
            log.info("[{}] 모든 직원이 출근했습니다!", time);
            return;
        }

        missingList.forEach(map -> {
            Integer employeeId = (Integer) map.get("employeeId");
            Integer attendanceId = (Integer) map.get("attendanceId");

            log.info("[{}] 출근 미체크 알림 발송 - employeeId: {}", time, employeeId);

            AttendanceNotificationEvent.ClockInMissingEvent event = AttendanceNotificationEvent.ClockInMissingEvent.builder()
                    .employeeId(employeeId)
                    .attendanceId(attendanceId)
                    .workDate(today.toString())
                    .build();

            eventPublisher.publishEvent(event);
        });

        log.info("[스케줄러 {}] 출근 미체크 알림 발송 완료: {}건", time, missingList.size());
    }

}