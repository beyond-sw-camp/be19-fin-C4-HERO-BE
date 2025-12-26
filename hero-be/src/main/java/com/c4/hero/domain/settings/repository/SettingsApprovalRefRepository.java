package com.c4.hero.domain.settings.repository;

import com.c4.hero.domain.approval.entity.ApprovalTemplate;
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
    
    /**
     * 서식에 연관된 참조 목록 조회
     *
     * @param templateId    서식ID 
     * @return  참조 목록 조회
     */
    List<SettingsApprovalRef> findByTemplate_TemplateId(Integer templateId);

    /**
     * 서식에 연관된 참조 목록 삭제(덮어쓰기)
     *
     * @param template 
     * @return  
     */
    void deleteAllByTemplate(ApprovalTemplate template);
}
