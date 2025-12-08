package com.c4.hero.domain.payroll.account.service;

import com.c4.hero.domain.payroll.account.dto.BankAccountCreateRequestDTO;
import com.c4.hero.domain.payroll.account.dto.BankAccountDTO;
import com.c4.hero.domain.payroll.account.entity.AccountEntity;
import com.c4.hero.domain.payroll.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//급여 계좌 서비스 계층 (계좌 조회, 등록, 대표계좌 설정)
@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;


//    사원의 급여 계좌 전체 목록 조회 (Param=로그인된 사원 id)
    public List<BankAccountDTO> getMyBankAccounts(Integer employeeId) {
        return accountRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // 사원의 급여 계좌 신규 등록
    public BankAccountDTO createMyBankAccount(Integer employeeId, BankAccountCreateRequestDTO request) {
//    대표계좌 존재 여부 확인
        boolean hasPrimary = accountRepository.existsByEmployeeIdAndIsPrimary(employeeId, 1);

//    새 계좌 엔티티 생성
        AccountEntity entity = AccountEntity.builder()
                .bankName(request.bankCode())
                .accountNumber(request.accountNumber())
                .accountHolder(request.accountHolder())
                .employeeId(employeeId)
                .isPrimary(hasPrimary ? 0 : 1)
                .build();

        AccountEntity saved = accountRepository.save(entity);
        return toDto(saved);
    }

//    선택한 계좌 대표계좌로 변경
    public void setPrimaryBankAccount(Integer employeeId, Integer bankAccountId) {
        List<AccountEntity> accounts = accountRepository.findByEmployeeId(employeeId);

        for (AccountEntity account : accounts) {
            if (account.getId().equals(bankAccountId)) {
                account.setIsPrimary(1);
            } else {
                account.setIsPrimary(0);
            }
        }

        accountRepository.saveAll(accounts);
    }

    //응답용 DTO로 가공(AccountEntity -> BankAccountDTO로 변환하는 메서드)
    private BankAccountDTO toDto(AccountEntity entity) {
        return new BankAccountDTO(
                entity.getId(),
                entity.getBankName(),
                entity.getAccountNumber(),
                entity.getAccountHolder(),
                entity.getIsPrimary() != null && entity.getIsPrimary() == 1
        );
    }
}
