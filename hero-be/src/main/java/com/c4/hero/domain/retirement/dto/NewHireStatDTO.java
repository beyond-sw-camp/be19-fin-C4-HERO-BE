package com.c4.hero.domain.retirement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: NewHireStatDTO
 * Description: 신입 사원 정착률 및 이직률 통계 정보를 전달하는 DTO 클래스
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
public class NewHireStatDTO {

    /** 분기 (예: 2024년 1분기) */
    private String quarter;

    /** 정착률 (%) */
    private double settlementRate;

    /** 이직률 (%) */
    private double turnoverRate;
}
