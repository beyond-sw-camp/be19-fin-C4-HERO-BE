package com.c4.hero.domain.evaluation.dto.score;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: ItemScoreResponseDTO
 * Description: 클라이언트로 보내는 평가서 항목 점수 데이터 DTO
 *
 * History
 * 2025/12/14 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemScoreResponseDTO {

    private Integer itemScoreItemScoreId;

    private Integer itemScoreFormItemId;

    private Integer itemScoreScore;

    private String itemScoreDescription;

    private String itemScoreRank;
}


