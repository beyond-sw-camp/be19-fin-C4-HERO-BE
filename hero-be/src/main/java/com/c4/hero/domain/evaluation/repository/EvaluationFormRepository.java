package com.c4.hero.domain.evaluation.repository;

import com.c4.hero.domain.evaluation.entity.EvaluationForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <pre>
 * Class Name: EvaluationFormRepository
 * Description: JPA 사용을 위한 평가서 저장소
 *
 * History
 * 2025/12/14 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Repository
public interface EvaluationFormRepository extends JpaRepository<EvaluationForm,Integer> {
    List<EvaluationForm> findByEvaluationId(Integer evaluationId);
}
