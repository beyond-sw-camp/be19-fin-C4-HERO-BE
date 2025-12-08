package com.c4.hero.domain.payroll.report.dto;

//수당, 공제 개별 항목 DTO
public record PayItemDTO(
        String name,
        int amount
) {}
