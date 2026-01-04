package com.c4.hero.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <pre>
 * Class Name  : WeeklyStatsDTO
 * Description : 주간 근무 통계 응답 DTO
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
public class WeeklyStatsDTO {

    /** 이번 주 총 근무시간 (분) */
    private Integer totalWorkMinutes;

    /** 이번 주 총 근무시간 (시간 단위) */
    private Double totalWorkHours;

    /** 법정 주 근무시간 (52시간) */
    private Integer legalWeeklyHours = 52;

    /** 근무시간 달성률 (%) */
    private Double achievementRate;

    /** 오늘 근무 중 여부 */
    private Boolean isWorkingToday;

    /** 오늘 현재까지 근무시간 (분) */
    private Integer todayWorkMinutes;
}