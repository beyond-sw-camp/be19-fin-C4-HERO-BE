package com.c4.hero.domain.evaluation.dto.guide;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: EvaluationGuideRequestDTO
 * Description: 클라이언트에서 오는 평기 가이드 요청 데이터 DTO
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
public class EvaluationGuideRequestDTO {

    @NotBlank(message = "생성할 평가 가이드명은 필수 입니다.")
    private String evaluationGuideName;

    @NotBlank(message = "생성할 평가 가이드 내용은 필수 입니다.")
    private String evaluationGuideContent;

    @NotNull(message = "생성할 평가 가이드 작성일은 필수 입니다.")
    private LocalDateTime evaluationGuideCreatedAt;

    @NotNull(message = "생성할 평가 가이드 작성자 ID는 필수 입니다.")
    private Integer evaluationGuideEmployeeId;

    @NotNull(message = "생성할 평가 가이드 작성 부서 ID는 필수 입니다.")
    private Integer evaluationGuideDepartmentId;
}
