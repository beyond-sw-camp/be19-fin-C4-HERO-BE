package com.c4.hero.domain.employee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 * Class Name: EmployeeListResponseDTO
 * Description: 직원 목록 조회 시 사용되는 데이터 전송 객체 (제한된 정보)
 *
 * History
 * 2025/12/12 승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeListResponseDTO {
    private Integer employeeId;
    private String employeeName;
    private String employeeNumber;
    private String departmentName;
    private String gradeName;
    private String jobTitleName;
}
