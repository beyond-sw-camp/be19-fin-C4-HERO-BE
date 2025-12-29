package com.c4.hero.domain.promotion.repository;

import com.c4.hero.domain.approval.entity.ApprovalDefaultLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionApprovalDefaultLineRepository extends JpaRepository<ApprovalDefaultLine, Integer> {

}
