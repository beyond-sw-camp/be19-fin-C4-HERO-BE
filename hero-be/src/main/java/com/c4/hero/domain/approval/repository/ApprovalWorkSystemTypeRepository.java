package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.attendance.entity.WorkSystemType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalWorkSystemTypeRepository extends JpaRepository<WorkSystemType, Integer> {
}
