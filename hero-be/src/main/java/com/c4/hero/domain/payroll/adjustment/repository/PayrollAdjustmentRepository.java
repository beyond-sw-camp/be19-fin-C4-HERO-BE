package com.c4.hero.domain.payroll.adjustment.repository;

import com.c4.hero.domain.payroll.adjustment.entity.PayrollAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <pre>
 * Repository Name : PayrollAdjustmentRepository
 * Description     : 급여 조정(PayrollAdjustment) 엔티티 관리 리포지토리
 *
 * History
 *  2025/12/31 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */

public interface PayrollAdjustmentRepository extends JpaRepository<PayrollAdjustment, Integer> {

    /**
     * 특정 급여월과 상태 기준 급여 조정 내역 조회
     *
     * @param effectiveMonth 급여월 (YYYY-MM)
     * @param status         조정 상태
     * @return 급여 조정 내역 목록
     */
    List<PayrollAdjustment> findAllByEffectiveMonthAndStatus(String effectiveMonth, String status);
}
