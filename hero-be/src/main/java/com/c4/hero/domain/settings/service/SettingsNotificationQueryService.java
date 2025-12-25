package com.c4.hero.domain.settings.service;

import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.settings.dto.response.SettingsNotificationHistoryResponseDTO;
import com.c4.hero.domain.settings.dto.response.SettingsNotificationStatisticsResponseDTO;
import com.c4.hero.domain.settings.dto.response.SettingsWebSocketHealthResponseDTO;
import com.c4.hero.domain.settings.mapper.SettingsMapper;
import com.c4.hero.domain.notification.util.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <pre>
 * Class Name: SettingsNotificationQueryService
 * Description: 관리자 알림 조회 전용 서비스
 *
 * History
 * 2025/12/24 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class SettingsNotificationQueryService {

    private final SettingsMapper settingsMapper;
    private final WebSocketSessionManager webSocketSessionManager;

    /**
     * 알림 발송 이력 조회 (페이징)
     *
     * @param pageable  페이징 정보
     * @param startDate 조회 시작일 (optional, yyyy-MM-dd)
     * @param endDate   조회 종료일 (optional, yyyy-MM-dd)
     * @param type      알림 타입 (optional)
     * @return 발송 이력 페이지 응답
     */
    public PageResponse<SettingsNotificationHistoryResponseDTO> getNotificationHistory(
            Pageable pageable, String startDate, String endDate, String type) {

        log.info("Fetching notification history - startDate: {}, endDate: {}, type: {}", startDate, endDate, type);

        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("type", type);

        List<SettingsNotificationHistoryResponseDTO> content =
                settingsMapper.findNotificationHistory(params, pageable);
        int total = settingsMapper.countNotificationHistory(params);

        return PageResponse.of(content, pageable.getPageNumber(), pageable.getPageSize(), total);
    }

    /**
     * 알림 발송 통계 조회
     *
     * @return 발송 통계 정보
     */
    public SettingsNotificationStatisticsResponseDTO getNotificationStatistics() {
        log.info("Fetching notification statistics");

        SettingsNotificationStatisticsResponseDTO statistics =
                settingsMapper.selectNotificationStatistics();

        if (statistics == null) {
            statistics = createEmptyStatistics();
        }

        // WebSocket 활성 연결 수 추가
        statistics.setActiveConnections(webSocketSessionManager.getActiveConnectionCount());

        // 가장 많이 발송된 타입 조회 (통계가 있을 때만)
        if (statistics.getTotalCount() > 0) {
            String mostSentType = settingsMapper.findMostSentType();
            statistics.setMostSentType(mostSentType);
        }

        return statistics;
    }

    /**
     * WebSocket 연결 상태 Health Check
     *
     * @return WebSocket 연결 상태 정보
     */
    public SettingsWebSocketHealthResponseDTO checkWebSocketHealth() {
        log.info("Checking WebSocket health");

        // WebSocket 세션 정보 조회
        List<SettingsWebSocketHealthResponseDTO.WebSocketSessionInfo> sessions =
                webSocketSessionManager.getAllSessions().stream()
                        .map(session -> SettingsWebSocketHealthResponseDTO.WebSocketSessionInfo.builder()
                                .sessionId(session.getSessionId())
                                .employeeId(session.getEmployeeId())
                                .employeeName(session.getEmployeeName())
                                .connectedAt(session.getConnectedAt())
                                .build())
                        .collect(Collectors.toList());

        return SettingsWebSocketHealthResponseDTO.builder()
                .status("UP")
                .activeConnections(sessions.size())
                .maxConnections(1000) // 설정값
                .averageResponseTime(webSocketSessionManager.getAverageResponseTime())
                .lastCheckTime(LocalDateTime.now())
                .sessions(sessions)
                .build();
    }

    /**
     * 빈 통계 객체 생성
     */
    private SettingsNotificationStatisticsResponseDTO createEmptyStatistics() {
        SettingsNotificationStatisticsResponseDTO statistics = new SettingsNotificationStatisticsResponseDTO();
        statistics.setTodayCount(0);
        statistics.setWeekCount(0);
        statistics.setMonthCount(0);
        statistics.setTotalCount(0);
        statistics.setAverageSuccessRate(0.0);
        statistics.setActiveConnections(0);
        return statistics;
    }
}