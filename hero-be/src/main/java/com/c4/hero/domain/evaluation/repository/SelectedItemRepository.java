package com.c4.hero.domain.evaluation.repository;

import com.c4.hero.domain.evaluation.entity.SelectedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <pre>
 * Class Name: SelectedItemRepository
 * Description: JPA 사용을 위한 평가 선택 항목 저장소
 *
 * History
 * 2025/12/12 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Repository
public interface SelectedItemRepository extends JpaRepository<SelectedItem, Integer> {

    void deleteByEvaluationId(Integer evaluationId);
}
