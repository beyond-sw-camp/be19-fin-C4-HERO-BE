package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.EmployeeDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * <pre>
 * Interface Name: EmployeeDepartmentRepository
 * Description: Department 엔티티에 대한 데이터 접근을 위한 Repository
 *
 * History
 * 2025/12/09 승건 최초 작성
 * 2025/12/19 승건 상위 부서의 ID를 기준으로 조회하는 메소드 추가
 * 2025/12/31 (민철) 부서목록 조회 쿼리 메서드
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
public interface EmployeeDepartmentRepository extends JpaRepository<EmployeeDepartment, Integer> {

    /**
     * 부서 이름으로 부서 엔티티를 조회합니다.
     *
     * @param departmentName 부서 이름
     * @return Optional<EmployeeDepartment>
     */
    Optional<EmployeeDepartment> findByDepartmentName(String departmentName);

    /**
     * 주어진 상위 부서 ID에 속하는 모든 직속 하위 부서 목록을 조회합니다.
     *
     * @param parentDepartmentId 상위 부서 ID
     * @return 하위 부서 엔티티 목록
     */
    List<EmployeeDepartment> findByParentDepartmentId(Integer parentDepartmentId);

    List<EmployeeDepartment> findByDepartmentIdGreaterThan(int id);

    /**
     * 특정 직원이 부서장으로 있는 부서 목록을 조회합니다.
     *
     * @param managerId 부서장 직원 ID
     * @return 부서 목록
     */
    List<EmployeeDepartment> findByManagerId(Integer managerId);
}
