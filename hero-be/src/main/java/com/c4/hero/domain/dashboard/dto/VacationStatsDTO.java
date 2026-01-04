package com.c4.hero.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <pre>
 * Class Name  : VacationStatsDTO
 * Description : 휴가 현황 DTO
 *
 * History
 * 2025/12/26 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VacationStatsDTO {

    /** 연차 사용 */
    private Double annualLeaveDays;

    /** 반차 사용 */
    private Double halfDayDays;

    /** 병가 사용 */
    private Double sickLeaveDays;

    /** 기타 휴가 */
    private Double otherLeaveDays;
}