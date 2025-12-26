package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.approval.entity.ApprovalDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalDocumentRepository extends JpaRepository<ApprovalDocument, Integer> {
}
