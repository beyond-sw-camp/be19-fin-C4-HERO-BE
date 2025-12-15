package com.c4.hero.domain.payroll.bankaccount.controller;

import com.c4.hero.domain.payroll.bankaccount.dto.BankAccountCreateRequestDTO;
import com.c4.hero.domain.payroll.bankaccount.dto.BankAccountDTO;
import com.c4.hero.domain.payroll.bankaccount.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * <pre>
 * Class Name: BankAccountController
 * Description: 사원 계좌 관련 컨트롤러
 *
 * History
 * 2025/12/08 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */

@RestController
@RequestMapping("/api/me/payroll")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;


    // @param EmployeeId = 사용자 id 1으로 하드코딩
    private Integer getEmployeeId(Principal principal) {
        // 테스트용으로 1번 사원 고정
        return 1;
    }


    /*
    내 계좌 목록 조회
    @param principal 사용자 인증 정보
    @return 계좌 목록
     */
    @GetMapping("/bank-accounts")
    public ResponseEntity<List<BankAccountDTO>> getMyBankAccounts(Principal principal) {
        Integer employeeId = getEmployeeId(principal);
        List<BankAccountDTO> accounts = bankAccountService.getMyBankAccounts(employeeId);
        return ResponseEntity.ok(accounts);
    }

     /*
     새 계좌 추가
     @param request 계좌 생성 요청 DTO
     @param principal 사용자 인증 정보
     @return 생성된 계좌 정보
      */
    @PostMapping("/bank-accounts")
    public ResponseEntity<BankAccountDTO> createMyBankAccount(
            @RequestBody BankAccountCreateRequestDTO request,
            Principal principal
    ) {
        Integer employeeId = getEmployeeId(principal);
        BankAccountDTO created = bankAccountService.createMyBankAccount(employeeId, request);
        return ResponseEntity.ok(created);
    }

    /*
    급여 수령 계좌 설정
    @param bankAccountId 계좌 ID
    @param principal 사용자 인증 정보
    @return noContent() 204 상태코드 반환
     */
    @PutMapping("/bank-accounts/{bankAccountId}/primary")
    public ResponseEntity<Void> setPrimaryBankAccount(
            @PathVariable Integer bankAccountId,
            Principal principal
    ) {
        Integer employeeId = getEmployeeId(principal);
        bankAccountService.setPrimaryBankAccount(employeeId, bankAccountId);
        return ResponseEntity.noContent().build();
    }


    /**
     * 계좌 정보 수정
     * @param bankAccountId 계좌 ID
     * @param request 수정할 계좌 정보
     */
    @PutMapping("/bank-accounts/{bankAccountId}")
    public ResponseEntity<Void> updateMyBankAccount(
            @PathVariable Integer bankAccountId,
            @RequestBody BankAccountCreateRequestDTO request,
            Principal principal
    ) {
        Integer employeeId = getEmployeeId(principal);
        bankAccountService.updateMyBankAccount(employeeId, bankAccountId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 계좌 삭제
     * @param bankAccountId 계좌 ID
     */
    @DeleteMapping("/bank-accounts/{bankAccountId}")
    public ResponseEntity<Void> deleteMyBankAccount(
            @PathVariable Integer bankAccountId,
            Principal principal
    ) {
        Integer employeeId = getEmployeeId(principal);
        bankAccountService.deleteMyBankAccount(employeeId, bankAccountId);
        return ResponseEntity.noContent().build();
    }
}
