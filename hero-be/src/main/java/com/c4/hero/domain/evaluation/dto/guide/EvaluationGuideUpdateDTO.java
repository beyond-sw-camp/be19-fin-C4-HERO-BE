package com.c4.hero.domain.evaluation.dto.guide;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: EvaluationGuideUpdateDTO
 * Description: 클라이언트로 부터 요청 받은 평가 가이드 수정 데이터 DTO
 *
 * History
 * 2025/12/11 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationGuideUpdateDTO {

    @NotNull(message = "수정할 평가 가이드 ID는 필수 입니다.")
    private Integer evaluationGuideEvaluationGuideId;

    @NotBlank(message = "수정할 평가 가이드명은 필수 입니다.")
    private String evaluationGuideName;

    @NotBlank(message = "수정할 평가 가이드 내용은 필수 입니다.")
    private String evaluationGuideContent;

    @NotNull(message = "수정할 평가 가이드 수정일은 필수 입니다.")
    private LocalDateTime evaluationGuideCreatedAt;

    @NotNull(message = "수정할 평가 가이드 수정자 ID는 필수 입니다.")
    private Integer evaluationGuideEmployeeId;

    private String evaluationGuideEmployeeName;

    @NotNull(message = "수정할 평가 가이드 수정부서 ID는 필수 입니다.")
    private Integer evaluationGuideDepartmentId;

    private String evaluationGuideDepartmentName;

    private Integer evaluationGuideGradeId;

    private String evaluationGuideGrade;
}
