package com.c4.hero.domain.payroll.account.service;

import com.c4.hero.domain.payroll.account.dto.BankAccountCreateRequestDTO;
import com.c4.hero.domain.payroll.account.dto.BankAccountDTO;
import com.c4.hero.domain.payroll.account.entity.AccountEntity;
import com.c4.hero.domain.payroll.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <pre>
 * Class Name: AccountService
 * Description: 사원 급여 계좌 서비스 계층
 *
 * History
 * 2025/12/08 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;


    /**
     *  사원의 급여 계좌 전체 목록 조회
     * @param employeeId 로그인 된 사원id (현재 하드코딩, id=1)
     * @return 사원의 계좌 목록
     */

    public List<BankAccountDTO> getMyBankAccounts(Integer employeeId) {
        return accountRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * 사원의 급여 계좌 신규 등록
     * 최초 등록 계좌는 대표 계좌로 설정하고, 이후 추가 계좌는 보조 계좌로 등록
     * @param employeeId 로그인 된 사원id (현재 하드코딩, id=1)
     * @param request 계좌 생성 요청 DTO
     * @return 생성된 계좌 정보
     */
    public BankAccountDTO createMyBankAccount(Integer employeeId, BankAccountCreateRequestDTO request) {

//        대표계좌 존재 여부 확인
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

    /**
     * 엔티티를 응답용 DTO로 변환
     * @param entity AccountEntity(-> BankAccountDTO로 변환하는 메서드)
     * @return BankAccountDTO
     */
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
