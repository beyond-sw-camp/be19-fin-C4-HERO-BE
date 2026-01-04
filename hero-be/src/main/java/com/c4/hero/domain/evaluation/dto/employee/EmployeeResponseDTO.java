package com.c4.hero.domain.evaluation.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: EmployeeResponseDTO
 * Description: 피평가자들 조회 응답 데이터 DTO
 *
 * History
 * 2025/12/12 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponseDTO {

    private Integer employeeEmployeeId;

    private String employeeEmployeeName;

    private Integer employeeDepartmentId;

    private String employeeDepartmentName;

    private Integer employeeGradeId;

    private String employeeGrade;

    private String employeeStatus;
}
