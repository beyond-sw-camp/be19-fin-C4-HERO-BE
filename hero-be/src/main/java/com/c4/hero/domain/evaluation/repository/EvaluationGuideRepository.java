package com.c4.hero.domain.evaluation.repository;

import com.c4.hero.domain.evaluation.entity.EvaluationGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationGuideRepository extends JpaRepository<EvaluationGuide,Integer> {
}
