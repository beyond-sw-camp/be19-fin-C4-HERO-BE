package com.c4.hero.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <pre>
 * Class Name  : AttendanceStatsDTO
 * Description : 출근 통계 DTO
 *
 * History
 * 2025/12/26 - (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceStatsDTO {

    /** 정상 출근 */
    private Integer normalDays;

    /** 지각 */
    private Integer lateDays;

    /** 결근 */
    private Integer absentDays;

    /** 조퇴 */
    private Integer earlyLeaveDays;
}