package com.c4.hero.domain.settings.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class SettingsDepartmentRequestDTO {
    private Integer departmentId;
    private String departmentName;
    private String departmentPhone;
    private Integer depth;
    private Integer parentDepartmentId;
    private Integer managerId;
    private List<SettingsDepartmentRequestDTO> children;
}
