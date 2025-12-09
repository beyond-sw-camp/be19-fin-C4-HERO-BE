package com.c4.hero.domain.notification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        // 메세지 브로드캐스팅을 위한 접두사
        registry.enableSimpleBroker("/topic");
        // 클라이언트-서버 통신을 위한 접두사
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/ws") // WebSocket 엔드포인트
                .setAllowedOrigins("https://localhost:5173") // 프론트엔드 origin 허용
                .withSockJS();
    }

}
