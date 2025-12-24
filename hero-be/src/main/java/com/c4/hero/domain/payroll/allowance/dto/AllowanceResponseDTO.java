package com.c4.hero.domain.payroll.allowance.dto;

import com.c4.hero.domain.payroll.allowance.entity.Allowance;

/**
 * <pre>
 * DTO Name : AllowanceResponseDTO
 * Description : 수당(Allowance) 조회 응답 DTO
 *
 * History
 *   2025/12/22 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record AllowanceResponseDTO(
        String allowanceId,
        String allowanceName,
        String description,
        Integer defaultAmount,
        String taxableYn,
        String activeYn
) {
    /**
     * Allowance 엔티티 -> 응답 DTO 변환 메서드
     *
     * @param entity Allowance 엔티티
     * @return AllowanceResponseDTO
     */
    public static AllowanceResponseDTO from(Allowance entity) {
        return new AllowanceResponseDTO(
                entity.getAllowanceId(),
                entity.getAllowanceName(),
                entity.getDescription(),
                entity.getDefaultAmount(),
                entity.getTaxableYn(),
                entity.getActiveYn()
        );
    }
}