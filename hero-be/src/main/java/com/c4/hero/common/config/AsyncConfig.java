package com.c4.hero.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * <pre>
 * Class Name: AsyncConfig
 * Description: 비동기 처리 설정
 *              @Async 애노테이션 활성화
 *
 * History
 * 2025/12/11 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    // @EnableAsync만 있으면 기본 설정으로 작동
    // 필요 시 ThreadPoolTaskExecutor 커스터마이징 가능
}