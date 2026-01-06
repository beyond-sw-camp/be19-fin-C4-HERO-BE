package com.c4.hero.domain.auth.security;

import com.c4.hero.domain.auth.dto.RequestLoginDTO;
import com.c4.hero.domain.auth.dto.response.LoginResponseDTO;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

/**
 * <pre>
 * Class Name: AuthenticationFilter
 * Description: 사용자의 로그인 요청을 가로채 인증을 처리하는 필터
 *
 * History
 * 2025-12-09 (승건) 최초 작성
 * 2025-12-10 (승건) Refresh Token을 HttpOnly 쿠키로 전달하도록 수정
 * 2025-12-29 (승건) 로그인 응답에 passwordChangeRequired 추가
 * </pre>
 *
 * @author 이승건
 * @version 1.2
 */
@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    /**
     * 로그인 요청 시 인증을 시도하는 메소드
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // 1. 요청 본문(JSON)을 DTO로 변환
            RequestLoginDTO loginDTO = objectMapper.readValue(request.getInputStream(), RequestLoginDTO.class);

            log.info("로그인 시도: {}", loginDTO.getAccount());

            // 2. 인증용 토큰(UsernamePasswordAuthenticationToken) 생성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginDTO.getAccount(), loginDTO.getPassword(), null);

            // 3. AuthenticationManager에 토큰을 전달하여 인증 시도
            // -> 내부적으로 UserDetailsService의 loadUserByUsername 실행
            return authenticationManager.authenticate(authenticationToken);

        } catch (AuthenticationException e) {
            // AuthenticationException은 그대로 던져서 unsuccessfulAuthentication이 처리하도록 함
            throw e;
        } catch (Exception e) {
            // 그 외 모든 예외(IOException, RuntimeException 등)는 AuthenticationServiceException으로 감싸서 던짐
            log.error("로그인 요청 처리 중 오류 발생", e);
            throw new AuthenticationServiceException("로그인 처리 중 시스템 오류가 발생했습니다.", e);
        }
    }

    /**
     * 인증 성공 시 호출되는 메소드
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공: {}", authResult.getName());

        // 1. Access Token, Refresh Token 생성
        String accessToken = jwtUtil.createAccessToken(authResult);
        String refreshToken = jwtUtil.createRefreshToken(authResult);
        log.info("accessToken: {}", accessToken);
        // 2. Access Token은 헤더에, Refresh Token은 HttpOnly 쿠키에 추가
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, JwtUtil.BEARER_PREFIX + accessToken);
        response.addCookie(createRefreshTokenCookie(refreshToken));

        // 3. 응답 본문 작성
        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
        LoginResponseDTO loginResponse = LoginResponseDTO.builder()
                .message("로그인 성공")
                .passwordChangeRequired(userDetails.isPasswordChangeRequired())
                .build();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(loginResponse));
    }

    /**
     * 인증 실패 시 호출되는 메소드
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.warn("로그인 실패: {}", failed.getMessage());

        String errorMessage = "로그인 실패";
        if (failed instanceof BadCredentialsException || failed.getCause() instanceof UsernameNotFoundException) {
            errorMessage = "아이디 또는 비밀번호가 일치하지 않습니다.";
        } else if (failed instanceof AuthenticationServiceException) {
            errorMessage = "시스템 오류로 인해 로그인을 처리할 수 없습니다.";
        } else {
            errorMessage = failed.getMessage();
        }

        // 401 Unauthorized 에러 응답
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\": \"로그인 실패\", \"message\": \"" + errorMessage + "\"}");
    }

    /**
     * Refresh Token을 담을 HttpOnly 쿠키 생성
     * @param token Refresh Token 값
     * @return 생성된 쿠키 객체
     */
    private Cookie createRefreshTokenCookie(String token) {
        Cookie cookie = new Cookie(JwtUtil.REFRESH_TOKEN_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS 환경에서만 쿠키 전송
        cookie.setPath("/"); // 모든 경로에서 쿠키 사용
        cookie.setMaxAge((int) (jwtUtil.getRefreshTokenExpirationTime() / 1000)); // 쿠키 만료 시간을 초 단위로 설정
        return cookie;
    }
}
