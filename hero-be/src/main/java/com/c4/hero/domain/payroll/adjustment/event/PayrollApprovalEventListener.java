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
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayrollApprovalEventListener {

    private static final String KEY_ADJUST = "payrolladjustment";
    private static final String KEY_RAISE  = "payrollraise";

    private final PayrollAdjustmentCommandService adjustmentService;
    private final PayrollRaiseCommandService raiseService;
    private final ObjectMapper objectMapper;

    @EventListener
    @Transactional
    public void handleApprovalCompleted(ApprovalCompletedEvent event) {
        String key = event.getTemplateKey();
        if (!KEY_ADJUST.equals(key) && !KEY_RAISE.equals(key)) return;

        log.info("💰 급여 결재 완료 이벤트 수신 - templateKey={}, docId={}", key, event.getDocId());

        try {
            if (KEY_ADJUST.equals(key)) {
                PayrollAdjustmentDetailDTO detail =
                        objectMapper.readValue(event.getDetails(), PayrollAdjustmentDetailDTO.class);

                if (isBlank(detail.getEffectiveMonth())) {
                    detail.setEffectiveMonth(nextMonthYYYYMM());
                }

                adjustmentService.applyApprovedAdjustment(
                        event.getDocId(),
                        event.getDrafterId(),              // created_by
                        detail.getPayrollId(),
                        detail.getReason(),
                        detail.getSign(),
                        detail.getAmount(),
                        detail.getEffectiveMonth()
                );

                log.info("✅ 급여조정 반영 완료 - docId={}, payrollId={}, amount={}{}",
                        event.getDocId(), detail.getPayrollId(), detail.getSign(), detail.getAmount());
                return;
            }

            // payrollraise
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
                    detail.getEffectiveMonth()
            );

            log.info("✅ 급여인상 반영 완료 - docId={}, employeeId={}, {} -> {}",
                    event.getDocId(), detail.getEmployeeId(), detail.getBeforeSalary(), detail.getAfterSalary());

        } catch (Exception e) {
            log.error("❌ 급여 결재 완료 처리 중 오류 - docId={}", event.getDocId(), e);
            throw new RuntimeException("급여 결재 완료 처리 중 오류", e);
        }
    }

    @EventListener
    @Transactional
    public void handleApprovalRejected(ApprovalRejectedEvent event) {
        String key = event.getTemplateKey();
        if (!KEY_ADJUST.equals(key) && !KEY_RAISE.equals(key)) return;

        // 현재 설계: 승인 시점에만 insert/update 하므로 반려는 보통 NO-OP
        log.info("🚨 급여 결재 반려 이벤트 수신 - templateKey={}, docId={}, comment={}",
                key, event.getDocId(), event.getComment());
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String nextMonthYYYYMM() {
        return LocalDate.now(ZoneId.of("Asia/Seoul")).plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }
}