package com.c4.hero.domain.evaluation.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: DashBoardFormItemResponseDTO
 * Description: 대시보드 그래프 출력을 위한 평가 정보 응답 데이터 DTO
 *
 * History
 * 2025/12/17 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashBoardFormItemResponseDTO {

    private Integer formItemId;

    private String formItemName;

    private String formItemEvaluateePerformance;

    private Float formItemWeight;

    private Integer formItemScore;

    private String formItemRank;

    private String formItemComment;
}
