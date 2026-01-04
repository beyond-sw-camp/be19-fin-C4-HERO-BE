package com.c4.hero.domain.evaluation.dto.ai.analysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: AiFormItemFastApiDTO
 * Description: MemberAnalysisFastApiRequestDTO에서 사용하는 평가 항목 데이터 DTO
 *
 * History
 * 2025/12/30 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiFormItemFastApiDTO {

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("score")
    private Float score;

    @JsonProperty("weight")
    private Float weight;

    @JsonProperty("comment")
    private String comment;
}
