package com.c4.hero.domain.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeptWorkSystemRowDTO {

    private int employeeId;
    private int departmentId;

    private String employeeName;
    private String state;
    private String jobTitle;
    private String workSystemName;

    private LocalTime startTime;
    private LocalTime endTime;
}

