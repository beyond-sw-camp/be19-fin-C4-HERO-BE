package com.c4.hero.domain.settings.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: SettingsNotificationBroadcastRequestDTO
 * Description: 전체 직원 대상 알림 발송 요청 DTO
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
public class SettingsNotificationBroadcastRequestDTO {

    /**
     * 알림 제목
     */
    private String title;

    /**
     * 알림 메시지
     */
    private String message;

    /**
     * 알림 타입 (SYSTEM, ANNOUNCEMENT 등)
     */
    private String type;

    /**
     * 연결 링크 (optional)
     */
    private String link;
}