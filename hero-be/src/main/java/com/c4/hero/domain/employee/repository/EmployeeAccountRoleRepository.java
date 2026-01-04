package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.Account;
import com.c4.hero.domain.employee.entity.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <pre>
 * Interface Name: EmployeeAccountRoleRepository
 * Description: AccountRole 엔티티에 대한 데이터 접근을 위한 Repository
 *
 * History
 * 2025/12/09 승건 최초 작성
 * 2025/12/15 승건 권한 삭제 기능 추가
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
public interface EmployeeAccountRoleRepository extends JpaRepository<AccountRole, Integer> {
    void deleteAllByAccount(Account account);
}
