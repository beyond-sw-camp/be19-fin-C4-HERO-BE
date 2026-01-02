package com.c4.hero.domain.evaluation.dto.ai.violation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * Class Name: GuideViolationResponseDTO
 * Description: 평가 가이드 위반 분석 결과 응답 DTO
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
public class GuideViolationResponseDTO {

    private Long evaluationTemplateId;

    private String evaluationTemplateName;

    private String managerName;

    private String departmentName;

    private List<ViolationItemDTO> violations;
}
