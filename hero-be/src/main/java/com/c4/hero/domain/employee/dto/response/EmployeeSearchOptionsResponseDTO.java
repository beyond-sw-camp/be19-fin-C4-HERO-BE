package com.c4.hero.domain.employee.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EmployeeSearchOptionsResponseDTO {
    private List<String> department;
    private List<String> grade;
    private List<String> jobTitle;
}
