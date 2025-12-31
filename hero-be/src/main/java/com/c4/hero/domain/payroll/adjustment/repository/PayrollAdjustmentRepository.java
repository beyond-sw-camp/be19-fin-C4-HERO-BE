package com.c4.hero.domain.payroll.adjustment.repository;

import com.c4.hero.domain.payroll.adjustment.entity.PayrollAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollAdjustmentRepository extends JpaRepository<PayrollAdjustment, Integer> {
    List<PayrollAdjustment> findAllByEffectiveMonthAndStatus(String effectiveMonth, String status);
}
