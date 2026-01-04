package com.c4.hero.domain.settings.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: SettingsNotificationHistoryResponseDTO
 * Description: 알림 발송 이력 응답 DTO
 *
 * History
 * 2025/12/22 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingsNotificationHistoryResponseDTO {

    /**
     * 발송 이력 ID
     */
    private Integer historyId;

    /**
     * 알림 제목
     */
    private String title;

    /**
     * 알림 메시지
     */
    private String message;

    /**
     * 알림 타입
     */
    private String type;

    /**
     * 발송 범위 (ALL, GROUP, INDIVIDUAL)
     */
    private String scope;

    /**
     * 대상 수
     */
    private Integer targetCount;

    /**
     * 성공 수
     */
    private Integer successCount;

    /**
     * 실패 수
     */
    private Integer failureCount;

    /**
     * 발송자 이름
     */
    private String senderName;

    /**
     * 발송 시간
     */
    private LocalDateTime sentAt;
}