package com.c4.hero.domain.notification.util;

import com.c4.hero.domain.notification.dto.NotificationSettingsDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * Class Name: NotificationSettingsValidator
 * Description: 알림 설정 검증 유틸리티
 *
 * History
 * 2025/12/17 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Slf4j
public class NotificationSettingsValidator {

    /**
     * 알림 타입이 활성화되어 있는지 확인
     *
     * @param settings 알림 설정
     * @param type 알림 타입
     * @return 활성화 여부
     */
    public static boolean isNotificationEnabled(NotificationSettingsDTO settings, String type) {
        if (settings == null) {
            log.warn("알림 설정이 null입니다. 기본값(true) 반환");
            return true;
        }

        switch (type) {
            case "attendance":
                return settings.getAttendanceEnabled();

            case "payroll":
                return settings.getPayrollEnabled();

            case "document":
                return settings.getApprovalEnabled();

            case "evaluation":
                return settings.getEvaluationEnabled();

            case "system":
                return settings.getSystemEnabled();

            default:
                log.warn("알 수 없는 알림 타입: {}", type);
                return true; // 기본적으로 활성화
        }
    }
}