package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.approval.entity.ApprovalTemplate;
import com.c4.hero.domain.settings.dto.response.SettingsDocumentTemplateResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * <pre>
 * Class Name: ApprovalTemplateRepository
 * Description: 서식 조회용 DB 접근 Repository 계층
 *
 * History
 * 2025/12/15 (변민철) 최초 작성
 * 2025/12/25 (민철) 서식 목록 조회 JPQL, 서식/기본결재선참조목록 조회 쿼리 메서드
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
public interface ApprovalTemplateRepository
        extends JpaRepository<ApprovalTemplate, Integer> {

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

    ApprovalTemplate findByTemplateId(Integer templateId);

    ApprovalTemplate findByTemplateKey(String formType);
}
