package com.c4.hero.domain.settings.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingsJobTitleRequestDTO {
    private Integer jobTitleId;
    private String jobTitleName;
}
