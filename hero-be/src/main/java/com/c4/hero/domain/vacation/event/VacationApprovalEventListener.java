package com.c4.hero.domain.vacation.event;

import com.c4.hero.domain.approval.event.ApprovalCompletedEvent;
import com.c4.hero.domain.vacation.service.VacationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

/**
 * <pre>
 * Class Name : VacationApprovalEventListener
 * Description: 결재 완료 이벤트를 수신하여 휴가 이력을 생성하는 리스너
 *
 * History
 * 2025/12/31 (이지윤) 휴가 신청서 처리 로직 최초 작성
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VacationApprovalEventListener {

    private final VacationService vacationService;

    /**
     * 결재 완료 이벤트를 수신하여 휴가 로그를 생성합니다.
     *
     * - templateKey가 "vacation" 인 경우에만 처리
     * - details(JSON 문자열)를 VacationService에 전달하여 VacationLog 저장
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onApprovalCompleted(ApprovalCompletedEvent event) {
        final String templateKey = event.getTemplateKey();
        log.info("[VacationListener] event captured. docId={}, templateKey={}",
                event.getDocId(), templateKey);

        try {
            if ("vacation".equals(templateKey)) {
                log.info("[VacationListener] vacation form detected. drafterId={}", event.getDrafterId());

                vacationService.createVacationLogFromApproval(
                        event.getDrafterId(),
                        event.getDetails()
                );
                log.info("휴가 신청서 처리 완료. docId={}, details={}", event.getDocId(), event.getDetails());
            }
        } catch (Exception e) {
            log.error(
                    "휴가 이벤트 적재 실패. docId={}, templateKey={}, details={}",
                    event.getDocId(),
                    templateKey,
                    event.getDetails(),
                    e
            );
        }
    }
}
