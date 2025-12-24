package com.c4.hero.domain.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 근태 점수 대시보드 상단 요약 카드 DTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AttendanceDashboardSummaryDTO {

    /** 전체 직원 수 */
    private Long totalEmployees;

    /** 우수 직원 수 (95점 이상) */
    private Long excellentEmployees;

    /** 위험 직원 수 (85점 이하) */
    private Long riskyEmployees;
}
