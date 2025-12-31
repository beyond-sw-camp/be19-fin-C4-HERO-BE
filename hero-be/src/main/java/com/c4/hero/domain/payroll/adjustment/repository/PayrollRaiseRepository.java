package com.c4.hero.domain.payroll.adjustment.repository;

import com.c4.hero.domain.payroll.adjustment.entity.PayrollRaise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PayrollRaiseRepository extends JpaRepository<PayrollRaise, Integer> {
    Optional<PayrollRaise> findTopByEmployeeIdAndEffectiveMonthAndStatusOrderByRaiseIdDesc(
            Integer employeeId, String effectiveMonth, String status
    );
}
