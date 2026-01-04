package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.approval.entity.ApprovalReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <pre>
 * Interface Name : ApprovalReferenceRepository
 * Description    : 결재 참조자 Repository
 *                  문서의 참조자 정보 조회 및 관리
 *
 * History
 *   2025/12/26 (민철) 최초 작성
 *   2026/01/01 (민철) 메서드 주석 추가
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */
@Repository
public interface ApprovalReferenceRepository extends JpaRepository<ApprovalReference, Integer> {

    /**
     * 문서 ID로 참조자 삭제
     * 문서 삭제 또는 임시저장 문서 수정 시 기존 참조자를 모두 삭제할 때 사용
     *
     * @param docId 문서 ID
     */
    void deleteByDocId(Integer docId);
}