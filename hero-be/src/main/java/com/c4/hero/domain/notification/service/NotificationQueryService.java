package com.c4.hero.domain.notification.service;

import com.c4.hero.domain.notification.dto.NotificationDTO;
import com.c4.hero.domain.notification.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <pre>
 * Class Name: NotificationQueryService
 * Description: 알림 Query Service (조회 전담)
 *              알림 목록 조회, 미읽은 개수 조회, 삭제된 알림 조회
 *
 * History
 * 2025/12/16 (혜원) 최초작성 (CQRS 패턴 적용 - Query 분리)
 * </pre>
 *
 * @author 혜원
 * @version 2.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationQueryService {

    private final NotificationMapper notificationMapper;

    /**
     * 특정 직원의 알림 목록 조회
     *
     * @param employeeId 직원 ID
     * @return List<NotificationDTO> 알림 목록
     */
    public List<NotificationDTO> findAllNotification(Integer employeeId) {
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
     * 소프트 삭제된 알림 목록 조회
     *
     * @param employeeId 직원 ID
     * @return 삭제된 알림 목록
     */
    public List<NotificationDTO> findDeletedNotifications(Integer employeeId) {
        return notificationMapper.selectDeletedNotifications(employeeId);
    }
}