package com.c4.hero.domain.evaluation.dto.evaluatee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: EvaluateeResponseDTO
 * Description: 클라이언트로 보내는 피평가자 데이터 DTO
 *
 * History
 * 2025/12/12 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluateeResponseDTO {

    private Integer evaluateeEvaluateeId;

    private Integer evaluateeEvaluationId;

    private Integer evaluateeEmployeeId;

    private String evaluateeEmployeeName;

    private Integer evaluateeGradeId;

    private String evaluateeGrade;

    private Integer evaluateeStatus;
}

