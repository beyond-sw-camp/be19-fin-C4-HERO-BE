package com.c4.hero.domain.approval.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: BeforePayrollResponseDTO
 * Description: 급여인상신청서 작성 시 기존 기본급 조회 응답 DTO
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
public class BeforePayrollResponseDTO {

    /**
     * 기존 기본급
     */
    private Integer beforePayroll;
}