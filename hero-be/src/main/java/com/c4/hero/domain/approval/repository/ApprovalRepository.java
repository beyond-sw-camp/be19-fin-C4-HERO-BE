package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.approval.entity.ApprovalTemplates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalRepository extends JpaRepository<ApprovalTemplates, Integer> {
}
