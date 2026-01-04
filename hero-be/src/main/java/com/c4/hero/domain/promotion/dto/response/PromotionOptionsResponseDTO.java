package com.c4.hero.domain.promotion.dto.response;


import com.c4.hero.domain.promotion.dto.PromotionDepartmentDTO;
import com.c4.hero.domain.promotion.dto.PromotionGradeDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * <pre>
 * Class Name: PromotionOptionsDTO
 * Description: 승진 계획에서 선택할 수 있는 옵션을 제공하기 위한 DTO(부서, 직급)
 *
 * History
 * 2025/12/19 (승건) 최초 작성
 * </pre>
 *
 * @author `승건
 * @version 1.0
 */

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PromotionOptionsResponseDTO {
    private List<PromotionDepartmentDTO> promotionDepartmentDTOList;
    private List<PromotionGradeDTO> promotionGradeDTOList;
}
