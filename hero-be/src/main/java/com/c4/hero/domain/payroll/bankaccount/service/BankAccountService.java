package com.c4.hero.domain.payroll.bankaccount.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.domain.auth.security.LoginOnly;
import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.bankaccount.dto.BankAccountCreateRequestDTO;
import com.c4.hero.domain.payroll.bankaccount.dto.BankAccountDTO;
import com.c4.hero.domain.payroll.bankaccount.entity.BankAccount;
import com.c4.hero.domain.payroll.bankaccount.repository.BankAccountRepository;
import com.c4.hero.domain.payroll.payment.mapper.PaymentHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <pre>
 * Class Name: BankAccountService
 * Description: 사원 급여 계좌 서비스 계층
 *
 * History
 * 2025/12/08 - 동근 최초 작성
 * 2026/01/03 - 동근 권한 인가 정책 추가
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@LoginOnly
@Service
@RequiredArgsConstructor
@Transactional
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final PaymentHistoryMapper paymentHistoryMapper;

    /**
     *  사원의 급여 계좌 전체 목록 조회
     * @param employeeId 로그인 된 사원id (현재 하드코딩, id=1)
     * @return 사원의 계좌 목록
     */

    public List<BankAccountDTO> getMyBankAccounts(Integer employeeId) {
        return bankAccountRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId)
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
        boolean hasPrimary = bankAccountRepository.existsByEmployeeIdAndIsPrimary(employeeId, 1);
//    새 계좌 엔티티 생성
        BankAccount entity = BankAccount.builder()
                .bankName(request.bankCode())
                .accountNumber(request.accountNumber())
                .accountHolder(request.accountHolder())
                .employeeId(employeeId)
                .isPrimary(hasPrimary ? 0 : 1)
                .build();

        BankAccount saved = bankAccountRepository.save(entity);
        return toDto(saved);
    }

//    선택한 계좌 대표계좌로 변경
    public void setPrimaryBankAccount(Integer employeeId, Integer bankAccountId) {
        List<BankAccount> accounts = bankAccountRepository.findByEmployeeId(employeeId);

        for (BankAccount account : accounts) {
            if (account.getId().equals(bankAccountId)) {
                account.setIsPrimary(1);
            } else {
                account.setIsPrimary(0);
            }
        }

        bankAccountRepository.saveAll(accounts);
    }

    /**
     * 엔티티를 응답용 DTO로 변환
     * @param entity AccountEntity(-> BankAccountDTO로 변환하는 메서드)
     * @return BankAccountDTO
     */
    private BankAccountDTO toDto(BankAccount entity) {
        return new BankAccountDTO(
                entity.getId(),
                entity.getBankName(),
                entity.getAccountNumber(),
                entity.getAccountHolder(),
                entity.getIsPrimary() != null && entity.getIsPrimary() == 1
        );
    }


    /**
     * 사원의 급여 계좌 정보 수정
     * @param employeeId 사원 ID
     * @param bankAccountId 수정할 급여 계좌 ID
     * @param request 급여 계좌 수정 요청 DTO
     */
    public void updateMyBankAccount(Integer employeeId, Integer bankAccountId,
                                    BankAccountCreateRequestDTO request) {

        BankAccount entity = bankAccountRepository
                .findByIdAndEmployeeId(bankAccountId, employeeId)
                .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));

        entity.setBankName(request.bankCode());
        entity.setAccountNumber(request.accountNumber());
        entity.setAccountHolder(request.accountHolder());
        // JPA 더티체킹으로 자동 업데이트
    }

    /**
     * 사원의 급여 계좌 삭제
     * - 대표(기본) 계좌는 삭제할 수 없다.
     * - 사원은 항상 최소 1개의 대표 계좌를 보유해야 한다.
     *
     * @param employeeId 사원 ID
     * @param bankAccountId 삭제할 계좌 ID
     */
    @Transactional
    public void deleteMyBankAccount(Integer employeeId, Integer bankAccountId) {

        // 사원 소유 계좌인지 먼저 검증
        BankAccount target = bankAccountRepository
                .findByIdAndEmployeeId(bankAccountId, employeeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BANK_ACCOUNT_NOT_FOUND));

        // 대표 계좌 삭제 금지
        boolean isPrimary = target.getIsPrimary() != null && target.getIsPrimary() == 1;
        if (isPrimary) {
            throw new BusinessException(ErrorCode.BANK_PRIMARY_DELETE_NOT_ALLOWED);
        }

        // 지급 이력 있으면 삭제 불가
        if (paymentHistoryMapper.existsByBankAccountId(bankAccountId) == 1) {
            throw new BusinessException(ErrorCode.BANK_ACCOUNT_HAS_PAYMENT_HISTORY);
        }

        // 계좌가 1개뿐이면 삭제 불가
        long cnt = bankAccountRepository.countByEmployeeId(employeeId);
        if (cnt <= 1) {
            throw new BusinessException(ErrorCode.BANK_MIN_ONE_REQUIRED);
        }

        // 삭제
        bankAccountRepository.delete(target);

        // 삭제 후 대표 계좌가 없으면 승격
        boolean hasPrimary = bankAccountRepository.existsByEmployeeIdAndIsPrimary(employeeId, 1);
        if (!hasPrimary) {
            List<BankAccount> remain = bankAccountRepository.findByEmployeeId(employeeId);
            if (remain.isEmpty()) {
                throw new BusinessException(ErrorCode.BANK_MIN_ONE_REQUIRED);
            }
            remain.get(0).setIsPrimary(1);
        }
    }
}
