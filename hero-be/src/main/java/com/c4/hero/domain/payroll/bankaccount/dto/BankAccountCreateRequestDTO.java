package com.c4.hero.domain.payroll.bankaccount.dto;

/**
 * <pre>
 * Class Name: BankAccountCreateRequestDTO
 * Description: 계좌 생성 요청 데이터 전달 객체
 *
 * History
 * 2025/12/08 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record BankAccountCreateRequestDTO(
        String bankCode,
        String accountNumber,
        String accountHolder
) {}
