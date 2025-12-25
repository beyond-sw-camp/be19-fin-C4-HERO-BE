package com.c4.hero.domain.notification.service;

import com.c4.hero.domain.notification.dto.NotificationDTO;
import com.c4.hero.domain.notification.dto.NotificationRegistDTO;
import com.c4.hero.domain.notification.dto.NotificationSettingsDTO;
import com.c4.hero.domain.notification.mapper.NotificationMapper;
import com.c4.hero.domain.notification.util.NotificationSettingsValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <pre>
 * Class Name: NotificationCommandService
 * Description: 알림 Command Service (생성, 수정, 삭제)
 *              알림 생성, 읽음 처리, 삭제, 복구 담당
 *
 * History
 * 2025/12/16 (혜원) 최초작성 (CQRS 패턴 적용 - Command 분리)
 * 2025/12/22 (혜원) 읽음 처리 및 삭제 관련 보안 파라미터(employeeId) 적용 및 로직 정비
 * </pre>
 *
 * @author 혜원
 * @version 2.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationCommandService {

    private final NotificationMapper notificationMapper;
    private final NotificationMySettingsService settingsService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 알림 생성 및 실시간 전송
     *
     * @param notificationRegistDTO 알림 등록 정보
     * @return NotificationDTO 생성된 알림 정보
     */
    @Transactional
    public NotificationDTO registAndSendNotification(NotificationRegistDTO notificationRegistDTO) {
        log.info("[알림 저장 시작]");

        // 1. 알림 설정 확인 (수신 거부 여부 등)
        NotificationSettingsDTO settings = settingsService.findSettingsByEmployeeId(
                notificationRegistDTO.getEmployeeId()
        );

        // 해당 타입의 알림이 비활성화되어 있으면 전송하지 않음
        if (!NotificationSettingsValidator.isNotificationEnabled(settings, notificationRegistDTO.getType())) {
            log.info("알림 타입이 비활성화됨: type={}, employeeId={}",
                    notificationRegistDTO.getType(),
                    notificationRegistDTO.getEmployeeId());
            return null; // 알림 발송하지 않음
        }

        // 2. DTO 생성
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .employeeId(notificationRegistDTO.getEmployeeId())
                .type(notificationRegistDTO.getType())  // 전달받은 카테고리 그대로 사용
                .title(notificationRegistDTO.getTitle())
                .message(notificationRegistDTO.getMessage())
                .link(notificationRegistDTO.getLink())
                .attendanceId(notificationRegistDTO.getAttendanceId())
                .payrollId(notificationRegistDTO.getPayrollId())
                .documentId(notificationRegistDTO.getDocumentId())
                .evaluationId(notificationRegistDTO.getEvaluationId())
                .build();

        // 3. DB 저장
        notificationMapper.insertNotification(notificationDTO);
        log.info("알림 DB 저장완료: notificationId={}, category={}, employeeId={}",
                notificationDTO.getNotificationId(),
                notificationDTO.getType(),
                notificationDTO.getEmployeeId());

        // 4. WebSocket 전송
        try {
            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + notificationDTO.getEmployeeId(),
                    notificationDTO
            );
            log.info("알림 WebSocket 전송 완료: employeeId={}", notificationDTO.getEmployeeId());
        } catch (Exception e) {
            log.error("WebSocket 전송 실패: {}", e.getMessage());
        }

        return notificationDTO;
    }

    /**
     * 특정 알림 읽음 처리 (복구된 기능!)
     *
     * @param notificationId 알림 ID
     * @param employeeId 직원 ID (로그 및 보안용)
     */
    @Transactional
    public void modifyIsRead(Integer notificationId, Integer employeeId) {
        notificationMapper.updateIsRead(notificationId);
        log.info("알림 읽음 처리 완료: notificationId={}, employeeId={}", notificationId, employeeId);
    }

    /**
     * 모든 알림 읽음 처리
     *
     * @param employeeId 직원 ID
     */
    @Transactional
    public void modifyAllIsRead(Integer employeeId) {
        notificationMapper.updateAllIsRead(employeeId);
        log.info("모든 알림 읽음 처리 완료: employeeId={}", employeeId);
    }

    /**
     * 알림 소프트 삭제
     *
     * @param notificationId 알림 ID
     * @param employeeId 직원 ID
     */
    @Transactional
    public void softRemoveNotification(Integer notificationId, Integer employeeId) {
        notificationMapper.softDeleteNotification(notificationId);
        log.info("알림 소프트 삭제 완료: notificationId={}, employeeId={}", notificationId, employeeId);
    }

    /**
     * 소프트 삭제된 알림 복구
     *
     * @param notificationId 알림 ID
     * @param employeeId 직원 ID
     */
    @Transactional
    public void modifyNotification(Integer notificationId, Integer employeeId) {
        notificationMapper.updateNotification(notificationId);
        log.info("알림 복구 완료: notificationId={}, employeeId={}", notificationId, employeeId);
    }

    /**
     * 알림 영구 삭제
     *
     * @param notificationId 알림 ID
     * @param employeeId 직원 ID
     */
    @Transactional
    public void removeNotification(Integer notificationId, Integer employeeId) {
        notificationMapper.deleteNotification(notificationId);
        log.info("알림 영구 삭제 완료: notificationId={}, employeeId={}", notificationId, employeeId);
    }

    /**
     * 30일 지난 소프트 삭제 알림 자동 영구 삭제
     * 매일 자정에 실행
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupDeletedNotifications() {
        log.info("[스케줄러] 소프트 삭제 알림 자동 정리 시작");

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // 30일 지난 삭제 알림 조회
        List<NotificationDTO> oldDeleted =
                notificationMapper.selectOldDeletedNotifications(thirtyDaysAgo);

        log.info("[스케줄러] 정리 대상 알림 개수: {}", oldDeleted.size());

        // 영구 삭제
        for (NotificationDTO notification : oldDeleted) {
            notificationMapper.deleteNotification(notification.getNotificationId());
        }

        log.info("[스케줄러] 소프트 삭제 알림 자동 정리 완료: {}개 영구 삭제", oldDeleted.size());
    }
}