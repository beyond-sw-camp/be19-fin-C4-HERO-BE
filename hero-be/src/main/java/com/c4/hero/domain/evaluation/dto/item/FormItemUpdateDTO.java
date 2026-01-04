package com.c4.hero.domain.evaluation.dto.item;

import com.c4.hero.domain.evaluation.dto.score.ItemScoreUpdateDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: FormItemUpdateDTO
 * Description: 클라이언트로 오는 평가서 항목 수정 데이터 DTO
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
public class FormItemUpdateDTO {

    private Integer formItemFormItemId;

    @NotNull(message = "평가서 ID는 필수 입니다.")
    private Integer formItemFormId;

    @NotNull(message = "선택 항목 ID는 필수 입니다.")
    private Integer formItemSelectedItemId;

    @NotNull(message = "가중치는 필수 입니다.")
    private Float formItemWeight;

    private String formItemDescription;

    private ItemScoreUpdateDTO itemScore;
}
