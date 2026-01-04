package com.c4.hero.common.config;

import com.c4.hero.domain.auth.security.AuthenticationFilter;
import com.c4.hero.domain.auth.security.JwtUtil;
import com.c4.hero.domain.auth.security.JwtVerificationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 * Class Name: SecurityConfig
 * Description: Spring Security 보안 설정
 *
 * - JWT 기반 인증 방식 사용
 * - CORS 설정
 * - 비밀번호 암호화 설정
 * - 웹소켓 설정
 *
 * History
 * 2025/11/28 (혜원) 최초 작성
 * 2025/12/09 (승건) 토큰 필터 추가
 * 2025/12/11 (혜원) WebSocket 설정 추가
 * 2025/12/14 (혜원) 개발 편의성을 위해 모든 시큐리티 허용
 * 2026/01/03 (동근) 급여 도메인 권한 인가 정책 구조 추가
 * </pre>
 *
 * @author 혜원
 * @version 1.4
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final ObjectMapper objectMapper;

    /**
     * Spring Security 필터 체인 설정
     * - CSRF 비활성화 (JWT 사용으로 불필요)
     * - CORS 설정 적용
     * - Stateless 세션 정책 (JWT 기반 인증)
     * - URL별 권한 설정
     *
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain
     * @throws Exception 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // AuthenticationManager 가져오기
        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

        // 로그인 필터 생성
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager, jwtUtil, objectMapper);
        authenticationFilter.setFilterProcessesUrl("/api/auth/login");

        http
                // CSRF 보호 비활성화 (JWT 사용)
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/ws/**")  // WebSocket 경로 CSRF 무시
                        .disable()
                )

                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 세션 사용 안 함 (Stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // URL별 권한 설정
                .authorizeHttpRequests(auth -> auth
//                                .requestMatchers("/ws/**").permitAll()
//                                .requestMatchers("/api/notifications/**").permitAll()  // 알림 API 추가
//                                .requestMatchers("/api/test/**").permitAll()  // 테스트 API
//                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Preflight 요청은 모두 허용
//                                .requestMatchers("/api/auth/**").permitAll()      // 인증 API는 모두 허용
//                                .requestMatchers("/api/auth/test").hasRole("EMPLOYEE")
//                                .anyRequest().permitAll()
//                        .requestMatchers("/api/public/**").permitAll()    // 공개 API 허용
                        //급여 권한 인가 처리 할때 요부분부터 (주석 해제할때 해당 텍스트 제거)
                        .requestMatchers("/api/admin/payroll/**")
                        .hasAnyRole("SYSTEM_ADMIN", "HR_MANAGER", "HR_PAYROLL") //관리자만 접근가능
                        .requestMatchers("/api/settings/payroll/**")
                        .hasAnyRole("SYSTEM_ADMIN", "HR_MANAGER", "HR_PAYROLL") //관리자만 접근가능
                       .requestMatchers("/api/me/payroll/**")
                        .authenticated() //로그인해야 접근 가능
                        // 급여 권한 인가 처리 할때 요기까지 (주석 해제할때 해당 텍스트 제거)
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // 관리자만 접근
//                        .anyRequest().authenticated()                      // 나머지는 인증 필요
                )
                // 커스텀 필터 추가
                // 1. 로그인 필터: UsernamePasswordAuthenticationFilter 위치에 추가
                .addFilterAt(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 2. JWT 검증 필터: 로그인 필터 이전에 추가
                .addFilterBefore(new JwtVerificationFilter(jwtUtil), AuthenticationFilter.class)
                // WebSocket을 위한 프레임 옵션 설정
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                );




        return http.build();
    }

    /**
     * 인증 관리자 Bean 등록
     *
     * @param configuration AuthenticationConfiguration
     * @return AuthenticationManager
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


    /**
     * 비밀번호 암호화 인코더
     * BCrypt 해시 알고리즘 사용
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CORS(Cross-Origin Resource Sharing) 설정
     * 프론트엔드에서 백엔드 API 호출 허용
     *
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 Origin (프론트엔드 주소)
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        configuration.setAllowedOrigins(Arrays.asList(

                "https://hero-hr.site",
                "http://localhost:8080" // 개발환경
        ));

        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // 허용할 헤더
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // 쿠키/인증 정보 포함 허용
        configuration.setAllowCredentials(true);

        // 클라이언트에서 접근할 수 있도록 헤더 노출
        configuration.setExposedHeaders(Arrays.asList(
                JwtUtil.AUTHORIZATION_HEADER
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}