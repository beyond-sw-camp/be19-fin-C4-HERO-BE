package com.c4.hero.domain.notification.config;

import com.c4.hero.domain.notification.handler.StompHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * <pre>
 * Class Name: WebSocketConfig
 * Description: WebSocket 및 STOMP 메시징 설정
 *              실시간 알림 전송을 위한 WebSocket 엔드포인트 및 메시지 브로커 구성
 *
 * History
 * 2025/12/11 (혜원) 최초 작성
 * 2025/12/25 (혜원) StompHandler 인터셉터 추가
 * </pre>
 *
 * @author 혜원
 * @version 1.1
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    /**
     * 메시지 브로커 설정
     *
     * @param registry MessageBrokerRegistry 메시지 브로커 레지스트리
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        // 서버 -> 클라이언트 메시지 브로드캐스팅 접두사
        // 클라이언트는 /topic/notifications/{employeeId} 형태로 구독
        registry.enableSimpleBroker("/topic");

        // 클라이언트 -> 서버 메시지 전송 접두사
        // 클라이언트는 /app/notifications/read 형태로 메시지 전송
        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * STOMP 엔드포인트 등록
     *
     * @param registry StompEndpointRegistry STOMP 엔드포인트 레지스트리
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/ws/notifications")  // WebSocket 연결 엔드포인트
                .setAllowedOriginPatterns("*")
                .withSockJS();  // WebSocket 미지원 브라우저 대응
    }

    /**
     * 클라이언트 인바운드 채널 설정
     * STOMP 메시지 인터셉터 등록 (JWT 인증 처리)
     *
     * @param registration ChannelRegistration 채널 등록 객체
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);  // StompHandler 등록
    }
}