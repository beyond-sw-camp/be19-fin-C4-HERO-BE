package com.c4.hero.domain.payroll.policy.repository;

import com.c4.hero.domain.payroll.policy.entity.BatchPolicySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * <pre>
 * Class Name : BatchPolicySnapshotRepository
 * Description : 급여 배치 정책 스냅샷(BatchPolicySnapshot) 조회를 위한 Repository
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public interface BatchPolicySnapshotRepository extends JpaRepository<BatchPolicySnapshot, Integer> {

    /**
     * 급여 배치 ID로 정책 스냅샷 조회
     *
     * @param batchId 급여 배치 식별자
     * @return 해당 배치에 연결된 정책 스냅샷 (없을 수 있음)
     */
    Optional<BatchPolicySnapshot> findByBatchId(Integer batchId);
}
