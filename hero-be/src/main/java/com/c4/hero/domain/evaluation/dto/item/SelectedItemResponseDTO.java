package com.c4.hero.domain.evaluation.dto.item;

import com.c4.hero.domain.evaluation.dto.criteria.CriteriaResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * Class Name: SelectedItemResponseDTO
 * Description: 클라이언트로 보내는 평가 선택 항목 데이터 DTO
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
public class SelectedItemResponseDTO {

    private Integer selectedItemSelectedItemId;

    private Integer selectedItemEvaluationId;

    private Integer selectedItemItemId;

    private String selectedItemItemName;

    private String selectedItemItemDescription;

    private List<CriteriaResponseDTO> criterias;
}

