package com.c4.hero.domain.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

/**
 * <pre>
 * Class Name: DeptWorkSystemRowDTO
 * Description: 부서별 근무제/근무 현황 테이블의 한 행(row)에 해당하는 데이터를 담는 DTO
 *
 * History
 * 2025/12/10 (이지윤) 최초 작성 및 컨벤션 적용
 * </pre>
 *
 * @author 이지윤
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeptWorkSystemDTO {

    /** 직원 PK (식별자) */
    private Integer employeeId;

    /** 부서 PK (식별자) */
    private Integer departmentId;

    /** 직원 이름 */
    private String employeeName;

    /** 근무 상태(예: 근무, 연차, 반차 등) */
    private String state;

    /** 직책/직무명(예: 대리, 과장, 팀장 등) */
    private String jobTitle;

    /** 적용 근무제 이름(예: 기본 근무제, 시차 출퇴근제 등) */
    private String workSystemName;

    /** 해당 근무제 기준 출근 시간 */
    private LocalTime startTime;

    /** 해당 근무제 기준 퇴근 시간 */
    private LocalTime endTime;
}
