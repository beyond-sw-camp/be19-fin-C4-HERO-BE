package com.c4.hero.domain.auth.service;

import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * <pre>
 * Interface Name: AuthService
 * Description: 인증 관련 비즈니스 로직을 정의하는 인터페이스
 *
 * - Spring Security의 UserDetailsService를 상속받아 사용자 조회 기능 구현
 *
 * History
 * 2025/12/09 (이승건) 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
public interface AuthService extends UserDetailsService {

    /**
     * Refresh Token을 사용하여 새로운 Access Token을 발급
     *
     * @param refreshToken Refresh Token
     * @return 새로 발급된 Access Token
     */
    String refreshAccessToken(String refreshToken);
}
