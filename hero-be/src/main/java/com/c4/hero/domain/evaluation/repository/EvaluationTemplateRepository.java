package com.c4.hero.domain.evaluation.repository;

import com.c4.hero.domain.evaluation.entity.EvaluationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <pre>
 * Class Name: EvaluationTemplateRepository
 * Description: JPA 사용을 위한 평가 템플릿 저장소
 *
 * History
 * 2025/12/07 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Repository
public interface EvaluationTemplateRepository extends JpaRepository<EvaluationTemplate,Integer> {

    @Query("select t.templateId from EvaluationTemplate t")
    List<Integer> findAllTemplateIds();
}
