package com.c4.hero.domain.employee.dto.response;

import com.c4.hero.domain.employee.type.ChangeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class DepartmentHistoryResponseDTO {
    private Integer employeeHistoryId;
    private Integer employeeId;
    private Integer changedBy;
    private LocalDateTime changedAt;
    private ChangeType changeType;
    private String departmentName;
}
