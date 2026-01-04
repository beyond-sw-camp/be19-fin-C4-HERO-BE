package com.c4.hero.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * Class Name: RequestLoginDTO
 * Description: 로그인 요청 시 사용되는 데이터 전송 객체
 *
 * History
 * 2025/12/09 (이승건) 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
@Getter
@Setter
public class RequestLoginDTO {

    /**
     * 계정 아이디
     */
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String account;

    /**
     * 비밀번호
     */
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;
}
