package com.c4.hero.domain.department.repository;

import com.c4.hero.domain.employee.entity.EmployeeDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <pre>
 * Interface Name: DepartmentRepository
 * Description   : 부서(Department) 및 직원-부서 매핑 엔티티(EmployeeDepartment)를
 *                 JPA로 조회/저장하기 위한 레포지토리
 *
 * History
 * 2025/12/24 (이지윤) 최초 작성 및 백엔드 코딩 컨벤션 적용
 * </pre>
 *
 * 현재는 기본적인 CRUD 기능(JpaRepository 기본 기능)만 사용하며,
 * 추후 부서 기준 커스텀 조회가 필요할 경우 메서드를 추가해서 확장할 수 있습니다.
 *
 * @author 이지윤
 * @version 1.0
 */
@Repository
public interface DepartmentRepository extends JpaRepository<EmployeeDepartment, Integer> {
}
