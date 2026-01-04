package com.c4.hero.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * Class Name: SwaggerConfig.java
 * Description: 스웨거 API 문서
 *
 * History
 * 2026/01/02 (민철) 스웨거 설정 클래스
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
/* 설명. http://localhost:5000/swagger-ui/index.html */
@OpenAPIDefinition(
        info = @Info(
                title = "HERO REST API 명세서",
                description = "HERO REST API를 설명하기 위한 명세서",
                version = "v1.0.0"
        )
)
@Configuration
public class SwaggerConfig {
}
