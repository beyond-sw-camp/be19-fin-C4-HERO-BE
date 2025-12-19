package com.c4.hero.domain.auth.controller;

import com.c4.hero.common.response.ApiResponse;
import com.c4.hero.domain.auth.service.AuthService;
import com.c4.hero.domain.auth.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <pre>
 * Class Name: AuthController
 * Description: 인증 관련 API를 처리하는 컨트롤러 (토큰 재발급, 로그아웃 등)
 *
 * History
 * 2025-12-09 (이승건) 최초 작성
 * 2025-12-10 (이승건) Refresh Token을 쿠키에서 읽도록 수정
 * 2025-12-10 (이승건) 로그아웃 기능 추가
 * </pre>
 *
 * @author 이승건
 * @version 1.1
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    /**
     * 토큰 검증을 위한 테스트 메소드
     *
     * @return "test success" 문자열
     */
    @GetMapping("/test")
    public String test(HttpServletRequest request) {

        String token = jwtUtil.resolveToken(request);

        log.info("토큰 정보 확인");

        log.info(jwtUtil.getUsername(token));
        log.info(jwtUtil.getEmployeeId(token).toString());
        log.info(jwtUtil.getEmployeeNumber(token));
        log.info(jwtUtil.getEmployeeName(token));
        log.info(jwtUtil.getDepartmentId(token).toString());
        log.info(jwtUtil.getDepartmentName(token));
        log.info(jwtUtil.getGradeId(token).toString());
        log.info(jwtUtil.getGradeName(token));
        log.info(jwtUtil.getJobTitleId(token).toString());
        log.info(jwtUtil.getJobTitleName(token));

        return "test success";
    }


    /**
     * Access Token 재발급
     *
     * @param refreshToken HttpOnly 쿠키에 담긴 Refresh Token
     * @return 새로 발급된 Access Token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refreshAccessToken(
            @CookieValue(JwtUtil.REFRESH_TOKEN_COOKIE_NAME) String refreshToken) {

        String newAccessToken = authService.refreshAccessToken(refreshToken);

        // 새로 발급된 Access Token을 응답 본문에 담아 반환
        Map<String, String> responseBody = Map.of("accessToken", newAccessToken);
        return ResponseEntity.ok(ApiResponse.success(responseBody));
    }

    /**
     * 로그아웃
     *
     * @param response HttpServletResponse
     * @return 성공 메시지
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        // Refresh Token이 담긴 쿠키를 만료시켜 클라이언트에서 삭제하도록 함
        response.addCookie(createExpiredCookie());
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 만료된 쿠키를 생성하여 클라이언트의 Refresh Token 쿠키를 삭제
     * @return 만료된 쿠키 객체
     */
    private Cookie createExpiredCookie() {
        Cookie cookie = new Cookie(JwtUtil.REFRESH_TOKEN_COOKIE_NAME, null);
        cookie.setMaxAge(0); // 쿠키 만료 시간을 0으로 설정하여 즉시 삭제
        cookie.setPath("/");
        return cookie;
    }
}
