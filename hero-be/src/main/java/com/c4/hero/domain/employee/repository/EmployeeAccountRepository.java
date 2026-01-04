package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.Account;
import com.c4.hero.domain.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * <pre>
 * Interface Name: EmployeeAccountRepository
 * Description: Account 엔티티에 대한 데이터 접근을 위한 Repository
 *
 * History
 * 2025/12/9 (승건) 최초 작성
 * 2025/12/9 (승건) findByAccount 메소드 추가
 * 2025/12/29 (승건) 사번으로 계정 조회 메소드 추가
 * 2025/12/31 (승건) 직원 엔티티로 계정 조회 메소드 추가
 * </pre>
 *
 * @author 이승건
 * @version 1.2
 */
public interface EmployeeAccountRepository extends JpaRepository<Account, Integer> {

    /**
     * 계정 아이디로 Account 엔티티를 조회
     *
     * @param account 계정 아이디
     * @return Optional<Account>
     */
    Optional<Account> findByAccount(String account);

    /**
     * 계정 아이디로 Account 엔티티를 조회 (권한 정보 포함)
     * LazyInitializationException 방지를 위해 Fetch Join 사용
     *
     * @param account 계정 아이디
     * @return Optional<Account>
     */
    @Query("SELECT a FROM Account a JOIN FETCH a.accountRoles ar JOIN FETCH ar.role WHERE a.account = :account")
    Optional<Account> findByAccountWithRoles(@Param("account") String account);

    /**
     * 직원 ID로 계정 조회
     *
     * @param employeeId 직원 ID
     * @return Optional<Account>
     */
    Optional<Account> findByEmployee_EmployeeId(Integer employeeId);

    /**
     * 사번으로 계정 조회
     *
     * @param employeeNumber 사번
     * @return Optional<Account>
     */
    Optional<Account> findByEmployee_EmployeeNumber(String employeeNumber);

    /**
     * 직원 엔티티로 계정 조회
     *
     * @param employee 직원 엔티티
     * @return Optional<Account>
     */
    Optional<Account> findByEmployee(Employee employee);
}
