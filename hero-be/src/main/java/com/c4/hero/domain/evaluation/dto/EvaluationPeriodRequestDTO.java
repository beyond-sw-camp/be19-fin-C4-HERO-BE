package com.c4.hero.domain.evaluation.dto;

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

    private String evaluationPeriodName;

    private LocalDateTime evaluationPeriodStart;

    private LocalDateTime evaluationPeriodEnd;
}
