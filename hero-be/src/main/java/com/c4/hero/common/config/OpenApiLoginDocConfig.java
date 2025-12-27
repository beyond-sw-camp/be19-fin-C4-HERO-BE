package com.c4.hero.common.config;

import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiLoginDocConfig {

    @Bean
    public OpenApiCustomizer authLoginEndpointCustomizer() {
        return openApi -> {
            Paths paths = openApi.getPaths();
            if (paths == null) {
                paths = new Paths();
                openApi.setPaths(paths);
            }

            // RequestLoginDTO 기준: account / password
            Schema<?> loginRequestSchema = new ObjectSchema()
                    .addProperty("account", new StringSchema().example("tmdrjs0040"))
                    .addProperty("password", new StringSchema().example("QLkzblN5Ue"));

            RequestBody requestBody = new RequestBody()
                    .required(true)
                    .content(new Content().addMediaType(
                            "application/json",
                            new MediaType().schema(loginRequestSchema)
                    ));

            Schema<?> loginResponseSchema = new ObjectSchema()
                    .addProperty("message", new StringSchema().example("로그인 성공"))
                    .addProperty("accessToken", new StringSchema().example("Bearer eyJhbGciOi..."));

            ApiResponse ok = new ApiResponse()
                    .description("""
                            로그인 성공.
                            - Access Token: 응답 헤더 Authorization 에 포함
                            - Refresh Token: HttpOnly Cookie(refresh_token) 로 설정
                            """)
                    .content(new Content().addMediaType(
                            "application/json",
                            new MediaType().schema(loginResponseSchema)
                    ));

            Operation op = new Operation()
                    .summary("로그인")
                    .description("Spring Security AuthenticationFilter에서 처리되는 로그인 엔드포인트입니다.")
                    .requestBody(requestBody)
                    .responses(new ApiResponses().addApiResponse("200", ok));

            paths.addPathItem("/api/auth/login", new PathItem().post(op));
        };
    }
}
