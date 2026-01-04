package com.c4.hero.domain.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name : AttendanceDashboardDTO
 * Description : 근태 점수 대시보드 한 행(row)에 대한 DTO
 *
 * History
 * 2025/12/17 이지윤 최초 작성
 * </pre>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AttendanceDashboardDTO {

    /** 직원 PK */
    private Integer employeeId;

    /** 사번 */
    private String employeeNumber;

    /** 직원 이름 */
    private String employeeName;

    /** 부서 ID */
    private Integer departmentId;

    /** 부서명 */
    private String departmentName;

    /** 지각 횟수 */
    private Long tardyCount;

    /** 결근 횟수 */
    private Long absenceCount;

    /** 근태 점수 (100 - 지각×1 - 결근×2) */
    private Long score;
}
