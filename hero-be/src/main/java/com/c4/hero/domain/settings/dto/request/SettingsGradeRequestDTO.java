package com.c4.hero.domain.settings.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingsGradeRequestDTO {
    private Integer gradeId;
    private String gradeName;
    private Integer requiredPoint;
}
