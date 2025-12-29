package com.c4.hero.domain.employee.controller;

import com.c4.hero.common.response.CustomResponse;
import com.c4.hero.domain.auth.security.CustomUserDetails;
import com.c4.hero.domain.employee.dto.request.PasswordChangeRequestDTO;
import com.c4.hero.domain.employee.dto.request.PasswordResetDTO;
import com.c4.hero.domain.employee.dto.request.PasswordResetRequestDTO;
import com.c4.hero.domain.employee.service.EmployeePasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <pre>
 * Class Name: EmployeePasswordController
 * Description: 직원 비밀번호 관련 API 컨트롤러
 *
 * History
 * 2025/12/29 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth/password")
@RequiredArgsConstructor
public class EmployeePasswordController {
    private final EmployeePasswordService employeePasswordService;

    /**
     * 로그인한 사용자의 비밀번호를 변경합니다.
     *
     * @param userDetails 현재 로그인한 사용자 정보
     * @param requestDTO  비밀번호 변경 요청 정보
     * @return 성공 응답
     */
    @PutMapping
    public ResponseEntity<CustomResponse<Void>> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PasswordChangeRequestDTO requestDTO
    ) {
        employeePasswordService.changePassword(
                userDetails.getEmployeeId(), requestDTO
        );

        return ResponseEntity.ok(CustomResponse.success());
    }

    /**
     * 비밀번호 재설정 토큰 발급을 요청하고 이메일로 전송합니다.
     *
     * @param requestDTO 사번, 이메일 정보
     * @return 성공 응답
     */
    @PostMapping("/reset-request")
    public ResponseEntity<CustomResponse<Void>> requestPasswordReset(
            @Valid @RequestBody PasswordResetRequestDTO requestDTO
    ) {
        employeePasswordService.issueAndSendPasswordResetToken(
                requestDTO.getEmployeeNumber(),
                requestDTO.getEmail()
        );
        return ResponseEntity.ok(CustomResponse.success());
    }

    /**
     * 토큰을 사용하여 비밀번호를 재설정합니다.
     *
     * @param requestDTO 토큰, 새 비밀번호 정보
     * @return 성공 응답
     */
    @PostMapping("/reset")
    public ResponseEntity<CustomResponse<Void>> resetPassword(
            @Valid @RequestBody PasswordResetDTO requestDTO
    ) {
        employeePasswordService.resetPasswordWithToken(
                requestDTO.getToken(),
                requestDTO.getNewPassword()
        );
        return ResponseEntity.ok(CustomResponse.success());
    }
}
