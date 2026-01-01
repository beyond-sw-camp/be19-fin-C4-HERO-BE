package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.approval.entity.ApprovalAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalAttachmentRepository extends JpaRepository<ApprovalAttachment, Integer> {

    /**
     * 특정 문서의 모든 첨부파일 삭제
     */
    void deleteByDocumentDocId(Integer docId);

    /**
     * 특정 문서의 모든 첨부파일 조회
     */
    List<ApprovalAttachment> findByDocumentDocId(Integer docId);
}
