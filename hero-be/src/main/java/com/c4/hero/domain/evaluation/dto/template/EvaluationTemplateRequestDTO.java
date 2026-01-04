package com.c4.hero.domain.evaluation.dto.template;

import com.c4.hero.domain.evaluation.dto.period.EvaluationPeriodRequestDTO;
import com.c4.hero.domain.evaluation.dto.item.TemplateItemRequestDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "생성할 평가 템플릿명은 필수 입니다.")
    private String evaluationTemplateName;

    @NotNull(message = "생성할 평가 템플릿 작성일은 필수 입니다.")
    private LocalDateTime evaluationTemplateCreatedAt;

    @NotNull(message = "생성할 평가 템플릿 작성자 ID는 필수 입니다.")
    private Integer evaluationTemplateEmployeeId;

    @NotNull(message = "생성할 평가 템플릿 작성 부서 ID는 필수 입니다.")
    private Integer evaluationTemplateDepartmentId;

    @NotNull(message = "생성할 평가 템플릿 타입값은 필수 입니다.")
    private Integer evaluationTemplateType;

    private List<TemplateItemRequestDTO> templateItems;

    private EvaluationPeriodRequestDTO evaluationPeriod;
}

