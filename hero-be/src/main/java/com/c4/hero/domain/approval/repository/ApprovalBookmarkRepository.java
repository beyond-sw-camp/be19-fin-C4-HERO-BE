/**
 * <pre>
 * Interface Name : ApprovalBookmarkRepository
 * Description    : 전자결재 문서 템플릿 즐겨찾기 Repository
 *
 * History
 * 2025/12/15 (변민철) 최초 작성
 * </pre>
 *
 * @author 변민철
 * @version 1.0
 */
package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.approval.entity.ApprovalBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApprovalBookmarkRepository
        extends JpaRepository<ApprovalBookmark, Integer> {

    /**
     * 특정 사원이 특정 문서 템플릿을 즐겨찾기 했는지 조회
     *
     * @param empId      사원 ID
     * @param templateId 문서 템플릿 ID
     * @return 즐겨찾기 정보
     */
    Optional<ApprovalBookmark> findByEmpIdAndTemplateId(
            Integer empId,
            Integer templateId
    );

    /**
     * 사원이 즐겨찾기한 문서 템플릿 ID 목록 조회
     * <p>
     * 목록 조회 시 즐겨찾기 여부 판단을 위한 성능 최적화용 쿼리
     *
     * @param empId 사원 ID
     * @return 즐겨찾기한 문서 템플릿 ID 목록
     */
    @Query("""
            SELECT b.templateId
            FROM ApprovalBookmark b
            WHERE b.empId = :empId
            """)
    List<Integer> findTemplateIdsByEmpId(
            @Param("empId") Integer empId
    );
}
