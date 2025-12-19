package com.c4.hero.domain.evaluation.dto.period;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: EvaluationPeriodResponseDTO
 * Description: 클라이언트로 보내는 평가 기간 응답 데이터 DTO
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
public class EvaluationPeriodResponseDTO {

    private Integer evaluationPeriodEvaluationPeriodId;

    private Integer evaluationPeriodTemplateId;

    private String evaluationPeriodName;

    private LocalDateTime evaluationPeriodStart;

    private LocalDateTime evaluationPeriodEnd;
}
