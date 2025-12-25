package com.c4.hero.domain.settings.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <pre>
 * Class Name: SettingsNotificationStatisticsResponseDTO
 * Description: 알림 발송 통계 응답 DTO
 *
 * History
 * 2025/12/22 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingsNotificationStatisticsResponseDTO {

    /**
     * 오늘 발송 건수
     */
    private Integer todayCount;

    /**
     * 이번 주 발송 건수
     */
    private Integer weekCount;

    /**
     * 이번 달 발송 건수
     */
    private Integer monthCount;

    /**
     * 전체 발송 건수
     */
    private Integer totalCount;

    /**
     * 평균 성공률 (%)
     */
    private Double averageSuccessRate;

    /**
     * 현재 WebSocket 연결 수
     */
    private Integer activeConnections;

    /**
     * 가장 많이 발송된 알림 타입
     */
    private String mostSentType;

}

