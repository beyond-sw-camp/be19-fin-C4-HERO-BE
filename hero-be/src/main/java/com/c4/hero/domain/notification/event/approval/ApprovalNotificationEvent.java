package com.c4.hero.domain.notification.event.approval;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: ApprovalNotificationEvent
 * Description: 결재 관련 알림 이벤트 통합 클래스
 *
 * History
 * 2026/01/02 (혜원) 결재 알림 이벤트 통합 및 신규 이벤트 추가
 * </pre>
 */
public class ApprovalNotificationEvent {

    /**
     * 결재 요청 도착 이벤트 (결재자에게)
     */
    @Getter
    @Builder
    public static class ApprovalRequestEvent {
        private Integer docId;              // 문서 ID
        private String templateKey;         // 문서 서식 키
        private String title;               // 문서 제목
        private Integer drafterId;          // 기안자 ID
        private String drafterName;         // 기안자 이름
        private Integer approverId;         // 결재자 ID (알림 수신자)
        private Integer seq;                // 결재 순서
        private LocalDateTime requestedAt;  // 요청 일시
    }

    /**
     * 결재 승인 완료 이벤트 (기안자에게)
     */
    @Getter
    @Builder
    public static class ApprovalCompletedEvent {
        private Integer docId;              // 문서 ID
        private String templateKey;         // 문서 서식 키
        private String title;               // 문서 제목
        private Integer drafterId;          // 기안자 ID (알림 수신자)
        private String details;             // JSON 상세 데이터
        private Integer approverId;         // 최종 승인자 ID
        private String approverName;        // 최종 승인자 이름
        private LocalDateTime completedAt;  // 승인 완료 일시
    }

    /**
     * 결재 반려 완료 이벤트 (기안자에게)
     */
    @Getter
    @Builder
    public static class ApprovalRejectedEvent {
        private Integer docId;              // 문서 ID
        private String templateKey;         // 문서 서식 키
        private String title;               // 문서 제목
        private Integer drafterId;          // 기안자 ID (알림 수신자)
        private String details;             // JSON 상세 데이터
        private Integer rejecterId;         // 반려자 ID
        private String rejecterName;        // 반려자 이름
        private String comment;             // 반려 사유
        private LocalDateTime rejectedAt;   // 반려 일시
    }

    /**
     * 결재 회수 완료 이벤트 (기안자에게)
     */
    @Getter
    @Builder
    public static class ApprovalRecalledEvent {
        private Integer docId;              // 문서 ID
        private String templateKey;         // 문서 서식 키
        private String title;               // 문서 제목
        private Integer drafterId;          // 기안자 ID (알림 수신자)
        private LocalDateTime recalledAt;   // 회수 일시
    }

    /**
     * 결재 대기 독촉 이벤트 (결재자에게)
     */
    @Getter
    @Builder
    public static class ApprovalReminderEvent {
        private Integer docId;              // 문서 ID
        private String templateKey;         // 문서 서식 키
        private String title;               // 문서 제목
        private Integer drafterId;          // 기안자 ID
        private String drafterName;         // 기안자 이름
        private Integer approverId;         // 결재자 ID (알림 수신자)
        private Integer waitingDays;        // 대기 일수
        private LocalDateTime requestedAt;  // 최초 요청 일시
    }
}