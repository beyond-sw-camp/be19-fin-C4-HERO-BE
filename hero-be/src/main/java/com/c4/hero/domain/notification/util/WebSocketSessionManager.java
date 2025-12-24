package com.c4.hero.domain.notification.util;

import jakarta.annotation.PostConstruct;
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
 * Description: WebSocket 세션 관리 및 모니터링 컴포넌트
 *              실시간 알림을 위한 사용자 세션을 추적하고 연결 상태를 모니터링
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
     * 직원 ID를 키로 하는 세션 정보 저장소
     * ConcurrentHashMap을 사용하여 멀티 스레드 환경에서 안전하게 세션 관리
     */
    private final Map<Integer, SessionInfo> sessions = new ConcurrentHashMap<>();

    /**
     * 세션 ID를 키로 직원 ID를 조회하기 위한 역방향 매핑
     * WebSocket 연결 해제 시 세션 ID로 직원 ID를 빠르게 찾기 위함
     */
    private final Map<String, Integer> sessionIdToEmployeeId = new ConcurrentHashMap<>();

    /**
     * 전체 응답 시간 누적값 (밀리초 단위)
     * AtomicLong을 사용하여 락 없이 스레드 안전한 증가 연산 수행
     */
    private final AtomicLong totalResponseTime = new AtomicLong(0);

    /**
     * 응답 횟수 카운터
     * 평균 응답 시간 계산을 위한 분모 값
     */
    private final AtomicLong responseCount = new AtomicLong(0);

    /**
     * 빈 초기화 후 실행되는 초기화 메서드
     * 싱글톤 인스턴스의 해시코드를 로그로 출력하여 인스턴스 추적
     */
    @PostConstruct
    public void init() {
        log.info("WebSocketSessionManager initialized. HashCode: {}", this.hashCode());
    }

    /**
     * WebSocket 세션 정보를 담는 내부 클래스
     * 연결된 사용자의 세션 ID, 직원 정보, 연결 시간을 저장
     */
    @Getter
    @Builder
    @AllArgsConstructor
    public static class SessionInfo {
        /** WebSocket 세션 ID */
        private String sessionId;

        /** 연결된 직원의 ID */
        private Integer employeeId;

        /** 연결된 직원의 이름 */
        private String employeeName;

        /** 연결 시작 시간 */
        private LocalDateTime connectedAt;
    }

    /**
     * 새로운 WebSocket 세션을 등록
     *
     * @param sessionId WebSocket 세션 ID
     * @param employeeId 직원 ID
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
        sessionIdToEmployeeId.put(sessionId, employeeId);
        log.info("Session added. EmployeeId: {}, Current size: {}, Manager HashCode: {}",
                employeeId, sessions.size(), this.hashCode());
    }

    /**
     * 직원 ID로 세션 제거
     *
     * @param employeeId 제거할 세션의 직원 ID
     */
    public void removeSession(Integer employeeId) {
        SessionInfo removed = sessions.remove(employeeId);
        if (removed != null) {
            sessionIdToEmployeeId.remove(removed.getSessionId());
            log.info("Session removed by EmployeeId: {}. Current size: {}, Manager HashCode: {}",
                    employeeId, sessions.size(), this.hashCode());
        } else {
            log.warn("Attempted to remove non-existent session for EmployeeId: {}", employeeId);
        }
    }

    /**
     * WebSocket 세션 ID로 세션 제거
     * WebSocket 연결 해제 이벤트에서 주로 사용
     *
     * @param sessionId 제거할 WebSocket 세션 ID
     */
    public void removeSessionBySessionId(String sessionId) {
        Integer employeeId = sessionIdToEmployeeId.remove(sessionId);
        if (employeeId != null) {
            sessions.remove(employeeId);
            log.info("Session removed by SessionId: {}. EmployeeId: {}, Current size: {}, Manager HashCode: {}",
                    sessionId, employeeId, sessions.size(), this.hashCode());
        } else {
            log.warn("Attempted to remove non-existent session for SessionId: {}", sessionId);
        }
    }

    /**
     * 현재 활성화된 모든 세션 정보 조회
     *
     * @return 모든 세션 정보의 리스트 (복사본)
     */
    public List<SessionInfo> getAllSessions() {
        return new ArrayList<>(sessions.values());
    }

    /**
     * 현재 활성 연결 수 조회
     * WebSocket Health Check 및 통계에서 사용
     *
     * @return 현재 연결된 세션 수
     */
    public int getActiveConnectionCount() {
        int count = sessions.size();
        log.debug("getActiveConnectionCount called. Current size: {}, Manager HashCode: {}",
                count, this.hashCode());
        return count;
    }

    /**
     * 특정 직원의 세션 존재 여부 확인
     * 알림 발송 전 연결 상태 체크에 사용
     *
     * @param employeeId 확인할 직원 ID
     * @return 세션이 존재하면 true, 그렇지 않으면 false
     */
    public boolean hasSession(Integer employeeId) {
        return sessions.containsKey(employeeId);
    }

    /**
     * 응답 시간 기록
     * WebSocket 메시지 전송 시 소요 시간을 누적하여 평균 계산에 사용
     *
     * @param responseTimeMs 응답 시간 (밀리초)
     */
    public void recordResponseTime(long responseTimeMs) {
        totalResponseTime.addAndGet(responseTimeMs);
        responseCount.incrementAndGet();
        log.trace("Response time recorded: {}ms. Total: {}, Count: {}",
                responseTimeMs, totalResponseTime.get(), responseCount.get());
    }

    /**
     * 평균 응답 시간 계산
     * Health Check API에서 WebSocket 성능 모니터링에 사용
     *
     * @return 평균 응답 시간 (밀리초), 기록이 없으면 0 반환
     */
    public Long getAverageResponseTime() {
        long count = responseCount.get();
        if (count == 0) {
            return 0L;
        }
        long average = totalResponseTime.get() / count;
        log.debug("Average response time calculated: {}ms (total: {}, count: {})",
                average, totalResponseTime.get(), count);
        return average;
    }
}