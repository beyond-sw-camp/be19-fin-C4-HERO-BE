package com.c4.hero.domain.department.dto;

import com.c4.hero.domain.employee.type.ChangeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: EmployeeDepartmentHistoryDTO
 * Description: 직원의 부서 변경 이력을 담는 DTO
 *
 * History
 * 2025/12/29 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDepartmentHistoryDTO {

    private Integer employeeHistoryId;
    private Integer employeeId;
    private Integer changedBy;
    private LocalDateTime changedAt;
    private ChangeType changeType;
    private String departmentName;
}
