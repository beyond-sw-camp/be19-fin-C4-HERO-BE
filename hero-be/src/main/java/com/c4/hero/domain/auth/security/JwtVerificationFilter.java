package com.c4.hero.domain.auth.security;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * <pre>
 * Class Name: JwtVerificationFilter
 * Description: Access Token의 유효성을 검증하고, 유효한 경우 SecurityContext에 인증 정보를 저장하는 필터
 *
 * History
 * 2025-12-09 (이승건) 최초 작성
 * 2025-12-09 (이승건) 토큰 만료 시 BusinessException을 던지도록 변경
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
@Slf4j
@RequiredArgsConstructor
public class JwtVerificationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // 필터를 적용하지 않을 URL 목록
    private static final List<String> EXCLUDE_URL = List.of("/api/auth/refresh");

    /**
     * 요청 헤더에서 JWT Access Token을 추출하고 유효성을 검증하여 SecurityContext에 인증 정보를 저장합니다.
     * {@inheritDoc}
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 관련 예외 발생 시
     * @throws IOException 입출력 관련 예외 발생 시
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtUtil.resolveToken(request);

        if (StringUtils.hasText(accessToken)) {
            try {
                if (jwtUtil.validateToken(accessToken)) {
                    Authentication authentication = jwtUtil.getAuthentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), request.getRequestURI());
                }
            } catch (ExpiredJwtException e) {
                // 토큰이 만료된 경우, BusinessException을 던져 GlobalExceptionHandler에서 처리하도록 함
                log.warn("만료된 Access Token입니다. uri: {}", request.getRequestURI());
                throw new BusinessException(ErrorCode.ACCESS_TOKEN_EXPIRED);
            }
        }

        // 다음 필터로 제어 넘김
        filterChain.doFilter(request, response);
    }

    /**
     * 필터를 적용할 필요가 없는 URL인지 확인
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return EXCLUDE_URL.stream().anyMatch(url -> url.equals(request.getRequestURI()));
    }
}
