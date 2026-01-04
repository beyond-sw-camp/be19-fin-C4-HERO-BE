package com.c4.hero.domain.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name : AttendanceEmployeeMonthlyStatDTO
 * Description : 직원 월별 근태 집계(차트용) DTO
 *
 * History
 * 2025/12/24 이지윤 최초 작성
 * </pre>
 *
 * month: 1~12 중 하나 (응답에서는 보통 6개월 범위만 내려감)
 *
 * @author 이지윤
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AttendanceEmployeeMonthlyStatDTO {

    /** 월(1~12) */
    private Integer month;

    /** 출근(또는 근무일) 수 */
    private Long workDays;

    /** 지각 수 */
    private Long tardyCount;

    /** 결근 수 */
    private Long absenceCount;
}
