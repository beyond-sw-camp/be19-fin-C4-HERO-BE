package com.c4.hero.domain.notification.scheduler.evaluation;

import com.c4.hero.domain.evaluation.entity.Evaluation;
import com.c4.hero.domain.evaluation.entity.Evaluatee;
import com.c4.hero.domain.evaluation.entity.EvaluationPeriod;
import com.c4.hero.domain.evaluation.repository.EvaluationRepository;
import com.c4.hero.domain.evaluation.repository.EvaluateeRepository;
import com.c4.hero.domain.evaluation.repository.EvaluationPeriodRepository;
import com.c4.hero.domain.notification.event.evaluation.EvaluationNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * <pre>
 * Class Name: EvaluationReminderScheduler
 * Description: 평가 제출 독촉 알림 스케줄러
 *
 * History
 * 2026/01/03 (혜원) 최초 작성
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EvaluationReminderScheduler {

    private final EvaluationRepository evaluationRepository;
    private final EvaluateeRepository evaluateeRepository;
    private final EvaluationPeriodRepository evaluationPeriodRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 매일 오전 10시에 평가 제출 독촉 알림 발송
     */
    @Scheduled(cron = "0 0 10 * * *")
    public void sendEvaluationReminders() {
        log.info("=== 평가 독촉 스케줄러 시작 ===");

        try {
            LocalDateTime now = LocalDateTime.now();

            // 1. 진행중인 평가 목록 조회 (status = 0)
            List<Evaluation> activeEvaluations = evaluationRepository.findByStatus(0);
            log.info("진행중인 평가 수: {}", activeEvaluations.size());

            if (activeEvaluations.isEmpty()) {
                log.info("진행중인 평가가 없습니다.");
                return;
            }

            int totalReminders = 0;

            for (Evaluation evaluation : activeEvaluations) {
                // 2. 평가 기간 조회
                EvaluationPeriod period = evaluationPeriodRepository.findById(
                        evaluation.getEvaluationPeriodId()
                ).orElse(null);

                if (period == null || period.getEnd() == null) {
                    continue;
                }

                // 3. 마감까지 남은 일수 계산
                long remainingDays = ChronoUnit.DAYS.between(now, period.getEnd());

                log.info("평가 확인 - evaluationId: {}, 평가명: {}, 남은일수: {}일",
                        evaluation.getEvaluationId(),
                        evaluation.getName(),
                        remainingDays);

                // 4. 마감 3일 전부터 알림 발송 (3, 2, 1일전, 당일 알림)
                if (remainingDays >= 0 && remainingDays <= 3) {
                    // 미완료 피평가자 조회 (status != 2)
                    List<Evaluatee> incompleteEvaluatees =
                            evaluateeRepository.findByEvaluationIdAndStatusNot(
                                    evaluation.getEvaluationId(), 2
                            );

                    log.info("evaluationId: {} - 미완료 피평가자 수: {}",
                            evaluation.getEvaluationId(), incompleteEvaluatees.size());

                    // 각 미완료 피평가자에게 독촉 알림
                    for (Evaluatee evaluatee : incompleteEvaluatees) {
                        publishEvaluationReminderEvent(
                                evaluation,
                                period,
                                evaluatee.getEmployeeId(),
                                (int) remainingDays
                        );
                        totalReminders++;
                    }
                }
            }

            log.info("=== 평가 독촉 스케줄러 종료 - 총 {}건 발송 ===", totalReminders);

        } catch (Exception e) {
            log.error("평가 독촉 스케줄러 실행 중 오류 발생", e);
        }
    }

    /**
     * 평가 독촉 알림 이벤트 발행
     */
    private void publishEvaluationReminderEvent(
            Evaluation evaluation,
            EvaluationPeriod period,
            Integer employeeId,
            int remainingDays
    ) {
        EvaluationNotificationEvent.EvaluationReminderEvent event =
                EvaluationNotificationEvent.EvaluationReminderEvent.builder()
                        .evaluationId(evaluation.getEvaluationId())
                        .evaluationName(evaluation.getName())
                        .evaluatorId(employeeId) // 피평가자 = 평가 받는 사람
                        .remainingDays(remainingDays)
                        .deadline(period.getEnd())
                        .build();

        log.info("평가 독촉 알림 발송 - evaluationId: {}, employeeId: {}, remainingDays: {}일",
                evaluation.getEvaluationId(), employeeId, remainingDays);

        eventPublisher.publishEvent(event);
    }
}