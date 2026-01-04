package com.c4.hero.domain.payroll.bankaccount.repository;

import com.c4.hero.domain.payroll.bankaccount.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


/**
 * <pre>
 * Class Name: BankAccountRepository
 * Description: 사원 급여 계좌 조회/저장용 JPA Repository
 *
 * History
 * 2025/12/08 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public interface BankAccountRepository extends JpaRepository<BankAccount, Integer> {

    /**
    사원의 계좌 목록을 생성일 내림차순으로 조회
     @param  employeeId 사원id (현재 하드코딩, id=1)
     @return 계좌 목록
     */
    List<BankAccount> findByEmployeeIdOrderByCreatedAtDesc(Integer employeeId);


    /**
    사원의 전체 계좌 조회
     @param  employeeId 사원id (현재 하드코딩, id=1)
     @return 계좌 목록
     */
    List<BankAccount> findByEmployeeId(Integer employeeId);


    /**
     사원이 대표 계좌를 등록했는지 여부 조회하는 메서드
     @param employeeId 사원id (현재 하드코딩, id=1)
     @param isPrimary 기본 계좌 여부(1=대표계좌, 0=보조(등록만 돼있는 계좌)
     @return 존재하면 true
     */
    boolean existsByEmployeeIdAndIsPrimary(Integer employeeId, Integer isPrimary);

    long countByEmployeeId(Integer employeeId);

    Optional<BankAccount> findByIdAndEmployeeId(Integer id, Integer employeeId);
}