package com.c4.hero.domain.evaluation.dto.item;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: SelectedItemRequestDTO
 * Description: 클라이언트에서 오는 평가의 선택 항목 데이터 DTO
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
public class SelectedItemRequestDTO {

    @NotNull(message = "생성할 평가의 평가 항목 ID는 필수 입니다.")
    private Integer selectedItemItemId;
}
