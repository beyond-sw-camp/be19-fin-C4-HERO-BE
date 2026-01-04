package com.c4.hero.domain.evaluation.repository;

import com.c4.hero.domain.evaluation.entity.TemplateItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <pre>
 * Class Name: TemplateItemRepository
 * Description: JPA 사용을 위한 평가 항목 저장소
 *
 * History
 * 2025/12/07 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Repository
public interface TemplateItemRepository extends JpaRepository<TemplateItem,Integer> {

    List<TemplateItem> findByTemplateId(Integer templateId);

    void deleteByTemplateId(Integer templateId);
}
