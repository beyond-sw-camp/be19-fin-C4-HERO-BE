package com.c4.hero.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <pre>
 * Class Name  : MonthlySummaryDTO
 * Description : 이번 달 요약 통계 DTO
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
public class MonthlySummaryDTO {

    /** 근무일수 */
    private Integer workDays;

    /** 남은 연차 */
    private Double remainingAnnualLeave;

    /** 사용한 휴가일수 */
    private Double usedVacationDays;
}