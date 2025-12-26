package com.c4.hero.domain.notification.handler;

import com.c4.hero.domain.auth.security.JwtUtil;
import com.c4.hero.domain.notification.util.WebSocketSessionManager;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

/**
 * <pre>
 * Class Name: StompHandler
 * Description: STOMP 프로토콜 메시지 인터셉터
 *              WebSocket 연결/해제 시점에 JWT 인증 및 세션 관리 수행
 *
 * History
 * 2025/12/22 (혜원) 최초 작성 - JWT 기반 WebSocket 인증 구현
 * </pre>
 *
 * @author 혜원
 * @version 1.1
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {

    /** JWT 토큰 검증 및 파싱 유틸리티 */
    private final JwtUtil jwtUtil;

    /** WebSocket 세션 관리자 */
    private final WebSocketSessionManager webSocketSessionManager;

    /**
     * 빈 초기화 후 실행되는 초기화 메서드
     * 주입된 WebSocketSessionManager 인스턴스의 해시코드를 로그로 출력하여 싱글톤 확인
     */
    @PostConstruct
    public void init() {
        log.info("StompHandler initialized. Injected Manager HashCode: {}",
                webSocketSessionManager.hashCode());
    }

    /**
     * STOMP 메시지 전송 전에 실행되는 인터셉터 메서드
     * CONNECT 명령 시 JWT 인증 및 세션 등록 수행
     * DISCONNECT 명령 시 세션 제거 수행
     *
     * @param message STOMP 메시지
     * @param channel 메시지 채널
     * @return 처리된 메시지 (인증 실패 시에도 메시지 전달하여 연결 자체는 허용)
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        log.debug("preSend called. Command: {}", accessor.getCommand());

        // WebSocket 연결 요청 처리
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            handleConnect(accessor);
        }
        // WebSocket 연결 해제 처리
        else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            handleDisconnect(accessor);
        }

        return message;
    }

    /**
     * WebSocket 연결 요청 처리
     * JWT 토큰을 검증하고 유효한 경우 세션 정보를 등록
     *
     * @param accessor STOMP 헤더 접근자
     */
    private void handleConnect(StompHeaderAccessor accessor) {
        log.info("STOMP CONNECT command received.");

        // Authorization 헤더에서 JWT 토큰 추출
        String token = extractToken(accessor);

        if (token == null) {
            log.warn("No valid token found in CONNECT request.");
            return;
        }

        try {
            // JWT 토큰 검증
            if (jwtUtil.validateToken(token)) {
                log.info("Token is valid.");

                // 토큰에서 사용자 정보 추출
                Integer employeeId = jwtUtil.getEmployeeId(token);
                String employeeName = jwtUtil.getEmployeeName(token);
                String sessionId = accessor.getSessionId();

                // 세션 등록
                log.info("Adding session. EmployeeId: {}, SessionId: {}, Manager HashCode: {}",
                        employeeId, sessionId, webSocketSessionManager.hashCode());
                webSocketSessionManager.addSession(sessionId, employeeId, employeeName);
            } else {
                log.warn("Token validation failed.");
            }
        } catch (Exception e) {
            log.error("Error during token validation or session creation", e);
        }
    }

    /**
     * WebSocket 연결 해제 처리
     * 세션 ID를 기반으로 등록된 세션 정보 제거
     *
     * @param accessor STOMP 헤더 접근자
     */
    private void handleDisconnect(StompHeaderAccessor accessor) {
        log.info("STOMP DISCONNECT command received.");

        String sessionId = accessor.getSessionId();

        log.info("Removing session. SessionId: {}, Manager HashCode: {}",
                sessionId, webSocketSessionManager.hashCode());
        webSocketSessionManager.removeSessionBySessionId(sessionId);
    }

    /**
     * STOMP 헤더에서 JWT 토큰 추출
     * "Bearer " 접두사를 제거하고 순수 토큰만 반환
     *
     * @param accessor STOMP 헤더 접근자
     * @return 추출된 JWT 토큰, 없으면 null
     */
    private String extractToken(StompHeaderAccessor accessor) {
        String token = accessor.getFirstNativeHeader(JwtUtil.AUTHORIZATION_HEADER);

        if (token == null) {
            log.debug("Authorization header not found.");
            return null;
        }

        // "Bearer " 접두사 제거
        if (token.startsWith(JwtUtil.BEARER_PREFIX)) {
            token = token.substring(7); // "Bearer ".length() == 7
        }

        log.debug("Token extracted: {}", token != null ? "Token exists" : "Token is null");
        return token;
    }
}