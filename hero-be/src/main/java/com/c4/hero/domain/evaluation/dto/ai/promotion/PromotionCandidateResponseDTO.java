package com.c4.hero.domain.evaluation.dto.ai.promotion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * Class Name: PromotionCandidateResponseDTO
 * Description: 클라이언트에서 승진 대상자 추천 분석 요청 데이터 DTO
 *
 * History
 * 2025/12/31 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionCandidateResponseDTO {

    private String name;

    private String department;

    @JsonProperty("current_grade")
    private String currentGrade;

    @JsonProperty("recommended_grade")
    private String recommendedGrade;

    @JsonProperty("growth_rate")
    private Float growthRate;

    @JsonProperty("core_competencies")
    private List<String> coreCompetencies;

    private String reason;
}
