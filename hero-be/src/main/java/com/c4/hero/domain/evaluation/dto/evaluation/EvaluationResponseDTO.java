package com.c4.hero.domain.evaluation.dto.evaluation;

import com.c4.hero.domain.evaluation.dto.evaluatee.EvaluateeResponseDTO;
import com.c4.hero.domain.evaluation.dto.item.SelectedItemResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <pre>
 * Class Name: EvaluationResponseDTO
 * Description: 클라이언트로 보내는 평가 응답 데이터 DTO
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
public class EvaluationResponseDTO {

    private Integer evaluationEvaluationId;

    private Integer evaluationEmployeeId;

    private String evaluationEmployeeName;

    private Integer evaluationDepartmentId;

    private String evaluationDepartmentName;

    private Integer evaluationGradeId;

    private String evaluationGrade;

    private Integer evaluationTemplateId;

    private String evaluationName;

    private Integer evaluationStatus;

    private LocalDateTime evaluationCreatedAt;

    private LocalDateTime evaluationEndedAt;

    private String evaluationTotalRank;

    private Float evaluationTotalScore;

    private Integer evaluationEvaluationGuideId;

    private String evaluationEvaluationGuideName;

    private Integer evaluationEvaluationPeriodId;

    private String evaluationEvaluationPeriodName;

    private LocalDateTime evaluationEvaluationPeriodStart;

    private LocalDateTime evaluationEvaluationPeriodEnd;

    private List<SelectedItemResponseDTO> selectedItems;

    private List<EvaluateeResponseDTO> evaluatees;
}

