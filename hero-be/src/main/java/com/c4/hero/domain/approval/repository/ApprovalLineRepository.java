package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.approval.entity.ApprovalLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <pre>
 * Interface Name : ApprovalLineRepository
 * Description    : 결재선 Repository
 *                  문서의 결재선 정보 조회 및 관리
 *
 * History
 *   2025/12/26 (민철) 최초 작성
 *   2026/01/01 (민철) 메서드 주석 추가
 *   2026/01/03 (혜원) 알림 독촉을 위해 조회 추가
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */
@Repository
public interface ApprovalLineRepository extends JpaRepository<ApprovalLine, Integer> {

    /**
     * 문서 ID로 결재선 목록 조회 (seq 순서로 정렬)
     * 문서 상세 조회 시 결재선을 순서대로 표시할 때 사용
     *
     * @param docId 문서 ID
     * @return 결재선 목록 (seq 오름차순 정렬)
     */
    List<ApprovalLine> findByDocIdOrderBySeqAsc(Integer docId);

    /**
     * 문서 ID로 결재선 삭제
     * 문서 삭제 또는 임시저장 문서 수정 시 기존 결재선을 모두 삭제할 때 사용
     *
     * @param docId 문서 ID
     */
    void deleteByDocId(Integer docId);

    /**
     * 문서 ID와 결재선 상태로 결재선 목록 조회
     *
     * @param docId 문서 ID
     * @param lineStatus 결재선 상태 (PENDING / APPROVED / REJECTED)
     * @return 해당 문서의 특정 상태 결재선 목록
     */
    List<ApprovalLine> findByDocIdAndLineStatus(Integer docId, String lineStatus);

}