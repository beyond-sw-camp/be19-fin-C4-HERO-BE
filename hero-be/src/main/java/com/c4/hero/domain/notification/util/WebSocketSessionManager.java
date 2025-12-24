package com.c4.hero.domain.notification.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <pre>
 * Class Name: WebSocketSessionManager
 * Description: WebSocket 세션 관리자
 *
 * History
 * 2025/12/22 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Component
@Slf4j
public class WebSocketSessionManager {

    /**
     * 세션 정보를 저장하는 맵 (employeeId -> SessionInfo)
     */
    private final Map<Integer, SessionInfo> sessions = new ConcurrentHashMap<>();

    /**
     * 응답 시간 누적값 (성능 측정용)
     */
    private final AtomicLong totalResponseTime = new AtomicLong(0);

    /**
     * 응답 횟수 (평균 계산용)
     */
    private final AtomicLong responseCount = new AtomicLong(0);

    /**
     * 세션 정보 내부 클래스
     */
    @Getter
    @Builder
    @AllArgsConstructor
    public static class SessionInfo {
        private String sessionId;
        private Integer employeeId;
        private String employeeName;
        private LocalDateTime connectedAt;
    }

    /**
     * 세션 추가
     *
     * @param sessionId    세션 ID
     * @param employeeId   직원 ID
     * @param employeeName 직원 이름
     */
    public void addSession(String sessionId, Integer employeeId, String employeeName) {
        SessionInfo sessionInfo = SessionInfo.builder()
                .sessionId(sessionId)
                .employeeId(employeeId)
                .employeeName(employeeName)
                .connectedAt(LocalDateTime.now())
                .build();

        sessions.put(employeeId, sessionInfo);
        log.info("WebSocket session added: employeeId={}, sessionId={}", employeeId, sessionId);
    }

    /**
     * 세션 제거
     *
     * @param employeeId 직원 ID
     */
    public void removeSession(Integer employeeId) {
        SessionInfo removed = sessions.remove(employeeId);
        if (removed != null) {
            log.info("WebSocket session removed: employeeId={}, sessionId={}", employeeId, removed.getSessionId());
        }
    }

    /**
     * 모든 세션 조회
     *
     * @return 세션 정보 목록
     */
    public List<SessionInfo> getAllSessions() {
        return new ArrayList<>(sessions.values());
    }

    /**
     * 현재 활성 연결 수 조회
     *
     * @return 활성 연결 수
     */
    public int getActiveConnectionCount() {
        return sessions.size();
    }

    /**
     * 특정 직원의 세션 존재 여부 확인
     *
     * @param employeeId 직원 ID
     * @return 세션 존재 여부
     */
    public boolean hasSession(Integer employeeId) {
        return sessions.containsKey(employeeId);
    }

    /**
     * 응답 시간 기록 (성능 측정용)
     *
     * @param responseTimeMs 응답 시간 (밀리초)
     */
    public void recordResponseTime(long responseTimeMs) {
        totalResponseTime.addAndGet(responseTimeMs);
        responseCount.incrementAndGet();
    }

    /**
     * 평균 응답 시간 조회
     *
     * @return 평균 응답 시간 (밀리초)
     */
    public Long getAverageResponseTime() {
        long count = responseCount.get();
        if (count == 0) {
            return 0L;
        }
        return totalResponseTime.get() / count;
    }
}