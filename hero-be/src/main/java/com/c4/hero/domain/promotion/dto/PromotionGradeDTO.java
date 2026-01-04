package com.c4.hero.domain.promotion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <pre>
 * Class Name: PromotionGradeDTO
 * Description: 승진 계획에 사용할 수 있는 직급을 제공하기 위한 DTO
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
public class PromotionGradeDTO {
    private Integer gradeId;
    private String grade;
}
