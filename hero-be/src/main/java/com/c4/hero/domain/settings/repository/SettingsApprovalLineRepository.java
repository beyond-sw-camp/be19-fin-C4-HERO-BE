package com.c4.hero.domain.settings.repository;

import com.c4.hero.domain.approval.entity.ApprovalTemplate;
import com.c4.hero.domain.settings.entity.SettingsApprovalLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <pre>
 * Class Name: SettingsApprovalLineRepository
 * Description: 기본 결재선 설정 DB 접근 계층
 *
 * History
 * 2025/12/19 (민철) 최초작성
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
public interface SettingsApprovalLineRepository extends JpaRepository<SettingsApprovalLine, Integer> {
    List<SettingsApprovalLine> findByTemplate_TemplateId(Integer templateId);

    void deleteAllByTemplate(ApprovalTemplate template);
}
