package com.c4.hero.domain.payroll.batch.repository;

import com.c4.hero.domain.payroll.batch.entity.PayrollBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <pre>
 * Repository Name : BatchRepository
 * Description     : 급여 배치(PayrollBatch) 엔티티 관리 리포지토리
 *
 * 역할
 *  - 급여 배치 엔티티 CRUD 관리
 *  - 급여월(salaryMonth) 기준 배치 중복 생성 방지
 *
 * 도메인 규칙
 *  - 동일 급여월(YYYY-MM)에 대한 급여 배치는 하나만 존재 가능
 *
 * History
 *  2025/12/15 - 동근 최초 작성
 * </pre>
 *
 *  @author 동근
 *  @version 1.0
 */
@Repository
public interface BatchRepository extends JpaRepository<PayrollBatch, Integer> {

    /**
     * 급여월 기준 급여 배치 존재 여부 확인
     *
     * @param salaryMonth 급여월 (YYYY-MM)
     * @return 존재 여부
     */
    boolean existsBySalaryMonth(String salaryMonth);

}
