package com.c4.hero.common.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * <pre>
 * Class Name: WebMvcConfig
 * Description: Spring MVC 설정
 *
 * - 정적 리소스 경로 설정
 * - 파일 업로드 경로 매핑
 *
 * History
 * 2025/11/28 (혜원) 최초 작성
 * 2025/12/08 (승민) addCorsMappings 메서드 추가
 * 2025/12/10 (승민) addCorsMappings 메서드 삭제
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 정적 리소스 핸들러 설정
     * 파일 업로드 경로를 URL로 접근 가능하도록 매핑
     *
     * @param registry ResourceHandlerRegistry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /uploads/** URL로 접근 시 ./uploads/ 폴더의 파일 반환
        // 예: http://localhost:8080/uploads/profile/image.jpg
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");

        // /files/** URL로 접근 시 ./files/ 폴더의 파일 반환
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:./files/");
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
