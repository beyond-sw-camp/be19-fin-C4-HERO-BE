package com.c4.hero.domain.evaluation.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <pre>
 * Class Name: DashBoardEvaluationResponseDTO
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
public class DashBoardEvaluationResponseDTO {

    private Integer evaluationId;

    private String evaluationName;

    private Integer evaluationManagerId;

    private String evaluationManagerName;

    private Integer evaluationDepartmentId;

    private String evaluationDepartmentName;

    private Integer evaluationMangerGardeId;

    private String evaluationMangerGarde;

    private LocalDateTime evaluationCreatedAt;

    private LocalDateTime evaluationEndedAt;

    private Float evaluationTotalScore;

    private String evaluationTotalRank;

    private DashBoardGuideResponseDTO evaluationGuide;

    private List<DashBoardItemResponseDTO> evaluationItems;

    private List<DashBoardEvaluateeResponseDTO> evaluatees;
}
