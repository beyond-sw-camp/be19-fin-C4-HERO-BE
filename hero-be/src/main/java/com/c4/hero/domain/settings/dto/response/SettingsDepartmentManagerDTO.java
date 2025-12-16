package com.c4.hero.domain.settings.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SettingsDepartmentManagerDTO {
    private Integer employeeId;
    private String employeeNumber;
    private String employeeName;
    private String jobTitle;
    private String grade;
}
