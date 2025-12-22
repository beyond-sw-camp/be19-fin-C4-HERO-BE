package com.c4.hero.domain.payroll.payment.repository;

import com.c4.hero.domain.payroll.payment.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <pre>
 * Repository Name : PaymentHistoryRepository
 * Description     : 지급 이력(PaymentHistory) 엔티티 관리 리포지토리
 *
 * 역할
 *  - 급여 지급(PAID) 처리 시 생성되는 PaymentHistory 엔티티 저장/조회
 *  - 특정 payrollId 에 대한 지급 이력이 이미 존재하는지 검사
 *
 * History
 *   2025/12/17 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Integer> {

    /**
     * 주어진 payrollId에 대해 지급 이력이 존재하는지 확인
     *
     * @param payrollId 급여 ID
     * @return 존재하면 true, 없으면 false
     */
    boolean existsByPayrollId(Integer payrollId);
}
