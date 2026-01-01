package com.c4.hero.domain.approval.dto;

import com.c4.hero.domain.employee.entity.EmployeeDepartment;
import com.c4.hero.domain.employee.entity.Grade;
import com.c4.hero.domain.employee.entity.JobTitle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonnelTypesResponseDTO {
    private List<EmployeeDepartment> departments;
    private List<Grade> grades;
    private List<JobTitle> jobTitles;
}
