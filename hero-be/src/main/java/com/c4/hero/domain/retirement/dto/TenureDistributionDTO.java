package com.c4.hero.domain.retirement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: TenureDistributionDTO
 * Description: 근속 연수별 인력 분포 DTO
 *
 * History
 * 2025/12/30 (승건) 최초 작성
 * 2025/01/02 (승건) 이름 및 필드명 변경
 * </pre>
 *
 * @author 승건
 * @version 1.1
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenureDistributionDTO {

    private String tenureRange; // 예: "0년차", "1년차"
    private double percentage;  // 해당 연차의 인원 비율
}
