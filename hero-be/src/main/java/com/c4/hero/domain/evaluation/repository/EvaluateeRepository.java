package com.c4.hero.domain.evaluation.repository;

import com.c4.hero.domain.evaluation.entity.Evaluatee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * <pre>
 * Class Name: CriteriaRepository
 * Description: JPA 사용을 위한 피평가자 저장소
 *
 * History
 * 2025/12/12 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Repository
public interface EvaluateeRepository extends JpaRepository<Evaluatee,Integer> {
}
