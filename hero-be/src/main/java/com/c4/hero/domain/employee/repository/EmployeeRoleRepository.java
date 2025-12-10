package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.Role;
import com.c4.hero.domain.employee.type.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * <pre>
 * Interface Name: EmployeeRoleRepository
 * Description: Role 엔티티에 대한 데이터 접근을 위한 Repository
 *
 * History
 * 2025/12/09 이승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
public interface EmployeeRoleRepository extends JpaRepository<Role, Integer> {

    /**
     * 권한 타입으로 Role 엔티티 조회
     *
     * @param roleType 권한 타입 (e.g., RoleType.EMPLOYEE)
     * @return Optional<Role>
     */
    Optional<Role> findByRole(RoleType roleType);
}
