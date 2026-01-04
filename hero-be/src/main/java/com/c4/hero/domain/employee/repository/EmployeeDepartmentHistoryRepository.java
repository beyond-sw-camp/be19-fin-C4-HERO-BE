package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.EmployeeDepartmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <pre>
 * Interface Name: EmployeeDepartmentHistoryRepository
 * Description: EmployeeDepartmentHistory 엔티티에 대한 데이터 접근을 위한 Repository
 *
 * History
 * 2025/12/09 (승건) 최초 작성
 * 2025/12/29 (승건) 직원 ID로 이력 조회 기능 추가
 * </pre>
 *
 * @author 승건
 * @version 1.1
 */
public interface EmployeeDepartmentHistoryRepository extends JpaRepository<EmployeeDepartmentHistory, Integer> {

    /**
     * 특정 직원의 부서 변경 이력을 조회합니다.
     *
     * @param employeeId 직원 ID
     * @return 부서 변경 이력 리스트
     */
    List<EmployeeDepartmentHistory> findByEmployee_EmployeeIdOrderByChangedAtDesc(Integer employeeId);
}
