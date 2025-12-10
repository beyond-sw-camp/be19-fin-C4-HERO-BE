package com.c4.hero.domain.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: CriteriaRequestDTO
 * Description: 클라이언트에서 오는 평기 기준 요청 데이터 DTO
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
public class CriteriaRequestDTO {

    private Integer criteriaItemId;

    private String criteriaRank;

    private String criteriaDescription;

    private Integer criteriaMinScore;

    private Integer criteriaMaxScore;
}

