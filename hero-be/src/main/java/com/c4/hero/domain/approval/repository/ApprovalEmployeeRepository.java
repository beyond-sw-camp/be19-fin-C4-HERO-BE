package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <pre>
 * Interface Name : ApprovalEmployeeRepository
 * Description    : 결재 도메인에서 사용하는 직원 정보 조회 Repository
 *                  Employee 엔티티는 employee 도메인에 속하지만,
 *                  결재 도메인에서 필요한 직원 정보 조회를 위해 별도 Repository 생성
 *
 * History
 *   2025/12/28 (민철) 최초 작성
 *   2026/01/01 (민철) 클래스 주석 및 메서드 주석 추가
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */
public interface ApprovalEmployeeRepository extends JpaRepository<Employee, Integer> {

    /**
     * 직원 ID로 직원 정보 조회
     * 급여인상신청서 작성 시 현재 기본급을 조회하는 등 직원 정보가 필요할 때 사용
     *
     * @param employeeId 직원 ID
     * @return 직원 엔티티
     */
    Employee findByEmployeeId(Integer employeeId);
}