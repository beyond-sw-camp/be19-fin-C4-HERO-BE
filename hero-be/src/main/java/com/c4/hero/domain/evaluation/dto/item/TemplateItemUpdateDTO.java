package com.c4.hero.domain.evaluation.dto.item;

import com.c4.hero.domain.evaluation.dto.criteria.CriteriaUpdateDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * Class Name: TemplateItemUpdateDTO
 * Description: 클라이언트에서 오는 평가 항목 수정 데이터 DTO
 *
 * History
 * 2025/12/07 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateItemUpdateDTO {

    @NotNull(message = "수정할 평가 템플릿 평가 항목 ID는 필수 입니다.")
    private Integer templateItemItemId;

    @NotNull(message = "수정할 평가 템플릿 ID는 필수 입니다.")
    private Integer templateItemTemplateId;

    @NotBlank(message = "수정할 평가 템플릿 평가 항목명은 필수 입니다.")
    private String templateItemItem;

    private String templateItemDescription;

    private List<CriteriaUpdateDTO> criterias;
}
