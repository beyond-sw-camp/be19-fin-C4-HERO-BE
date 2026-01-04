package com.c4.hero.domain.payroll.batch.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.batch.entity.PayrollBatch;
import com.c4.hero.domain.payroll.batch.repository.BatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <pre>
 * Class Name : PayrollBatchStatusTxService
 * Description : 급여 배치 상태 전이 전용 트랜잭션 분리(Service)
 *
 * History
 *  2025/12/18 - 동근 최초 작성
 *  2026/01/03 - 동근 권한 인가 정책 추가
 * </pre>
 *
 * @author 동근
 * @version 1.1
 */
@PayrollAdminOnly
@Service
@RequiredArgsConstructor
public class PayrollBatchStatusTxService {

    private final BatchRepository batchRepository;

    /**
     * 급여 배치 상태를 READY -> CALCULATED 로 전이
     *
     * @param batchId 급여 배치 ID
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markCalculatedInNewTx(Integer batchId) {
        PayrollBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYROLL_BATCH_NOT_FOUND));

        batch.markCalculated();
        batchRepository.save(batch);
    }
}
