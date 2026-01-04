package com.c4.hero.domain.promotion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Class Name: PromotionDepartmentDTO
 * Description: 승진 계획 옵션 중 부서 정보를 트리 구조로 담는 DTO
 *
 * History
 * 2025/12/19 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDepartmentDTO {

    /**
     * 부서 ID
     */
    private Integer departmentId;

    /**
     * 부서명
     */
    private String departmentName;

    /**
     * 하위 부서 목록
     */
    @Builder.Default
    private List<PromotionDepartmentDTO> children = new ArrayList<>();

    /**
     * 하위 부서를 추가하는 편의 메소드
     *
     * @param child 하위 부서 DTO
     */
    public void addChild(PromotionDepartmentDTO child) {
        this.children.add(child);
    }
}
