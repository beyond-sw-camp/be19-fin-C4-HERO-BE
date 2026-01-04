package com.c4.hero.domain.employee.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: ContactUpdateRequestDTO
 * Description: 연락처 정보 수정 요청 DTO
 *
 * History
 * 2025/12/28 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactUpdateRequestDTO {

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    private String mobile;

    private String address;
}