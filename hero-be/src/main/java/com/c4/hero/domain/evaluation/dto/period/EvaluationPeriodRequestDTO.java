package com.c4.hero.domain.evaluation.dto.period;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: EvaluationPeriodRequestDTO
 * Description: 클라이언트에서 오는 평기 기간 요청 데이터 DTO
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
public class EvaluationPeriodRequestDTO {

    private Integer evaluationPeriodTemplateId;

    @NotBlank(message = "생성할 평가 템플릿 평가 기간명은 필수 입니다.")
    private String evaluationPeriodName;

    @NotNull(message = "생성할 평가 템플릿 평가 기간 시작일은 필수 입니다.")
    private LocalDateTime evaluationPeriodStart;

    @NotNull(message = "생성할 평가 템플릿 평가 기간 종료일은 필수 입니다.")
    private LocalDateTime evaluationPeriodEnd;
}
