package com.c4.hero.domain.evaluation.dto.form;

import com.c4.hero.domain.evaluation.dto.item.FormItemResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <pre>
 * Class Name: EvaluationFormResponseDTO
 * Description: 클라이언트로 보내는 평가서 응답 데이터 DTO
 *
 * History
 * 2025/12/14 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationFormResponseDTO {

    private Integer evaluationFormFormId;

    private Integer evaluationFormEvaluationId;

    private String evaluationFormEvaluationName;

    private Integer evaluationFormEvaluationEmployeeId;

    private String evaluationFormEvaluationEmployeeName;

    private Integer evaluationFormEmployeeId;

    private String evaluationFormEmployeeName;

    private Integer evaluationFormDepartmentId;

    private String evaluationFormDepartmentName;

    private LocalDateTime evaluationFormCreatedAt;

    private Integer evaluationFormGradeId;

    private String evaluationFormGrade;

    private String evaluationFormTotal;

    private String evaluationFormTotalRank;

    private Float evaluationFormTotalScore;

    private Integer evaluationFormEvaluationPeriodId;

    private String evaluationFormEvaluationPeriodName;

    private LocalDateTime evaluationFormEvaluationPeriodStart;

    private LocalDateTime evaluationFormEvaluationPeriodEnd;

    private List<FormItemResponseDTO> formItems;
}

