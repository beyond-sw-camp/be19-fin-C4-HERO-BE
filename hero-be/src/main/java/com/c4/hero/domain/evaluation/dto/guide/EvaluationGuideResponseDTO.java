package com.c4.hero.domain.evaluation.dto.guide;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: EvaluationGuideResponseDTO
 * Description: 클라이언트로 평기 가이드 응답 데이터 DTO
 *
 * History
 * 2025/12/10 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationGuideResponseDTO {

    private Integer evaluationGuideEvaluationGuideId;

    private String evaluationGuideName;

    private String evaluationGuideContent;

    private LocalDateTime evaluationGuideCreatedAt;

    private Integer evaluationGuideEmployeeId;

    private String evaluationGuideEmployeeName;

    private Integer evaluationGuideDepartmentId;

    private String evaluationGuideDepartmentName;

    private Integer evaluationGuideGradeId;

    private String evaluationGuideGrade;
}
