package com.c4.hero.domain.retirement.repository;

import com.c4.hero.domain.retirement.entity.ExitReasonMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * <pre>
 * Interface Name: ExitReasonMasterRepository
 * Description: 퇴사 사유(ExitReasonMaster) 엔티티에 대한 데이터 접근을 위한 Repository
 *
 * History
 * 2025/12/30 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
public interface ExitReasonMasterRepository extends JpaRepository<ExitReasonMaster, Integer> {
    Optional<ExitReasonMaster> findByReasonName(String reasonName);
}
