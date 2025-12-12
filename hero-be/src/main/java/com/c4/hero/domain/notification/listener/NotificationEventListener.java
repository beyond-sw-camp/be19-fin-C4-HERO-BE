package com.c4.hero.domain.notification.listener;

import com.c4.hero.common.event.NotificationEvent;
import com.c4.hero.domain.notification.dto.NotificationRegistDTO;
import com.c4.hero.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * <pre>
 * Class Name: NotificationEventListener
 * Description: 알림 이벤트 리스너
 *              다른 도메인에서 발행한 알림 이벤트를 수신하여 알림 생성 및 전송
 *
 * History
 * 2025/12/11 (최혜원) 최초 작성
 * </pre>
 *
 * @author 최혜원
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    /**
     * 알림 이벤트 처리
     * TransactionalEventListener: 트랜잭션 커밋 후에 이벤트 처리 (AFTER_COMMIT)
     * Async: 비동기 처리로 메인 로직에 영향 없음
     *
     * @param event NotificationEvent 알림 이벤트
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationEvent(NotificationEvent event) {

        log.info("알림 이벤트 수신: type={}, employeeId={}", event.getType(), event.getEmployeeId());

        try {
            NotificationRegistDTO notificationRegistDTO = NotificationRegistDTO.builder()
                    .employeeId(event.getEmployeeId())
                    .type(event.getType())
                    .title(event.getTitle())
                    .message(event.getMessage())
                    .link(event.getLink())
                    .attendanceId(event.getAttendanceId())
                    .payrollId(event.getPayrollId())
                    .documentId(event.getDocumentId())
                    .evaluationId(event.getEvaluationId())
                    .build();

                notificationService.registAndSendNotification(notificationRegistDTO);

        } catch (Exception e) {
            log.error("알림 생성 실패: {}", e.getMessage(), e);
            // 알림 실패해도 메인 로직에는 영향 없음 (비동기)
        }
    }
}