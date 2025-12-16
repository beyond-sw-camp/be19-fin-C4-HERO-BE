package com.c4.hero.domain.evaluation.dto.item;

import com.c4.hero.domain.evaluation.dto.criteria.CriteriaResponseDTO;
import com.c4.hero.domain.evaluation.dto.score.ItemScoreResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * Class Name: FormItemResponseDTO
 * Description: 클라이언트로 보내는 평가서 항목 데이터 DTO
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
public class FormItemResponseDTO {

    private Integer formItemFormItemId;

    private Integer formItemFormId;

    private Integer formItemSelectedItemId;

    private String formItemSelectedItemItemName;

    private String formItemSelectedItemItemDescription;

    private Float formItemWeight;

    private String formItemDescription;

    private ItemScoreResponseDTO itemScore;

    private List<CriteriaResponseDTO> criterias;
}


