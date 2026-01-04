package com.c4.hero.domain.payroll.policy.service;

import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.policy.entity.BatchPolicySnapshot;
import com.c4.hero.domain.payroll.policy.repository.BatchPolicySnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <pre>
 * Class Name : PolicySnapshotTxService
 * Description : 급여 배치 기준 정책 스냅샷 저장 트랜잭션 서비스
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 *  2026/01/03 - 동근 권한 인가 정책 추가
 * </pre>
 *
 * @author 동근
 * @version 1.1
 */
@PayrollAdminOnly
@Service
@RequiredArgsConstructor
public class PolicySnapshotTxService {

    private final BatchPolicySnapshotRepository snapshotRepository;

    /**
     * 배치 기준 정책 스냅샷 저장(교체)
     *  - 동일 batchId의 스냅샷이 이미 존재하면 삭제 후 재등록
     *
     * @param batchId 급여 배치 ID
     * @param policyId 스냅샷 대상 급여 정책 ID
     * @param salaryMonth 급여월(YYYY-MM)
     * @param snapshotJson 정책 전체 스냅샷(JSON 문자열)
     */
    @Transactional
    public void saveSnapshot(Integer batchId, Integer policyId, String salaryMonth, String snapshotJson) {
        snapshotRepository.findByBatchId(batchId).ifPresent(snapshotRepository::delete);

        snapshotRepository.save(BatchPolicySnapshot.builder()
                .batchId(batchId)
                .policyId(policyId)
                .salaryMonth(salaryMonth)
                .snapshotJson(snapshotJson)
                .build());
    }
}
