package com.c4.hero.domain.notification.listener.approval;

import com.c4.hero.common.event.NotificationEvent;
import com.c4.hero.domain.notification.event.approval.ApprovalNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * <pre>
 * Class Name: ApprovalNotificationEventListener
 * Description: 결재 이벤트를 알림 이벤트로 변환하는 리스너
 *
 * History
 * 2026/01/02 (혜원) 최초 작성
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApprovalNotificationEventListener {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 결재 요청 도착 → 결재자에게 알림
     */
//    @Async
    @EventListener
    public void handleApprovalRequest(ApprovalNotificationEvent.ApprovalRequestEvent event) {
        log.info("[결재알림 변환] 결재 요청 - docId: {}, approverId: {}",
                event.getDocId(), event.getApproverId());

        NotificationEvent notification = NotificationEvent.builder()
                .employeeId(event.getApproverId())  // 결재자에게
                .type("approval")
                .title("새로운 결재 요청")
                .message(String.format("%s님의 '%s' 문서 결재 요청이 도착했습니다.",
                        event.getDrafterName(), event.getTitle()))
                .link("/approval/documents/" + event.getDocId())
                .documentId(event.getDocId())
                .build();

        eventPublisher.publishEvent(notification);
    }

    /**
     * 결재 승인 완료 → 기안자에게 알림
     */
//    @Async
    @EventListener
    public void handleApprovalCompleted(ApprovalNotificationEvent.ApprovalCompletedEvent event) {
        log.info("[결재알림 변환] 결재 승인 - docId: {}, drafterId: {}",
                event.getDocId(), event.getDrafterId());

        NotificationEvent notification = NotificationEvent.builder()
                .employeeId(event.getDrafterId())  // 기안자에게
                .type("approval")
                .title("결재 승인 완료")
                .message(String.format("'%s' 문서가 최종 승인되었습니다.", event.getTitle()))
                .link("/approval/documents/" + event.getDocId())
                .documentId(event.getDocId())
                .build();

        eventPublisher.publishEvent(notification);
    }

    /**
     * 결재 반려 → 기안자에게 알림
     */
//    @Async
    @EventListener
    public void handleApprovalRejected(ApprovalNotificationEvent.ApprovalRejectedEvent event) {
        log.info("[결재알림 변환] 결재 반려 - docId: {}, drafterId: {}",
                event.getDocId(), event.getDrafterId());

        String message = String.format("'%s' 문서가 반려되었습니다. 사유: %s",
                event.getTitle(), event.getComment());

        NotificationEvent notification = NotificationEvent.builder()
                .employeeId(event.getDrafterId())  // 기안자에게
                .type("approval")
                .title("결재 반려")
                .message(message)
                .link("/approval/documents/" + event.getDocId())
                .documentId(event.getDocId())
                .build();

        eventPublisher.publishEvent(notification);
    }

    /**
     * 결재 회수 완료 → 기안자에게 알림
     */
//    @Async
    @EventListener
    public void handleApprovalRecalled(ApprovalNotificationEvent.ApprovalRecalledEvent event) {
        log.info("[결재알림 변환] 결재 회수 - docId: {}, drafterId: {}",
                event.getDocId(), event.getDrafterId());

        NotificationEvent notification = NotificationEvent.builder()
                .employeeId(event.getDrafterId())  // 기안자에게
                .type("approval")
                .title("결재 회수 완료")
                .message(String.format("'%s' 문서가 성공적으로 회수되었습니다.", event.getTitle()))
                .link("/approval/documents/" + event.getDocId())
                .documentId(event.getDocId())
                .build();

        eventPublisher.publishEvent(notification);
    }

    /**
     * 결재 대기 독촉 → 결재자에게 알림
     */
//    @Async
    @EventListener
    public void handleApprovalReminder(ApprovalNotificationEvent.ApprovalReminderEvent event) {
        log.info("[결재알림 변환] 결재 독촉 - docId: {}, approverId: {}, waitingDays: {}",
                event.getDocId(), event.getApproverId(), event.getWaitingDays());

        String message = String.format("%s님의 '%s' 문서가 %d일째 결재 대기 중입니다.",
                event.getDrafterName(), event.getTitle(), event.getWaitingDays());

        NotificationEvent notification = NotificationEvent.builder()
                .employeeId(event.getApproverId())  // 결재자에게
                .type("approval")
                .title("결재 대기 독촉")
                .message(message)
                .link("/approval/documents/" + event.getDocId())
                .documentId(event.getDocId())
                .build();

        eventPublisher.publishEvent(notification);
    }
}