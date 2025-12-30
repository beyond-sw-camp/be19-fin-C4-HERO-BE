package com.c4.hero.domain.attendance.event;

import com.c4.hero.domain.approval.event.ApprovalCompletedEvent;
import com.c4.hero.domain.attendance.service.AttendanceEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

/**
 * <pre>
 * Class Name : AttendanceApprovalEventListener
 * Description: 결재 완료 이벤트를 수신하여 근태 정정 요청 데이터를 생성하는 리스너
 *
 * History
 * 2025/12/29 (이지윤) 최초 작성 및 컨벤션 적용
 * </pre>
 *
 * ApprovalCompletedEvent 중, 근태기록수정신청서(templateKey=modifyworkrecord)에 대해서만
 * 근태 정정 요청 적재 로직을 수행합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AttendanceApprovalEventListener {

    /** 근태 정정 이벤트 처리 서비스 */
    private final AttendanceEventService attendanceEventService;

    /**
     * 결재 완료 이벤트를 수신하여 근태 정정 요청을 생성합니다.
     *
     * <p>동작 규칙</p>
     * <ul>
     *     <li>templateKey가 {@code modifyworkrecord} 인 경우에만 처리</li>
     *     <li>해당 문서의 details(JSON 문자열)를 그대로 서비스에 전달하여
     *         근태 정정 요청 엔티티를 생성</li>
     *     <li>트랜잭션 커밋 이후(AFTER_COMMIT) 시점에 실행</li>
     * </ul>
     *
     * @param event 결재 완료 이벤트(ApprovalCompletedEvent)
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onApprovalCompleted(ApprovalCompletedEvent event) {
        // 근태기록수정신청서가 아닌 템플릿은 무시
        if (!"modifyworkrecord".equals(event.getTemplateKey())) {
            return;
        }

        try {
            // details JSON 문자열 그대로 서비스로 전달하여 근태 정정 요청 생성
            attendanceEventService.createCorrectionRequestFromApproval(
                    event.getDrafterId(),
                    event.getDetails()
            );
        } catch (Exception e) {
            log.error(
                    "근태 정정 요청 적재 실패. docNo={}, templateKey={}",
                    event.getDocId(),
                    event.getTemplateKey(),
                    e
            );
            return;
        }

        log.info("근태기록수정신청서 처리 완료. templateKey={}, details={}",
                event.getTemplateKey(), event.getDetails());
    }
}
