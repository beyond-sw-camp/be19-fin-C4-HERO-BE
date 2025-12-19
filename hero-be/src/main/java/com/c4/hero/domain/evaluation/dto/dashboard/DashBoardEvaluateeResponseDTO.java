package com.c4.hero.domain.evaluation.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <pre>
 * Class Name: DashBoardFormResponseDTO
 * Description: 대시보드 그래프 출력을 위한 평가 정보 응답 데이터 DTO
 *
 * History
 * 2025/12/17 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashBoardEvaluateeResponseDTO {

    private Integer evaluationFormId;

    private Integer evaluationEvaluateeId;

    private String evaluationEvaluateeName;

    private Integer evaluationEvaluateeDepartmentId;

    private String evaluationEvaluateeDepartmentName;

    private Integer evaluationEvaluateeGradeId;

    private String evaluationEvaluateeGrade;

    private LocalDateTime evaluationFormCreatedAt;

    private String evaluationEvaluateeSummary;

    private String evaluationEvaluateeTotalRank;

    private Float evaluationEvaluateeTotalScore;

    private List<DashBoardFormItemResponseDTO> formItems;
}
