package com.c4.hero.domain.retirement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: TenureRetentionDTO
 * Description: 근속 기간별 잔존율 통계 정보를 전달하는 DTO 클래스
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
public class TenureRetentionDTO {

    /** 근속 기간 구간 (예: 1년 이상) */
    private String tenureRange;

    /** 잔존율 (%) */
    private double retentionRate;
}
