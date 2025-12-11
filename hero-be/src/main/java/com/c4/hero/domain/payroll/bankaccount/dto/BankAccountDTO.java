package com.c4.hero.domain.payroll.bankaccount.dto;


/**
 * <pre>
 * Class Name: BankAccountDTO
 * Description: 프론트에 돌려줄 응답(클라이언트로 전달되는 계좌 정보 응답 객체)
 *
 * History
 * 2025/12/08 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record BankAccountDTO(
        Integer id,
        String bankName,
        String accountNumber,
        String accountHolder,
        boolean primary
) {}
