package com.c4.hero.domain.payroll.deduction.dto;

import com.c4.hero.domain.payroll.deduction.entity.Deduction;

/**
 * <pre>
 * DTO Name : DeductionResponseDTO
 * Description : 공제(Deduction) 조회 응답 DTO
 *
 * History
 *   2025/12/22 - 동근 최초 작성
 * </pre>
 *
 * @version 1.0
 * @author 동근
 */
public record DeductionResponseDTO(
        String deductionId,
        String deductionName,
        String description,
        String deductionType,
        String calculationType,
        Double rate,
        Integer fixedAmount,
        String activeYn
) {
    /**
     * Deduction 엔티티 -> 응답 DTO 변환 메서드
     *
     * @param entity Deduction 엔티티
     * @return DeductionResponseDTO
     */
    public static DeductionResponseDTO from(Deduction entity) {
        return new DeductionResponseDTO(
                entity.getDeductionId(),
                entity.getDeductionName(),
                entity.getDescription(),
                entity.getDeductionType(),
                entity.getCalculationType(),
                entity.getRate(),
                entity.getFixedAmount(),
                entity.getActiveYn()
        );
    }
}
