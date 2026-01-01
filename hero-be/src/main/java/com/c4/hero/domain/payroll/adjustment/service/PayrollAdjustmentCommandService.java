package com.c4.hero.domain.payroll.adjustment.service;

import com.c4.hero.domain.payroll.adjustment.entity.PayrollAdjustment;
import com.c4.hero.domain.payroll.adjustment.repository.PayrollAdjustmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <pre>
 * Class Name : PayrollAdjustmentCommandService
 * Description : 급여 조정 승인 시 조정 내역을 생성하는 Command 서비스
 *
 * History
 *  2025/12/31 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class PayrollAdjustmentCommandService {

    private final PayrollAdjustmentRepository repository;

    /**
     * 승인된 급여 조정 내역 반영
     *
     * - 전자결재 승인 완료 이벤트 리스너에서 호출됨
     * - 급여(Payroll) 엔티티를 직접 수정하지 않음
     * - 계산 단계에서 합산 처리될 조정 내역만 생성
     *
     * @param approvalDocId 전자결재 문서 ID
     * @param createdBy     조정 등록자 (employee_id)
     * @param payrollId     조정 대상 급여 ID
     * @param reason        조정 사유
     * @param sign          조정 부호 ("+" / "-")
     * @param amount        조정 금액 (null 인 경우 0으로 처리)
     * @param effectiveMonth 조정 적용 급여월 (YYYY-MM)
     */
    @Transactional
    public void applyApprovedAdjustment(
            Integer approvalDocId,
            Integer createdBy,
            Integer payrollId,
            String reason,
            String sign,
            Integer amount,
            String effectiveMonth
    ) {
        PayrollAdjustment adj = PayrollAdjustment.builder()
                .reason(reason)
                .sign(sign)
                .amount(amount == null ? 0 : amount)
                .effectiveMonth(effectiveMonth)
                .status("APPROVED")
                .payrollId(payrollId)
                .createdBy(createdBy)
                .approvalDocId(approvalDocId)
                .build();

        repository.save(adj);
    }
}
