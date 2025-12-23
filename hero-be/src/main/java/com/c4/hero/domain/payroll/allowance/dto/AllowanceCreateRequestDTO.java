package com.c4.hero.domain.payroll.allowance.dto;

/**
 * <pre>
 * DTO Name : AllowanceCreateRequestDTO
 * Description : 수당(Allowance) 생성/수정 요청 DTO
 *
 * History
 *   2025/12/22 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record AllowanceCreateRequestDTO(
        String allowanceId,
        String allowanceName,
        String description,
        Integer defaultAmount,
        String taxableYn
) {}
