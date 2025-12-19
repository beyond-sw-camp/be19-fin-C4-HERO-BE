package com.c4.hero.domain.evaluation.dto.evaluatee;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: EvaluateeRequestDTO
 * Description: 클라이언트에서 오는 평가의 평가 대상자(피평가자) 데이터 DTO
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
public class EvaluateeRequestDTO {

    @NotNull(message = "생성할 평가의 피평가자 ID는 필수 입니다.")
    private Integer evaluateeEmployeeId;

    @NotNull(message = "생성할 평가의 피평가자 제출 상태값은 필수 입니다.")
    private Integer evaluateeStatus;
}
