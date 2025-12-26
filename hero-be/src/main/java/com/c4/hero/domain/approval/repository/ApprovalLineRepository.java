package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.approval.entity.ApprovalLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}