package com.c4.hero.domain.settings.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: SettingsNotificationHistory
 * Description: 알림 발송 이력 엔티티
 *
 * History
 * 2025/12/22 혜원 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Entity
@Table(name = "tbl_notification_history")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingsNotificationHistory {

    /**
     * 발송 이력 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Integer historyId;

    /**
     * 알림 제목
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 알림 메시지
     */
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    /**
     * 알림 타입 (SYSTEM, ANNOUNCEMENT 등)
     */
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    /**
     * 발송 범위 (ALL, GROUP, INDIVIDUAL)
     */
    @Column(name = "scope", nullable = false, length = 20)
    private String scope;

    /**
     * 대상 수
     */
    @Column(name = "target_count", nullable = false)
    private Integer targetCount;

    /**
     * 성공 수
     */
    @Column(name = "success_count", nullable = false)
    private Integer successCount;

    /**
     * 실패 수
     */
    @Column(name = "failure_count", nullable = false)
    private Integer failureCount;

    /**
     * 발송자 ID
     */
    @Column(name = "sender_id", nullable = false)
    private Integer senderId;

    /**
     * 발송 시간
     */
    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;
}