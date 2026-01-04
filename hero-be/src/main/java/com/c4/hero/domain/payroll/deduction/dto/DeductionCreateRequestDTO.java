package com.c4.hero.domain.payroll.deduction.dto;

/**
 * <pre>
 * DTO Name : DeductionCreateRequestDTO
 * Description : 공제(Deduction) 생성/수정 요청 DTO
 *
 * History
 *   2025/12/22 - 동근 최초 작성
 * </pre>
 *
 * @version 1.0
 * @author 동근
 */
public record DeductionCreateRequestDTO(
        String deductionId,
        String deductionName,
        String description,
        String deductionType,
        String calculationType,
        Double rate,
        Integer fixedAmount
) {}
