package com.c4.hero.domain.evaluation.dto.item;

import com.c4.hero.domain.evaluation.dto.criteria.CriteriaRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * Class Name: TemplateItemRequestDTO
 * Description: 클라이언트에서 오는 평기 항목 요청 데이터 DTO
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
public class TemplateItemRequestDTO {

    private Integer templateItemTemplateId;

    @NotBlank(message = "생성할 평가 템플릿 평가 항목명은 필수 입니다.")
    private String templateItemItem;

    private String templateItemDescription;

    private List<CriteriaRequestDTO> criterias;
}



