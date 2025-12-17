package com.c4.hero.domain.notification.service;

import com.c4.hero.domain.notification.dto.NotificationDTO;
import com.c4.hero.domain.notification.dto.NotificationRegistDTO;
import com.c4.hero.domain.notification.mapper.NotificationMapper;
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

 * </pre>
 *
 * @author 혜원
 * @version 2.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationCommandService {

    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 알림 생성 및 실시간 전송
     *
     * @param notificationRegistDTO 알림 등록 정보
     * @return NotificationDTO 생성된 알림 정보
     */
    @Transactional
    public NotificationDTO registAndSendNotification(NotificationRegistDTO notificationRegistDTO) {

        // 파라미터로 받은 RegistDTO 값을 주입
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .employeeId(notificationRegistDTO.getEmployeeId())
                .type(notificationRegistDTO.getType())
                .title(notificationRegistDTO.getTitle())
                .message(notificationRegistDTO.getMessage())
                .link(notificationRegistDTO.getLink())
                .attendanceId(notificationRegistDTO.getAttendanceId())
                .payrollId(notificationRegistDTO.getPayrollId())
                .documentId(notificationRegistDTO.getDocumentId())
                .evaluationId(notificationRegistDTO.getEvaluationId())
                .build();

        // DB 저장
        notificationMapper.insertNotification(notificationDTO);
        log.info("알림 DB 저장완료: type={}, employeeId={}",
                notificationDTO.getType(), notificationDTO.getEmployeeId());

        // WebSocket으로 실시간 전송
        try {
            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + notificationDTO.getEmployeeId(),
                    notificationDTO
            );
            log.info("알림 WebSocket 전송 완료: employeeId={}", notificationDTO.getEmployeeId());
        } catch (Exception e) {
            log.error("WebSocket 전송 실패: {}", e.getMessage());
            // DB는 저장됐으니 실시간 전송 실패해도 괜찮음
        }

        return notificationDTO;
    }

    /**
     * 특정 직원의 알림 목록 조회
     *
     * @param employeeId 직원 ID
     * @return List<NotificationDTO> 알림 목록
     */
    public List<NotificationDTO> registAllNotification(Integer employeeId) {
        return notificationMapper.selectAllNotification(employeeId);
    }

    /**
     * 미읽은 알림 개수 조회
     *
     * @param employeeId 직원 ID
     * @return int 미읽은 알림 개수
     */
    public int findUnreadNotification(Integer employeeId) {
        return notificationMapper.selectUnreadNotification(employeeId);
    }

    /**
     * 알림 읽음 처리
     *
     * @param notificationId 알림 ID
     */
    @Transactional
    public void modifyIsRead(Integer notificationId) {
        notificationMapper.updateIsRead(notificationId);
        log.info("알림 읽음 처리: notificationId={}", notificationId);
    }

    /**
     * 모든 알림 읽음 처리
     *
     * @param employeeId 직원 ID
     */
    @Transactional
    public void modifyAllIsRead(Integer employeeId) {
        notificationMapper.updateAllIsRead(employeeId);
        log.info("모든 알림 읽음 처리: employeeId={}", employeeId);
    }

    /**
     * 알림 소프트 삭제
     *
     * @param notificationId 알림 ID
     */
    @Transactional
    public void softRemoveNotification(Integer notificationId) {
        notificationMapper.softDeleteNotification(notificationId);
        log.info("알림 소프트 삭제 완료: notificationId={}", notificationId);
    }

    /**
     * 소프트 삭제된 알림 복구
     *
     * @param notificationId 알림 ID
     */
    @Transactional
    public void modifyNotification(Integer notificationId) {
        notificationMapper.updateNotification(notificationId);
        log.info("알림 복구 완료: notificationId={}", notificationId);
    }

    /**
     * 알림 영구 삭제
     *
     * @param notificationId 알림 ID
     */
    @Transactional
    public void removeNotification(Integer notificationId) {
        notificationMapper.deleteNotification(notificationId);
        log.info("알림 영구 삭제 완료: notificationId={}", notificationId);
    }

    /**
     * 소프트 삭제된 알림 목록 조회
     *
     * @param employeeId 직원 ID
     * @return 삭제된 알림 목록
     */
    public List<NotificationDTO> findDeletedNotifications(Integer employeeId) {
        return notificationMapper.selectDeletedNotifications(employeeId);
    }

    /**
     * 30일 지난 소프트 삭제 알림 자동 영구 삭제
     * 매일 자정에 실행
     */
    @Scheduled(cron = "0 0 0 * * *")  // 매일 자정
    @Transactional
    public void cleanupDeletedNotifications() {
        log.info("소프트 삭제 알림 자동 정리 시작");

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // 30일 지난 삭제 알림 조회
        List<NotificationDTO> oldDeleted =
                notificationMapper.selectOldDeletedNotifications(thirtyDaysAgo);

        log.info("정리 대상 알림 개수: {}", oldDeleted.size());

        // 영구 삭제
        for (NotificationDTO notification : oldDeleted) {
            notificationMapper.deleteNotification(notification.getNotificationId());
        }

        log.info("소프트 삭제 알림 자동 정리 완료: {}개 영구 삭제", oldDeleted.size());
    }
}