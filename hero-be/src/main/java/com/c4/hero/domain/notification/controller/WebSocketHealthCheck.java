package com.c4.hero.domain.notification.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <pre>
 * Class Name: WebSocketHealthCheck
 * Description: WebSocket 설정 확인을 위한 헬스체크 컨트롤러
 *
 * History
 * 2025/12/11 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@RestController
public class WebSocketHealthCheck {

    /**
     * WebSocket 엔드포인트 설정 확인
     *
     * @return String WebSocket 설정 상태 메시지
     */
    @GetMapping("/ws/health")
    public String health() {
        return "WebSocket endpoints are configured";
    }
}