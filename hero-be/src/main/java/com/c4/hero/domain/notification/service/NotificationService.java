package com.c4.hero.domain.notification.service;

import com.c4.hero.domain.notification.dto.NotificationDTO;
import com.c4.hero.domain.notification.dto.NotificationRegistDTO;
import com.c4.hero.domain.notification.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <pre>
 * Class Name: NotificationService
 * Description: 알림 비즈니스 로직 처리
 *              알림 생성, 조회, 읽음 처리 등
 *
 * History
 * 2025/12/11 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

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
        return notificationMapper.countUnreadNotification(employeeId);
    }

    /**
     * 알림 읽음 처리
     *
     * @param notificationId 알림 ID
     */
    @Transactional
    public void ModifyIsRead(Integer notificationId) {
        notificationMapper.updateIsRead(notificationId);
        log.info("알림 읽음 처리: notificationId={}", notificationId);
    }

    /**
     * 모든 알림 읽음 처리
     *
     * @param employeeId 직원 ID
     */
    @Transactional
    public void ModifyAllIsRead(Integer employeeId) {
        notificationMapper.updateAllIsRead(employeeId);
        log.info("모든 알림 읽음 처리: employeeId={}", employeeId);
    }
}