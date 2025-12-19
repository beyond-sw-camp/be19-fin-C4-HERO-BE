package com.c4.hero.domain.evaluation.dto.item;

import com.c4.hero.domain.evaluation.dto.criteria.CriteriaResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * Class Name: TemplateItemResponseDTO
 * Description: 클라이언트로 보내는 평가 항목 응답 데이터 DTO
 *
 * History
 * 2025/12/07 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateItemResponseDTO {

    private Integer templateItemItemId;

    private Integer templateItemTemplateId;

    private String templateItemItem;

    private String templateItemDescription;

    private List<CriteriaResponseDTO> criterias;
}
