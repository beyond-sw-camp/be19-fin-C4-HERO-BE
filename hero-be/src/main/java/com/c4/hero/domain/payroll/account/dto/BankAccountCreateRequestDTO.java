package com.c4.hero.domain.payroll.account.dto;

// 프론트에서 오는 요청
public record BankAccountCreateRequestDTO(
        String bankCode,
        String accountNumber,
        String accountHolder
) {}
