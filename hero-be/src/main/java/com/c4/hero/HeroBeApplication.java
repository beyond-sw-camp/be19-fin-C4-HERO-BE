package com.c4.hero;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

/**
 * <pre>
 * Class Name: HeroBeApplication
 * Description: Hero 백엔드 Spring Boot 애플리케이션 메인 클래스
 *
 * History
 * 2025/11/28 (혜원) 최초 작성
 * 2025/12/15 (혜원) 알림 삭제 스케줄링 기능 활성화
 * 2026/01/01 (혜원) 서버시간 서울로 강제
 * </pre>
 *
 * @author 혜원
 * @version 2.0
 */
@SpringBootApplication
@EnableScheduling // Spring 스케줄링 기능
public class HeroBeApplication {

    @PostConstruct
    public void started() {
        // 애플리케이션의 기본 시간대를 서울로 설정
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {
        SpringApplication.run(HeroBeApplication.class, args);
    }

}
