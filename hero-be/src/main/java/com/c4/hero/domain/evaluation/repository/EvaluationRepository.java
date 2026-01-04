package com.c4.hero.domain.evaluation.repository;

import com.c4.hero.domain.evaluation.entity.Evaluatee;
import com.c4.hero.domain.evaluation.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <pre>
 * Class Name: EvaluationRepository
 * Description: JPA 사용을 위한 평가 저장소
 *
 * History
 * 2025/12/07 (김승민) 최초 작성
 * 2026/01/03 (혜원) 알림 발송용 조회 추가
 * </pre>
 *
 * @author 김승민
 */

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation,Integer> {

    List<Evaluation> findByTemplateId(Integer templateId);

    /**
     * 평가 상태로 조회
     *
     * @param status 평가 상태 (0: 미시작, 1: 진행중, 2: 완료)
     * @return 해당 상태의 평가 목록
     */
    List<Evaluation> findByStatus(Integer status);

}