package com.c4.hero.domain.employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * Class Name: PasswordResetDTO
 * Description: 비밀번호 변경 요청 DTO (현재 비밀번호를 모르는 경우)
 *
 * History
 * 2025/12/29 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Getter
@Setter
public class PasswordResetDTO {

    @NotBlank(message = "토큰이 필요합니다.")
    private String token;

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    private String newPassword;
}
