package com.c4.hero.domain.employee.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * Class Name: PasswordResetRequestDTO
 * Description: 비밀번호 변경 인증 요청 DTO
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
public class PasswordResetRequestDTO {

    @NotBlank(message = "사번을 입력해주세요.")
    private String employeeNumber;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email
    private String email;
}
