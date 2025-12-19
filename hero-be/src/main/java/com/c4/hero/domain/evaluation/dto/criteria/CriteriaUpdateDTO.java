package com.c4.hero.domain.evaluation.dto.criteria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: CriteriaUpdateDTO
 * Description: 클라이언트에서 오는 평가 기준 수정 데이터 DTO
 *
 * History
 * 2025/12/07 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriteriaUpdateDTO {

    @NotNull(message = "수정할 평가 템플릿 평가 항목의 평가 기준 ID는 필수 입니다.")
    private Integer criteriaCriteriaId;

    @NotNull(message = "수정할 평가 템플릿 평가 항목 ID는 필수 입니다.")
    private Integer criteriaItemId;

    @NotBlank(message = "수정할 평가 템플릿 평가 항목의 평가 기준 등급은 필수 입니다.")
    private String criteriaRank;

    private String criteriaDescription;

    @NotNull(message = "수정할 평가 템플릿 평가 항목의 평가 기준 최소값은 필수 입니다.")
    private Integer criteriaMinScore;

    @NotNull(message = "수정할 평가 템플릿 평가 항목의 평가 기준 최대값은 필수 입니다.")
    private Integer criteriaMaxScore;
}
