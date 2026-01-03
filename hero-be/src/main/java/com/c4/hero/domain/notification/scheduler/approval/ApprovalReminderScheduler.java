package com.c4.hero.domain.notification.scheduler.approval;

import com.c4.hero.domain.approval.entity.ApprovalDocument;
import com.c4.hero.domain.approval.entity.ApprovalLine;
import com.c4.hero.domain.approval.repository.ApprovalDocumentRepository;
import com.c4.hero.domain.approval.repository.ApprovalLineRepository;
import com.c4.hero.domain.approval.service.ApprovalCommandService;
import com.c4.hero.domain.notification.event.approval.ApprovalNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Class Name: ApprovalReminderScheduler
 * Description: 결재 대기 독촉 알림 스케줄러
 *
 * History
 * 2026/01/02 (혜원) 최초 작성
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApprovalReminderScheduler {
    private final ApprovalDocumentRepository documentRepository;
    private final ApprovalLineRepository lineRepository;
    private final ApprovalCommandService approvalCommandService;

    /**
     * 매일 오전 10시에 결재 대기 독촉 알림 발송 (3일 이상인 기안자에게)
     */
    @Scheduled(cron = "0 0 10 * * *")
    public void sendApprovalReminders() {
        log.info("=== 결재 독촉 스케줄러 시작 ===");

        try {
            // 1. 진행중인 문서 조회
            List<ApprovalDocument> inProgressDocs = documentRepository.findByDocStatus("INPROGRESS");
            log.info("진행중인 문서 수: {}", inProgressDocs.size());

            if (inProgressDocs.isEmpty()) {
                log.info("진행중인 문서가 없습니다.");
                return;
            }

            int totalReminders = 0;

            for (ApprovalDocument doc : inProgressDocs) {
                log.info("문서 확인 - docId: {}, 제목: {}, 생성일: {}",
                        doc.getDocId(), doc.getTitle(), doc.getCreatedAt());

                // 2. 대기중인 결재선 조회
                List<ApprovalLine> pendingLines = lineRepository.findByDocIdAndLineStatus(
                        doc.getDocId(), "PENDING");

                log.info("docId: {} - 대기중인 결재선 수: {}", doc.getDocId(), pendingLines.size());

                for (ApprovalLine line : pendingLines) {
                    // 3. 대기 일수 계산
                    long waitingDays = ChronoUnit.DAYS.between(
                            doc.getCreatedAt(),
                            LocalDateTime.now()
                    );

                    log.info("결재선 확인 - lineId: {}, approverId: {}, 대기일수: {}일",
                            line.getLineId(), line.getApproverId(), waitingDays);

                    // 4. 3일 이상이면 바로 독촉
                    if (waitingDays >= 3) {
                        approvalCommandService.publishApprovalReminderEvent(
                                doc,
                                line,
                                (int) waitingDays
                        );
                        totalReminders++;
                        log.info("독촉 알림 발송 완료 - docId: {}, approverId: {}, waitingDays: {}일",
                                doc.getDocId(), line.getApproverId(), waitingDays);
                    } else {
                        log.info("대기일수 부족 - docId: {}, waitingDays: {}일",
                                doc.getDocId(), waitingDays);
                    }
                }
            }

            log.info("=== 결재 독촉 스케줄러 종료 - 총 {}건 발송 ===", totalReminders);

        } catch (Exception e) {
            log.error("결재 독촉 스케줄러 실행 중 오류 발생", e);
        }
    }
}