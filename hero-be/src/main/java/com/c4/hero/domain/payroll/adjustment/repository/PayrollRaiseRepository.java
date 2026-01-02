package com.c4.hero.domain.payroll.adjustment.repository;

import com.c4.hero.domain.payroll.adjustment.entity.PayrollRaise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * <pre>
 * Repository Name : PayrollRaiseRepository
 * Description     : 급여 인상(PayrollRaise) 엔티티 관리 리포지토리
 *
 * History
 *
 *  2025/12/31 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public interface PayrollRaiseRepository extends JpaRepository<PayrollRaise, Integer> {
    /**
     * 사원 + 급여월 + 상태 기준 최신 급여 인상 내역 조회
     *
     * @param employeeId   사원 ID
     * @param effectiveMonth 적용 급여월 (YYYY-MM)
     * @param status       인상 상태
     * @return 최신 급여 인상 내역 Optional
     */
    Optional<PayrollRaise> findTopByEmployeeIdAndEffectiveMonthAndStatusOrderByRaiseIdDesc(
            Integer employeeId, String effectiveMonth, String status
    );
}
