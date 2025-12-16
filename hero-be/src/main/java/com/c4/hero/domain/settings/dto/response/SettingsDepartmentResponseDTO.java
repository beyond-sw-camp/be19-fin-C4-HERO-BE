package com.c4.hero.domain.settings.dto.response;

import com.c4.hero.domain.employee.entity.Employee;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SettingsDepartmentResponseDTO {
    private Integer departmentId;
    private String departmentName;
    private String departmentPhone;
    private Integer depth;
    private Integer parentDepartmentId;
    private SettingsDepartmentManagerDTO manager;
    private List<SettingsDepartmentResponseDTO> children;
}
