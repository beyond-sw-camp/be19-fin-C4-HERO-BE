package com.c4.hero.domain.evaluation.dto.ai.violation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: ViolationItemDTO
 * Description: 위반 항목 응답 DTO
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
public class ViolationItemDTO {

    @JsonProperty("피평가자")
    private String evaluateeName;

    @JsonProperty("항목")
    private String itemName;

    @JsonProperty("위반 사유")
    private String violationReason;
}
