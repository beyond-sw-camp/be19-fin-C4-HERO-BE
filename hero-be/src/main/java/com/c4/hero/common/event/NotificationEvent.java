package com.c4.hero.common.event;

import lombok.Getter;

/**
 * <pre>
 * Class Name: NotificationEvent
 * Description: 알림 이벤트 - 다른 도메인에서 알림 발행 시 사용
 *
 * History
 * 2025/12/11 (최혜원) 최초 작성
 * </pre>
 *
 * @author 최혜원
 * @version 1.0
 */
@Getter
public class NotificationEvent {
    private final Integer employeeId;
    private final String type;
    private final String title;
    private final String message;
    private final String link;
    private final Integer attendanceId;
    private final Integer payrollId;
    private final Integer documentId;
    private final Integer evaluationId;

    private NotificationEvent(Builder builder) {
        this.employeeId = builder.employeeId;
        this.type = builder.type;
        this.title = builder.title;
        this.message = builder.message;
        this.link = builder.link;
        this.attendanceId = builder.attendanceId;
        this.payrollId = builder.payrollId;
        this.documentId = builder.documentId;
        this.evaluationId = builder.evaluationId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer employeeId;
        private String type;
        private String title;
        private String message;
        private String link;
        private Integer attendanceId;
        private Integer payrollId;
        private Integer documentId;
        private Integer evaluationId;

        public Builder employeeId(Integer employeeId) {
            this.employeeId = employeeId;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder link(String link) {
            this.link = link;
            return this;
        }

        public Builder attendanceId(Integer attendanceId) {
            this.attendanceId = attendanceId;
            return this;
        }

        public Builder payrollId(Integer payrollId) {
            this.payrollId = payrollId;
            return this;
        }

        public Builder documentId(Integer documentId) {
            this.documentId = documentId;
            return this;
        }

        public Builder evaluationId(Integer evaluationId) {
            this.evaluationId = evaluationId;
            return this;
        }

        public NotificationEvent build() {
            return new NotificationEvent(this);
        }
    }
}