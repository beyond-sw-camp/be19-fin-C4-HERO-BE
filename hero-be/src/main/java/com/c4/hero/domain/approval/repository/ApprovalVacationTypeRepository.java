package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.vacation.entity.VacationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalVacationTypeRepository extends JpaRepository<VacationType, Integer> {
}
