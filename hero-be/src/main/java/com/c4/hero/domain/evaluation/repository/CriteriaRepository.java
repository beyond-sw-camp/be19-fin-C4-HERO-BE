package com.c4.hero.domain.evaluation.repository;

import com.c4.hero.domain.evaluation.entity.Criteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <pre>
 * Class Name: CriteriaRepository
 * Description: JPA 사용을 위한 평가 기준 저장소
 *
 * History
 * 2025/12/07 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Repository
public interface CriteriaRepository extends JpaRepository<Criteria,Integer> {

    void deleteByItemId(Integer itemId);

    void deleteByItemIdIn(List<Integer> itemIds);
}
