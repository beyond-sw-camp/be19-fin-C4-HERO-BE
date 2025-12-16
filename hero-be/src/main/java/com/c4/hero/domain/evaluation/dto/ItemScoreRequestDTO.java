package com.c4.hero.domain.evaluation.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: ItemScoreRequestDTO
 * Description: 클라이언트에서 오는 평가서 항목 점수 데이터 DTO
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
public class ItemScoreRequestDTO {

    private Integer itemScoreFormItemId;

    private Integer itemScoreScore;

    private String itemScoreDescription;

    private String itemScoreRank;
}
