package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.approval.entity.ApprovalBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * <pre>
 * Interface Name : ApprovalBookmarkRepository
 * Description    : 전자결재 문서 템플릿 즐겨찾기 Repository
 *                  사용자가 자주 사용하는 서식을 즐겨찾기로 관리
 *
 * History
 * 2025/12/15 (민철) 최초 작성
 * 2026/01/01 (민철) 메서드 주석 개선
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */
public interface ApprovalBookmarkRepository extends JpaRepository<ApprovalBookmark, Integer> {

    /**
     * 특정 사원이 특정 문서 템플릿을 즐겨찾기 했는지 조회
     * 즐겨찾기 토글 기능에서 현재 상태를 확인할 때 사용
     *
     * @param empId      사원 ID
     * @param templateId 문서 템플릿 ID
     * @return 즐겨찾기 정보 (Optional)
     */
    Optional<ApprovalBookmark> findByEmpIdAndTemplateId(
            Integer empId,
            Integer templateId
    );

    /**
     * 사원이 즐겨찾기한 문서 템플릿 ID 목록 조회
     * 서식 목록 조회 시 즐겨찾기 여부 판단을 위한 성능 최적화용 쿼리
     * N+1 문제를 방지하기 위해 ID만 조회
     *
     * @param empId 사원 ID
     * @return 즐겨찾기한 문서 템플릿 ID 목록
     */
    @Query("""
            SELECT b.templateId
            FROM ApprovalBookmark b
            WHERE b.empId = :empId
            """)
    List<Integer> findTemplateIdsByEmpId(@Param("empId") Integer empId);
}