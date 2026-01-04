package com.c4.hero.domain.notification.listener.evaluation;

import com.c4.hero.common.event.NotificationEvent;
import com.c4.hero.domain.notification.event.evaluation.EvaluationNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * <pre>
 * Class Name: EvaluationNotificationEventListener
 * Description: 평가 이벤트를 알림 이벤트로 변환하는 리스너
 *
 * History
 * 2026/01/03 (혜원) 최초 작성
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EvaluationNotificationEventListener {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 평가 시작 → 피평가자들에게 알림
     */
    @EventListener
    public void handleEvaluationStarted(EvaluationNotificationEvent.EvaluationStartedEvent event) {
        log.info("[평가알림 변환] 평가 시작 - evaluationId: {}, 피평가자 수: {}",
                event.getEvaluationId(), event.getEmployeeIds().size());

        // 각 피평가자에게 개별 알림 발송
        for (Integer employeeId : event.getEmployeeIds()) {
            NotificationEvent notification = NotificationEvent.builder()
                    .employeeId(employeeId)  // 피평가자에게
                    .type("evaluation")
                    .title("새로운 평가 시작")
                    .message(String.format("'%s' 평가가 시작되었습니다.", event.getEvaluationName()))
                    .link("/evaluation/evaluation/" + event.getEvaluationId())
                    .evaluationId(event.getEvaluationId())
                    .build();

            eventPublisher.publishEvent(notification);
        }

        log.info("[평가알림 변환] 평가 시작 알림 발송 완료 - {}명", event.getEmployeeIds().size());
    }

    /**
     * 평가 결과 등록 → 피평가자에게 알림
     */
    @EventListener
    public void handleEvaluationGraded(EvaluationNotificationEvent.EvaluationGradedEvent event) {
        log.info("[평가알림 변환] 평가 결과 등록 - formId: {}, employeeId: {}, grade: {}",
                event.getFormId(), event.getEmployeeId(), event.getGrade());

        NotificationEvent notification = NotificationEvent.builder()
                .employeeId(event.getEmployeeId())  // 피평가자에게
                .type("evaluation")
                .title("평가 결과 등록")
                .message(String.format("'%s' 평가 결과가 등록되었습니다. (등급: %s)",
                        event.getEvaluationName(), event.getGrade()))
                .link("/evaluation/form/" + event.getFormId())
                .evaluationId(event.getEvaluationId())
                .build();

        eventPublisher.publishEvent(notification);
    }

    /**
     * 평가 마감 독촉 → 미제출 평가자에게 알림
     */
    @EventListener
    public void handleEvaluationReminder(EvaluationNotificationEvent.EvaluationReminderEvent event) {
        log.info("[평가알림 변환] 평가 독촉 - evaluationId: {}, evaluatorId: {}, remainingDays: {}",
                event.getEvaluationId(), event.getEvaluatorId(), event.getRemainingDays());

        String message = String.format("'%s' 평가 제출 마감이 %d일 남았습니다.",
                event.getEvaluationName(), event.getRemainingDays());

        NotificationEvent notification = NotificationEvent.builder()
                .employeeId(event.getEvaluatorId())  // 평가자에게
                .type("evaluation")
                .title("평가 제출 독촉")
                .message(message)
                .link("/evaluation/evaluation/" + event.getEvaluationId())
                .evaluationId(event.getEvaluationId())
                .build();

        eventPublisher.publishEvent(notification);
    }
}