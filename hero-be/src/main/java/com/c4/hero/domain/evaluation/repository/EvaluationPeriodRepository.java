package com.c4.hero.domain.evaluation.repository;

import com.c4.hero.domain.evaluation.entity.EvaluationPeriod;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * <pre>
 * Class Name: EvaluationPeriodRepository
 * Description: JPA 사용을 위한 평가 기간 저장소
 *
 * History
 * 2025/12/07 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Repository
public interface EvaluationPeriodRepository extends CrudRepository<EvaluationPeriod,Integer> {
}
