package com.c4.hero.domain.retirement.repository;

import com.c4.hero.domain.retirement.entity.Retirement;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <pre>
 * Interface Name: RetirementRepository
 * Description: 퇴직 기록(Retirement) 엔티티에 대한 데이터 접근을 위한 Repository
 *
 * History
 * 2025/12/30 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
public interface RetirementRepository extends JpaRepository<Retirement, Integer> {
}
