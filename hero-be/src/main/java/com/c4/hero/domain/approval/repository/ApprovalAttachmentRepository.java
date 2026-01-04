package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.approval.entity.ApprovalAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <pre>
 * Interface Name : ApprovalAttachmentRepository
 * Description    : 결재 문서 첨부파일 Repository
 *                  문서의 첨부파일 조회 및 삭제 기능 제공
 *
 * History
 *   2025/12/26 (민철) 최초 작성
 *   2026/01/01 (민철) 메서드 주석 추가
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */
public interface ApprovalAttachmentRepository extends JpaRepository<ApprovalAttachment, Integer> {

    /**
     * 특정 문서의 모든 첨부파일 삭제
     * 문서 삭제 시 연관된 첨부파일을 모두 제거할 때 사용
     *
     * @param docId 문서 ID
     */
    void deleteByDocumentDocId(Integer docId);

    /**
     * 특정 문서의 모든 첨부파일 조회
     * 문서 상세 조회 시 첨부파일 목록을 가져올 때 사용
     *
     * @param docId 문서 ID
     * @return 첨부파일 목록
     */
    List<ApprovalAttachment> findByDocumentDocId(Integer docId);
}