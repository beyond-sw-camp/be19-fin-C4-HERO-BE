/**
 * <pre>
 * Interface Name : ApprovalTemplateRepository
 * Description    : 전자결재 문서 양식(템플릿) 관리 Repository
 *
 * History
 * 2025/12/15 (변민철) 최초 작성
 * </pre>
 *
 * @author 변민철
 * @version 1.0
 */
package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.approval.entity.ApprovalFormTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 전자결재 문서 템플릿 Repository
 */
public interface ApprovalTemplateRepository
        extends JpaRepository<ApprovalFormTemplate, Integer> {
}
