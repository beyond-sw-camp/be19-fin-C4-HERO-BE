package com.c4.hero.domain.auth.security;

import com.c4.hero.common.s3.S3Service;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * <pre>
 * Class Name: JwtUtil
 * Description: JWT 토큰의 생성, 검증, 정보 추출을 담당하는 유틸리티 클래스
 *
 * History
 * 2025/12/09 (승건) 최초 작성
 * 2025/12/09 (승건) 토큰 만료 예외 처리 추가
 * 2025/12/10 (승건) 권한 정보에 'ROLE_' 접두사 추가
 * 2025/12/17 (승건) 토큰 정보 추출 메소드 추가 및 employeeNumber 추가
 * 2025/12/22 (혜원) 기본 User 대신 CustomUserDetails 객체 반환하도록 수정
 * 2025/12/29 (승건) 비밀번호 재설정 토큰 생성 메서드 추가
 * </pre>
 *
 * @author 이승건
 * @version 1.3
 */
@Slf4j
@Getter
@Component
public class JwtUtil {

    private final Key key;
    private final long accessTokenExpirationTime;
    private final long refreshTokenExpirationTime;
    private final S3Service s3Service;

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final String BEARER_PREFIX = "Bearer ";

    public JwtUtil(@Value("${token.secret}") String secretKey,
                   @Value("${token.access-expiration-time}") long accessTokenExpirationTime,
                   @Value("${token.refresh-expiration-time}") long refreshTokenExpirationTime,
                   S3Service s3Service) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
        this.s3Service = s3Service;
    }

    /**
     * Access Token 생성
     * @param auth 인증 정보
     * @return Access Token
     */
    public String createAccessToken(Authentication auth) {
        // 권한 정보에 "ROLE_" 접두사를 붙여서 문자열로 변환
        String authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(role -> !role.equals("FACTOR_PASSWORD"))
                .map(role -> "ROLE_" + role) // "ROLE_" 접두사 추가
                .collect(Collectors.joining(","));

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
        claims.put("auth", authorities);
        claims.put("employeeId", userDetails.getEmployeeId());
        claims.put("employeeNumber", userDetails.getEmployeeNumber());
        claims.put("employeeName", userDetails.getEmployeeName());
        claims.put("departmentId", userDetails.getDepartmentId());
        claims.put("departmentName", userDetails.getDepartmentName());
        claims.put("gradeId", userDetails.getGradeId());
        claims.put("gradeName", userDetails.getGradeName());
        claims.put("jobTitleId", userDetails.getJobTitleId());
        claims.put("jobTitleName", userDetails.getJobTitleName());
        claims.put("passwordChangeRequired", userDetails.isPasswordChangeRequired());
        claims.put("imagePath", s3Service.generatePresignedUrl(userDetails.getImagePath()));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Refresh Token 생성 (최소한의 정보만 담음)
     * @param auth 인증 정보
     * @return Refresh Token
     */
    public String createRefreshToken(Authentication auth) {
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());

        // Refresh Token에는 재발급에 필요한 최소한의 정보(subject)만 담는다.
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 비밀번호 재설정용 임시 토큰 생성
     * @param employeeNumber 사용자를 식별할 사번
     * @return 임시 토큰
     */
    public String createPasswordResetToken(String employeeNumber) {
        Claims claims = Jwts.claims().setSubject(employeeNumber);
        claims.put("type", "PASSWORD_RESET"); // 토큰 타입 지정

        long expirationTime = 10 * 60 * 1000; // 10분

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰 유효성 검사. 만료된 토큰은 ExpiredJwtException을 던집니다.
     * @param token 검증할 토큰
     * @return 유효 여부
     * @throws ExpiredJwtException 토큰이 만료된 경우
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.warn("유효하지 않은 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            // 만료된 토큰의 경우, 예외를 그대로 던져서 호출한 쪽에서 처리하도록 함
            throw e;
        } catch (UnsupportedJwtException e) {
            log.warn("지원하지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.warn("JWT 클레임이 비어있습니다.");
        }
        return false;
    }

    /**
     * 토큰에서 인증 객체 반환
     * @param accessToken Access Token
     * @return Authentication 객체
     */
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);
        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // passwordChangeRequired 값 추출 (기본값 false)
        Boolean passwordChangeRequired = claims.get("passwordChangeRequired", Boolean.class);
        if (passwordChangeRequired == null) {
            passwordChangeRequired = false;
        }

        CustomUserDetails principal = new CustomUserDetails(
                getEmployeeId(accessToken),      // claims에서 employeeId 가져오기
                getEmployeeNumber(accessToken),  // claims에서 employeeNumber 가져오기
                getEmployeeName(accessToken),    // claims에서 employeeName 가져오기
                getDepartmentId(accessToken),
                getDepartmentName(accessToken),
                getGradeId(accessToken),
                getGradeName(accessToken),
                getJobTitleId(accessToken),
                getJobTitleName(accessToken),
                passwordChangeRequired,
                getImagePath(accessToken)
        );

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * Request Header에서 Access Token 정보 추출
     * @param request HttpServletRequest
     * @return 토큰 문자열
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 토큰에서 사용자 이름(subject) 추출
     * @param token JWT 토큰
     * @return 사용자 이름
     */
    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 토큰에서 사용자 번호 추출
     * @param token JWT 토큰
     * @return 사용자 번호
     */
    public Integer getEmployeeId(String token) {
        return parseClaims(token).get("employeeId", Integer.class);
    }

    /**
     * 토큰에서 사번 추출
     * @param token JWT 토큰
     * @return 사번
     */
    public String getEmployeeNumber(String token) {
        return parseClaims(token).get("employeeNumber", String.class);
    }

    /**
     * 토큰에서 사원 이름 추출
     * @param token
     * @return 사원 이름
     */
    public String getEmployeeName(String token) {
        return parseClaims(token).get("employeeName", String.class);
    }

    /**
     * 토큰에서 부서 ID 추출
     * @param token
     * @return 부서 ID
     */
    public Integer getDepartmentId(String token) {
        return parseClaims(token).get("departmentId", Integer.class);
    }

    /**
     * 토큰에서 부서 이름 추출
     * @param token
     * @return 부서 이름
     */
    public String getDepartmentName(String token) {
        return parseClaims(token).get("departmentName", String.class);
    }

    /**
     * 직급 ID 추출
     * @param token
     * @return 직급 ID
     */
    public Integer getGradeId(String token) {
        return parseClaims(token).get("gradeId", Integer.class);
    }

    /**
     * 직급 이름 추출
     * @param token
     * @return 직급 이름
     */
    public String getGradeName(String token) {
        return parseClaims(token).get("gradeName", String.class);
    }

    /**
     * 직책 ID 추출
     * @param token
     * @return 직책 ID
     */
    public Integer getJobTitleId(String token) {
        return parseClaims(token).get("jobTitleId", Integer.class);
    }

    /**
     * 직책 이름 추출
     * @param token
     * @return 직책 이름
     */
    public String getJobTitleName(String token) {
        return parseClaims(token).get("jobTitleName", String.class);
    }

    /**
     * 이미지 경로 추출
     * @param token
     * @return 이미지 경로
     */
    public String getImagePath(String token) {
        return parseClaims(token).get("imagePath", String.class);
    }


    /**
     * 토큰에서 클레임 추출
     * @param token JWT 토큰
     * @return Claims
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
