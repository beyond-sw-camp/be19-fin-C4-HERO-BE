package com.c4.hero.domain.evaluation.dto.criteria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "생성할 평가 템플릿 평가 항목의 평가 기준 등급는 필수 입니다.")
    private String criteriaRank;

    private String criteriaDescription;

    @NotNull(message = "생성할 평가 템플릿 평가 항목의 평가 기준 최소 점수는 필수 입니다.")
    private Integer criteriaMinScore;

    @NotNull(message = "생성할 평가 템플릿 평가 항목의 평가 기준 최대 점수는 필수 입니다.")
    private Integer criteriaMaxScore;
}

