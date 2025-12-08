package com.c4.hero.domain.payroll.account.dto;

// 프론트에 돌려줄 응답 (types/payroll.ts에 맞춰가는 용도)
public record BankAccountDTO(
        Integer id,
        String bankName,
        String accountNumber,
        String accountHolder,
        boolean primary
) {}
