package com.c4.hero.domain.promotion.repository;

import com.c4.hero.domain.promotion.entity.PromotionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <pre>
 * Interface Name: PromotionDetailRepository
 * Description: PromotionDetail 엔티티에 대한 데이터 접근을 위한 Repository
 *
 * History
 * 2025/12/19 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
public interface PromotionDetailRepository extends JpaRepository<PromotionDetail, Integer> {
}
