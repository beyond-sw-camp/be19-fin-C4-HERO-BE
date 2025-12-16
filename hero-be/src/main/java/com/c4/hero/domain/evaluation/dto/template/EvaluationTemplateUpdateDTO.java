package com.c4.hero.domain.evaluation.dto.template;

import com.c4.hero.domain.evaluation.dto.item.TemplateItemUpdateDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationTemplateUpdateDTO {

    @NotNull(message = "수정할 평가 템플릿의 ID는 필수 입니다.")
    private Integer evaluationTemplateTemplateId;

    @NotBlank(message = "수정할 평가 템플릿명은 필수 입니다.")
    private String evaluationTemplateName;

    @NotNull(message = "수정할 평가 템플릿의 수정일은 필수 입니다.")
    private LocalDateTime evaluationTemplateCreatedAt;

    @NotNull(message = "수정할 평가 템플릿의 수정자 ID는 필수 입니다.")
    private Integer evaluationTemplateEmployeeId;

    private String evaluationTemplateEmployeeName;

    @NotNull(message = "수정할 평가 템플릿의 수정부서 ID은 필수 입니다.")
    private Integer evaluationTemplateDepartmentId;

    private String evaluationTemplateDepartmentName;

    private Integer evaluationTemplateGradeId;

    private String evaluationTemplateGrade;

    @NotNull(message = "수정할 평가 템플릿 타입값은 필수 입니다.")
    private Integer evaluationTemplateType;

    @NotNull(message = "수정할 평가 템플릿 기간 ID는 필수 입니다.")
    private Integer evaluationPeriodEvaluationPeriodId;

    @NotBlank(message = "수정할 평가 템플릿 기간명는 필수 입니다.")
    private String evaluationPeriodName;

    @NotNull(message = "수정할 평가 템플릿 평가 시작일은 필수 입니다.")
    private LocalDateTime evaluationPeriodStart;

    @NotNull(message = "수정할 평가 템플릿 평가 종료일은 필수 입니다.")
    private LocalDateTime evaluationPeriodEnd;

    private List<Integer> deletedItemIds;

    private List<Integer> deletedCriteriaIds;

    private List<TemplateItemUpdateDTO> templateItems;
}
