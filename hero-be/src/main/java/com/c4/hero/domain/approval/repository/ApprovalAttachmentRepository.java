package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.approval.entity.ApprovalAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalAttachmentRepository extends JpaRepository<ApprovalAttachment, Integer> {
}
