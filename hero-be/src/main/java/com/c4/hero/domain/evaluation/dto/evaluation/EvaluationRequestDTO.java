package com.c4.hero.domain.evaluation.dto.evaluation;

import com.c4.hero.domain.evaluation.dto.evaluatee.EvaluateeRequestDTO;
import com.c4.hero.domain.evaluation.dto.item.SelectedItemRequestDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <pre>
 * Class Name: EvaluationRequestDTO
 * Description: 클라이언트에서 오는 평가 요청 데이터 DTO
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
public class EvaluationRequestDTO {

    @NotNull(message = "생성할 평가 작성자 ID는 필수 입니다.")
    private Integer evaluationEmployeeId;

    @NotNull(message = "생성할 평가 작성부서 ID는 필수 입니다.")
    private Integer evaluationDepartmentId;

    @NotNull(message = "생성할 평가의 평가 템플릿 ID는 필수 입니다.")
    private Integer evaluationTemplateId;

    @NotBlank(message = "생성할 평가명은 필수 입니다.")
    private String evaluationName;

    @NotNull(message = "생성할 평가 상태값은 필수 입니다.")
    private Integer evaluationStatus;

    @NotNull(message = "생성할 평가 생성일은 필수 입니다.")
    private LocalDateTime evaluationCreatedAt;

    private LocalDateTime evaluationEndedAt;

    private String evaluationTotalRank;

    private Float evaluationTotalScore;

    @NotNull(message = "생성할 평가의 평가 가이드 ID는 필수 입니다.")
    private Integer evaluationEvaluationGuideId;

    @NotNull(message = "생성할 평가의 평가 기간 ID는 필수 입니다.")
    private Integer evaluationEvaluationPeriodId;

    private List<SelectedItemRequestDTO> selectedItems;

    private List<EvaluateeRequestDTO> evaluatees;
}

