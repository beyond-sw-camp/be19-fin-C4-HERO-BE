package com.c4.hero.domain.payroll.account.controller;

import com.c4.hero.domain.payroll.account.dto.BankAccountCreateRequestDTO;
import com.c4.hero.domain.payroll.account.dto.BankAccountDTO;
import com.c4.hero.domain.payroll.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    private Integer getEmployeeId(Principal principal) {
        // 테스트용으로 1번 사원 고정
        return 1;
    }

    // 내 계좌 목록 조회
    @GetMapping("/bank-accounts")
    public ResponseEntity<List<BankAccountDTO>> getMyBankAccounts(Principal principal) {
        Integer employeeId = getEmployeeId(principal);
        List<BankAccountDTO> accounts = accountService.getMyBankAccounts(employeeId);
        return ResponseEntity.ok(accounts);
    }

    // 새 계좌 추가
    @PostMapping("/bank-accounts")
    public ResponseEntity<BankAccountDTO> createMyBankAccount(
            @RequestBody BankAccountCreateRequestDTO request,
            Principal principal
    ) {
        Integer employeeId = getEmployeeId(principal);
        BankAccountDTO created = accountService.createMyBankAccount(employeeId, request);
        return ResponseEntity.ok(created);
    }

    // 급여 수령 계좌 설정
    @PutMapping("/bank-accounts/{bankAccountId}/primary")
    public ResponseEntity<Void> setPrimaryBankAccount(
            @PathVariable Integer bankAccountId,
            Principal principal
    ) {
        Integer employeeId = getEmployeeId(principal);
        accountService.setPrimaryBankAccount(employeeId, bankAccountId);
        return ResponseEntity.noContent().build();
    }
}
