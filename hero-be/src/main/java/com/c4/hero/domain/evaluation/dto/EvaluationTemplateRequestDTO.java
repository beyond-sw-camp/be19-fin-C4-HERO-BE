package com.c4.hero.domain.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <pre>
 * Class Name: EvaluationTemplateRequestDTO
 * Description: 클라이언트에서 오는 평기 템플릿 요청 데이터 DTO
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
public class EvaluationTemplateRequestDTO {

    private String evaluationTemplateName;

    private LocalDateTime evaluationTemplateCreatedAt;

    private Integer evaluationTemplateEmployeeId;

    private Integer evaluationTemplateDepartmentId;

    private Integer evaluationTemplateType;

    private List<TemplateItemRequestDTO> templateItems;

    private EvaluationPeriodRequestDTO evaluationPeriod;
}

