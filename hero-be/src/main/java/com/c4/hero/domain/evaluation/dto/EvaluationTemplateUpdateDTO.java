package com.c4.hero.domain.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationTemplateUpdateDTO {

    private Integer evaluationTemplateTemplateId;

    private String evaluationTemplateName;

    private LocalDateTime evaluationTemplateCreatedAt;

    private Integer evaluationTemplateEmployeeId;

    private String evaluationTemplateEmployeeName;

    private Integer evaluationTemplateDepartmentId;

    private String evaluationTemplateDepartmentName;

    private Integer evaluationTemplatePosition;

    private String evaluationTemplateGrade;

    private Integer evaluationTemplateType;

    private Integer evaluationPeriodEvaluationPeriodId;

    private String evaluationPeriodName;

    private LocalDateTime evaluationPeriodStart;

    private LocalDateTime evaluationPeriodEnd;

    private List<TemplateItemUpdateDTO> templateItems;
}
