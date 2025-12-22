package com.c4.hero.domain.promotion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * <pre>
 * Class Name: PromotionDetailPlanDTO
 * Description: 승진 상세 계획 정보를 담는 DTO
 *
 * History
 * 2025/12/19 (승건) 최초 작성
 * 2025/12/22 (승건) 승진 상세 계획 id 추가
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PromotionDetailPlanDTO {
    private Integer promotionDetailId;  // 승진 상세 계획 ID
    private Integer departmentId;       // 승진 대상 부서 ID
    private String department;          // 승진 대상 부서명
    private Integer gradeId;            // 승진 후 직급 ID
    private String grade;               // 승진 후 직급명
    private Integer quotaCount;         // TO
    private List<PromotionCandidateDTO> candidateList;
}
