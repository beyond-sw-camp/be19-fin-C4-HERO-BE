package com.c4.hero.domain.settings.repository;

import com.c4.hero.domain.settings.dto.SettingsDefaultRefDTO;
import com.c4.hero.domain.settings.entity.SettingsApprovalRef;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <pre>
 * Class Name: SettingsApprovalRefRepository
 * Description: 참조 목록 설정 DB 접근 계층
 *
 * History
 * 2025/12/19 (민철) 최초작성
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
public interface SettingsApprovalRefRepository extends JpaRepository<SettingsApprovalRef, Integer> {
    List<SettingsApprovalRef> findByTemplate_TemplateId(Integer templateId);
}
