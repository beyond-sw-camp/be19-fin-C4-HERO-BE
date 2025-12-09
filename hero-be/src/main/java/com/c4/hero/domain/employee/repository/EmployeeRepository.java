package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * <pre>
 * Interface Name: EmployeeRepository
 * Description: Employee 엔티티에 대한 데이터 접근을 위한 Repository
 *
 * History
 * 2025/12/09 이승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    /**
     * 사번, 이메일, 또는 전화번호 중 하나라도 일치하는 직원 엔티티를 조회
     *
     * @param employeeNumber 사번
     * @param email 암호화된 이메일
     * @param phone 암호화된 전화번호
     * @return Optional<Employee>
     */
    Optional<Employee> findByEmployeeNumberOrEmailOrPhone(String employeeNumber, byte[] email, byte[] phone);
}
