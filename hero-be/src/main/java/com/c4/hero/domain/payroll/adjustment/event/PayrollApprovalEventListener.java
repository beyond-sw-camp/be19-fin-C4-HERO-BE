package com.c4.hero.domain.payroll.adjustment.event;

import com.c4.hero.domain.approval.event.ApprovalCompletedEvent;
import com.c4.hero.domain.approval.event.ApprovalRejectedEvent;
import com.c4.hero.domain.payroll.adjustment.dto.PayrollAdjustmentDetailDTO;
import com.c4.hero.domain.payroll.adjustment.service.PayrollAdjustmentCommandService;
import com.c4.hero.domain.payroll.adjustment.dto.PayrollRaiseDetailDTO;
import com.c4.hero.domain.payroll.adjustment.service.PayrollRaiseCommandService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * <pre>
 * Class Name : PayrollApprovalEventListener
 * Description : 전자결재 완료/반려 이벤트를 수신하여 급여 조정/인상 도메인 반영을 수행하는 리스너
 *
 * History
 *  2025/12/31 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PayrollApprovalEventListener {

    private static final String KEY_ADJUST = "modifypayroll";
    private static final String KEY_RAISE  = "raisepayroll";

    private final PayrollAdjustmentCommandService adjustmentService;
    private final PayrollRaiseCommandService raiseService;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    /**
     * 전자결재 완료 이벤트 처리
     *
     * @param event 전자결재 완료 이벤트
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleApprovalCompleted(ApprovalCompletedEvent event) {
        String key = event.getTemplateKey();
        if (!KEY_ADJUST.equals(key) && !KEY_RAISE.equals(key)) return;

        log.info(" 급여 결재 완료 이벤트 수신 - templateKey={}, docId={}", key, event.getDocId());

        try {
            if (KEY_ADJUST.equals(key)) {
                PayrollAdjustmentDetailDTO detail =
                        objectMapper.readValue(event.getDetails(), PayrollAdjustmentDetailDTO.class);

                if (isBlank(detail.getEffectiveMonth())) {
                    detail.setEffectiveMonth(nextMonthYYYYMM());
                }

                adjustmentService.applyApprovedAdjustment(
                        event.getDocId(),
                        event.getDrafterId(),
                        detail.getPayrollId(),
                        detail.getReason(),
                        detail.getSign(),
                        detail.getAmount(),
                        detail.getEffectiveMonth()
                );

                log.info(" 급여조정 반영 완료 - docId={}, payrollId={}, amount={}{}",
                        event.getDocId(), detail.getPayrollId(), detail.getSign(), detail.getAmount());
                return;
            }

            PayrollRaiseDetailDTO detail =
                    objectMapper.readValue(event.getDetails(), PayrollRaiseDetailDTO.class);

            if (isBlank(detail.getEffectiveMonth())) {
                detail.setEffectiveMonth(nextMonthYYYYMM());
            }

            raiseService.applyApprovedRaise(
                    event.getDocId(),
                    event.getDrafterId(),                 // requested_by
                    detail.getEmployeeId(),
                    detail.getBeforeSalary(),
                    detail.getAfterSalary(),
                    detail.getReason(),
                    detail.getRaisePercent(),
                    detail.getEffectiveMonth()
            );

            log.info("급여인상 반영 완료 - docId={}, employeeId={}, {} -> {}",
                    event.getDocId(), detail.getEmployeeId(), detail.getBeforeSalary(), detail.getAfterSalary());

        } catch (Exception e) {
            log.error("급여 결재 완료 처리 중 오류 - docId={}", event.getDocId(), e);
            throw new RuntimeException("급여 결재 완료 처리 중 오류", e);
        }
    }

    /**
     * 전자결재 반려 이벤트 처리
     *
     * @param event 전자결재 반려 이벤트
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleApprovalRejected(ApprovalRejectedEvent event) {
        String key = event.getTemplateKey();
        if (!KEY_ADJUST.equals(key) && !KEY_RAISE.equals(key)) return;

        log.info("급여 결재 반려 이벤트 수신 - templateKey={}, docId={}, comment={}",
                key, event.getDocId(), event.getComment());
    }

    /**
     * 문자열 공백/NULL 여부 확인 유틸
     *
     * @param s 문자열
     * @return true = null 또는 공백
     */
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * 다음 달 급여월(yyyy-MM) 계산
     *
     * @return 다음달 급여월 문자열 (yyyy-MM)
     */
    private static String nextMonthYYYYMM() {
        return LocalDate.now(ZoneId.of("Asia/Seoul")).plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }
}