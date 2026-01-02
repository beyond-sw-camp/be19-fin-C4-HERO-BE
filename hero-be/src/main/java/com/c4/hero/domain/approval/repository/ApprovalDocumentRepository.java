package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.approval.entity.ApprovalDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * <pre>
 * Interface Name : ApprovalDocumentRepository
 * Description    : 결재 문서 Repository
 *                  결재 문서의 CRUD 및 문서 번호 생성 기능 제공
 *
 * History
 *   2025/12/26 (민철) 최초 작성
 *   2026/01/01 (민철) 메서드 주석 추가
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */
public interface ApprovalDocumentRepository extends JpaRepository<ApprovalDocument, Integer> {

    /**
     * 특정 prefix로 시작하는 문서 번호 중 마지막 문서 번호 조회
     * 새로운 문서 번호를 생성할 때 사용 (순번을 +1 증가시킴)
     *
     * 예시:
     * - prefix가 "HERO-2025-%"인 경우
     * - DB에 "HERO-2025-001", "HERO-2025-002"가 있다면
     * - "HERO-2025-002"를 반환
     *
     * @param prefix 문서 번호 prefix (예: "HERO-2025-%")
     * @return 마지막 문서 번호 (없으면 null)
     */
    @Query("SELECT d.docNo FROM ApprovalDocument d WHERE d.docNo LIKE :prefix ORDER BY d.docNo DESC LIMIT 1")
    String findLastDocNoLike(@Param("prefix") String prefix);
}