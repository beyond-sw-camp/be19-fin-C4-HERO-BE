package com.c4.hero.domain.settings.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class SettingsPermissionsRequestDTO {
    private Integer employeeId;
    private List<Integer> roleIds;
}
