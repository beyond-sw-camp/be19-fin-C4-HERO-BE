package com.c4.hero.domain.retirement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: RetirementSummaryDTO
 * Description: 퇴직 현황 요약 정보를 전달하는 DTO 클래스
 *
 * History
 * 2025/12/30 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetirementSummaryDTO {

    /** 잔존율 (%) */
    private double retentionRate;

    /** 정착률 (%) */
    private double settlementRate;

    /** 종합 이직률 (%) */
    private double totalTurnoverRate;

    /** 신입 1년 내 이직률 (%) */
    private double newHireTurnoverRate;
}
