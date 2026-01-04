package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.approval.entity.ApprovalTemplate;
import com.c4.hero.domain.settings.dto.response.SettingsDocumentTemplateResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * <pre>
 * Interface Name : ApprovalTemplateRepository
 * Description    : 서식 템플릿 Repository
 *                  결재 문서 서식 조회 및 관리
 *
 * History
 * 2025/12/15 (민철) 최초 작성
 * 2025/12/25 (민철) 서식 목록 조회 JPQL, 서식/기본결재선참조목록 조회 쿼리 메서드
 * 2026/01/01 (민철) 메서드 주석 추가
 *
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */
public interface ApprovalTemplateRepository extends JpaRepository<ApprovalTemplate, Integer> {

    /**
     * 서식 템플릿 목록과 각 서식의 결재선 단계 수를 함께 조회
     * 설정 화면에서 서식 목록을 표시할 때 사용
     *
     * @return 서식 템플릿 정보와 결재선 단계 수를 포함한 DTO 목록
     */
    @Query("""
    select new com.c4.hero.domain.settings.dto.response.SettingsDocumentTemplateResponseDTO(
        t.templateId,
        t.templateName,
        t.templateKey,
        t.category,
        t.description,
        cast(count(l) as integer)
    )
    from ApprovalTemplate t
    left join SettingsApprovalLine l
        on l.template = t
    group by
        t.templateId,
        t.templateKey,
        t.templateName,
        t.category,
        t.description
    """)
    List<SettingsDocumentTemplateResponseDTO> findByTemplateWithStepsCount();

    /**
     * 서식 템플릿 ID로 서식 조회
     * 서식 작성 화면 진입 시 서식 정보를 가져올 때 사용
     *
     * @param templateId 서식 템플릿 ID
     * @return 서식 템플릿 엔티티
     */
    ApprovalTemplate findByTemplateId(Integer templateId);

    /**
     * 서식 템플릿 키로 서식 조회
     * 문서 작성 시 서식 유형에 따른 정보를 가져올 때 사용
     *
     * @param formType 서식 템플릿 키 (예: vacation, overtime, resign)
     * @return 서식 템플릿 엔티티
     */
    ApprovalTemplate findByTemplateKey(String formType);
}