package com.c4.hero.domain.retirement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * Class Name: ExitReasonStatsResponseDTO
 * Description: 퇴사 사유 통계 응답 DTO
 *
 * History
 * 2025/01/03 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExitReasonStatsResponseDTO {

    private List<ExitReasonStat> earlyLeavers; // 1년 미만 퇴사자 통계
    private List<ExitReasonStat> totalLeavers; // 전체 퇴사자 통계

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExitReasonStat {
        private String reasonName;
        private Long count;
    }
}
