package com.c4.hero.domain.evaluation.dto.form;

import com.c4.hero.domain.evaluation.dto.item.FormItemRequestDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <pre>
 * Class Name: EvaluationFromRequestDTO
 * Description: 클라이언트에서 오는 평가서 요청 데이터 DTO
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
public class EvaluationFormRequestDTO {

    @NotNull(message = "생성할 평가 ID는 필수 입니다.")
    private Integer evaluationFormEvaluationId;

    @NotNull(message = "생성할 작성자 ID는 필수 입니다.")
    private Integer evaluationFormEmployeeId;

    @NotNull(message = "생성할 작성부서 ID는 필수 입니다.")
    private Integer evaluationFormDepartmentId;

    @NotNull(message = "생성할 작성일자는 필수 입니다.")
    private LocalDateTime evaluationFormCreatedAt;

    private String evaluationFormTotal;

    private String evaluationFormTotalRank;

    private Float evaluationFormTotalScore;

    private List<FormItemRequestDTO> formItems;
}
