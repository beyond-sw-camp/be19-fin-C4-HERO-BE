package com.c4.hero.domain.settings.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <pre>
 * Class Name: SettingsWebSocketHealthResponseDTO
 * Description: WebSocket 연결 상태 응답 DTO
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
public class SettingsWebSocketHealthResponseDTO {

    /**
     * 서버 상태 (UP, DOWN)
     */
    private String status;

    /**
     * 현재 활성 연결 수
     */
    private Integer activeConnections;

    /**
     * 최대 연결 수
     */
    private Integer maxConnections;

    /**
     * 평균 응답 시간 (ms)
     */
    private Long averageResponseTime;

    /**
     * 마지막 Health Check 시간
     */
    private LocalDateTime lastCheckTime;

    /**
     * 연결된 사용자 세션 목록
     */
    private List<WebSocketSessionInfo> sessions;

    /**
     * WebSocket 세션 정보
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WebSocketSessionInfo {
        private String sessionId;
        private Integer employeeId;
        private String employeeName;
        private LocalDateTime connectedAt;
    }
}