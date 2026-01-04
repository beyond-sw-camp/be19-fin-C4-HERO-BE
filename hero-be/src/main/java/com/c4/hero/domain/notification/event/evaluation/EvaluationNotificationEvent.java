package com.c4.hero.domain.notification.event.evaluation;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <pre>
 * Class Name: EvaluationNotificationEvent
 * Description: 평가 관련 알림 이벤트 통합 클래스
 *
 * History
 * 2026/01/03 (혜원) 최초 작성
 * </pre>
 */
public class EvaluationNotificationEvent {

    /**
     * 평가 시작 알림 이벤트 (피평가자들에게)
     */
    @Getter
    @Builder
    public static class EvaluationStartedEvent {
        private Integer evaluationId;       // 평가 ID
        private String evaluationName;      // 평가명
        private List<Integer> employeeIds;  // 피평가자 ID 목록 (알림 수신자들)
        private LocalDateTime startDate;    // 평가 시작일
        private LocalDateTime endDate;      // 평가 종료일
    }

    /**
     * 평가 결과 등록 알림 이벤트 (피평가자에게)
     */
    @Getter
    @Builder
    public static class EvaluationGradedEvent {
        private Integer formId;             // 평가서 ID
        private Integer evaluationId;       // 평가 ID
        private String evaluationName;      // 평가명
        private Integer employeeId;         // 피평가자 ID (알림 수신자)
        private String grade;               // 등급 (A, B, C 등)
        private LocalDateTime gradedAt;     // 채점 일시
    }

    /**
     * 평가 마감 독촉 알림 이벤트 (미제출 평가자에게)
     */
    @Getter
    @Builder
    public static class EvaluationReminderEvent {
        private Integer evaluationId;       // 평가 ID
        private String evaluationName;      // 평가명
        private Integer evaluatorId;        // 평가자 ID (알림 수신자)
        private Integer remainingDays;      // 남은 일수
        private LocalDateTime deadline;     // 마감일
    }
}