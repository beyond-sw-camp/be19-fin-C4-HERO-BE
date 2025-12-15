package com.c4.hero.domain.settings.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingsPermissionsResponseDTO {
    private Integer employeeId;
    private String department;
    private String grade;
    private String jobTitle;
    private List<String> role;

}
