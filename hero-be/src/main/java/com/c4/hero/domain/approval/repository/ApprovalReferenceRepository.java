package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.approval.entity.ApprovalReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <pre>
 * Interface Name : ApprovalReferenceRepository
 * Description    : 결재 참조자 Repository
 *
 * History
 *   2025/12/26 - 민철 최초 작성
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
@Repository
public interface ApprovalReferenceRepository extends JpaRepository<ApprovalReference, Integer> {
}