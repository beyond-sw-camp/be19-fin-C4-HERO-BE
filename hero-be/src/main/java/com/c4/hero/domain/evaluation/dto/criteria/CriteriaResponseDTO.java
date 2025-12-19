package com.c4.hero.domain.evaluation.dto.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: CriteriaResponseDTO
 * Description: 클라이언트로 보내는 평가 기준 응답 데이터 DTO
 *
 * History
 * 2025/12/07 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriteriaResponseDTO {

    private Integer criteriaCriteriaId;

    private Integer criteriaItemId;

    private String criteriaRank;

    private String criteriaDescription;

    private Integer criteriaMinScore;

    private Integer criteriaMaxScore;
}
