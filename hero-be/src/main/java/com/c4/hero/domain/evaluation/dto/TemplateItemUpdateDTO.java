package com.c4.hero.domain.evaluation.dto;

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

    private Integer templateItemItemId;

    private Integer templateItemTemplateId;

    private String templateItemItem;

    private String templateItemDescription;

    private List<CriteriaUpdateDTO> criterias;
}
