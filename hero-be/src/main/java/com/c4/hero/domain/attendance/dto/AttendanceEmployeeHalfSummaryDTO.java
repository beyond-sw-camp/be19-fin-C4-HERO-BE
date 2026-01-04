package com.c4.hero.domain.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name : AttendanceEmployeeHalfSummaryDTO
 * Description : 직원 반기(상/하반기) 근태 요약 카드 DTO
 *
 * History
 * 2025/12/24 이지윤 최초 작성
 * </pre>
 *
 * @author 이지윤
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AttendanceEmployeeHalfSummaryDTO {

    /** 총 출근(근무일) */
    private Long totalWorkDays;

    /** 총 지각 */
    private Long totalTardyCount;

    /** 총 결근 */
    private Long totalAbsenceCount;
}
