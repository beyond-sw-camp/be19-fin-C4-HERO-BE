package com.c4.hero.domain.payroll.batch.repository;

import com.c4.hero.domain.payroll.batch.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * <pre>
 * Repository Name : PayrollRepository
 * Description     : 사원별 급여(Payroll) 엔티티 관리 리포지토리
 *
 * 역할
 *  - 사원 단위 급여 엔티티 CRUD 관리
 *  - (사원 + 급여월) 기준 중복 급여 생성 방지
 *
 * 도메인 규칙
 *  - 동일 사원(employeeId)은 동일 급여월(salaryMonth)에 대해
 *    하나의 급여 데이터만 가질 수 있음
 *
 * History
 *  2025/12/15 - 동근 최초 작성
 * </pre>
 *
 *  @author 동근
 *  @version 1.0
 */

public interface PayrollRepository extends JpaRepository<Payroll, Integer> {

    /**
     * 사원 + 급여월 기준 급여 조회
     *
     * @param employeeId 사원 ID
     * @param salaryMonth 급여월 (YYYY-MM)
     * @return 급여 엔티티 Optional
     */
    Optional<Payroll> findByEmployeeIdAndSalaryMonth(Integer employeeId, String salaryMonth);

    /**
     * 사원 + 급여월 기준 급여 존재 여부 확인
     *
     * @param employeeId 사원 ID
     * @param salaryMonth 급여월 (YYYY-MM)
     * @return 존재 여부
     */
    boolean existsByEmployeeIdAndSalaryMonth(Integer employeeId, String salaryMonth);
}
