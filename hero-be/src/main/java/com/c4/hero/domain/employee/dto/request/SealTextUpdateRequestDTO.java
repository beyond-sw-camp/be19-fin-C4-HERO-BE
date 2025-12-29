package com.c4.hero.domain.employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: SealTextUpdateRequestDTO
 * Description: 텍스트 직인 업데이트 요청 DTO
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
public class SealTextUpdateRequestDTO {

    @NotBlank(message = "직인 텍스트는 필수입니다.")
    @Size(max = 10, message = "직인 텍스트는 10자 이하여야 합니다.")
    private String sealText;
}