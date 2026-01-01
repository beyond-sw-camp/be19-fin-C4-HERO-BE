package com.c4.hero.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * <pre>
 * Class Name: AiClientConfig
 * Description: 파이썬 서버(Fast API)와 연동하기 위한 설정
 *
 * History
 * 2025/12/30 (김승민) 최초 작성
 * </pre>
 *
 * @author 승민
 * @version 1.0
 */

@Configuration
public class AiClientConfig {

    @Bean
    public WebClient aiWebClient(
            @Value("${ai.server.base-url}") String baseUrl
    ) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
