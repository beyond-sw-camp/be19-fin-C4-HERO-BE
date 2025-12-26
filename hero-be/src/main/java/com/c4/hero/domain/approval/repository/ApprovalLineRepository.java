package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.approval.entity.ApprovalLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <pre>
 * Interface Name : ApprovalLineRepository
 * Description    : 결재선 Repository
 *
 * History
 *   2025/12/26 - 민철 최초 작성
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
@Repository
public interface ApprovalLineRepository extends JpaRepository<ApprovalLine, Integer> {

    /**
     * 문서 ID로 결재선 목록 조회 (seq 순서로 정렬)
     *
     * @param docId 문서 ID
     * @return 결재선 목록
     */
    List<ApprovalLine> findByDocIdOrderBySeqAsc(Integer docId);
}