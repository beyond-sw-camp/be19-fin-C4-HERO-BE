package com.c4.hero.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponseDTO {
    private String message;
    private boolean passwordChangeRequired;
}
