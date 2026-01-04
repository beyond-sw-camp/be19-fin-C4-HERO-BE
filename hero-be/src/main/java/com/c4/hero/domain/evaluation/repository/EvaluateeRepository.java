package com.c4.hero.domain.evaluation.repository;

import com.c4.hero.domain.evaluation.entity.Evaluatee;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * <pre>
 * Class Name: CriteriaRepository
 * Description: JPA 사용을 위한 피평가자 저장소
 *
 * History
 * 2025/12/12 (김승민) 최초 작성
 * 2026/01/03 (혜원) 알림 발송용 조회 추가
 * </pre>
 *
 * @author 김승민
 */

@Repository
public interface EvaluateeRepository extends JpaRepository<Evaluatee,Integer> {

    void deleteByEvaluationId(Integer evaluationId);

    Long countByEvaluationIdAndStatus(Integer evaluationId, Integer status);

    Evaluatee findByEvaluationIdAndEmployeeId(Integer evaluationId, Integer employeeId);

    Long countByEvaluationIdAndStatusNot(Integer evaluationId, int i);

    /**
     * 평가 ID와 특정 상태가 아닌 피평가자 목록 조회
     *
     * @param evaluationId 평가 ID
     * @param status 제외할 상태 (2: 완료)
     * @return 미완료 피평가자 목록
     */
    List<Evaluatee> findByEvaluationIdAndStatusNot(Integer evaluationId, Integer status);
}
