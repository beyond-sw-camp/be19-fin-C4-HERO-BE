package com.c4.hero.domain.evaluation.dto.template;

import com.c4.hero.domain.evaluation.dto.item.TemplateItemResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <pre>
 * Class Name: EvaluationTemplateResponseDTO
 * Description: 클라이언트로 보내는 평가 템플릿 응답 데이터 DTO
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
public class EvaluationTemplateResponseDTO {

    private Integer evaluationTemplateTemplateId;

    private String evaluationTemplateName;

    private LocalDateTime evaluationTemplateCreatedAt;

    private Integer evaluationTemplateEmployeeId;

    private String evaluationTemplateEmployeeName;

    private Integer evaluationTemplateDepartmentId;

    private String evaluationTemplateDepartmentName;

    private Integer evaluationTemplateGradeId;

    private String evaluationTemplateGrade;

    private Integer evaluationTemplateType;

    private Integer evaluationPeriodEvaluationPeriodId;

    private String evaluationPeriodName;

    private LocalDateTime evaluationPeriodStart;

    private LocalDateTime evaluationPeriodEnd;

    private List<TemplateItemResponseDTO> templateItems;
}


