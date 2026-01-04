package com.c4.hero.domain.approval.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: WorkSystemTypeResponseDTO
 * Description: 근무변경신청서 작성 시 근무제 템플릿 목록 조회 응답 DTO
 *
 * History
 * 2025/12/28 (민철) 최초작성
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkSystemTypeResponseDTO {

    /**
     * 근무제 템플릿 ID
     */
    private Integer workSystemTypeId;

    /**
     * 근무제 템플릿명 (예: 표준 근무제, 탄력 근무제, 선택적 근무제)
     */
    private String workSystemTypeName;
}