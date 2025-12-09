package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * <pre>
 * Interface Name: DepartmentRepository
 * Description: Department 엔티티에 대한 데이터 접근을 위한 Repository
 *
 * History
 * 2025/12/09 이승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    /**
     * 부서 이름으로 부서 엔티티 조회
     *
     * @param departmentName 부서 이름
     * @return Optional<Department>
     */
    Optional<Department> findByDepartmentName(String departmentName);
}
