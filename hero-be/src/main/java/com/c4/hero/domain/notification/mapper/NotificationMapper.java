package com.c4.hero.domain.notification.mapper;

import com.c4.hero.domain.notification.dto.NotificationDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <pre>
 * Interface Name: NotificationMapper
 * Description: 알림 데이터 접근 매퍼
 *
 * History
 * 2025/12/11 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Mapper
public interface NotificationMapper {

    /**
     * 알림 생성
     *
     * @param notification 알림 정보
     */
    void insertNotification(NotificationDTO notification);

    /**
     * 특정 직원의 알림 목록 조회
     *
     * @param employeeId 직원 ID
     * @return List<NotificationDto> 알림 목록
     */
    List<NotificationDTO> selectAllNotification(Integer employeeId);

    /**
     * 미읽은 알림 개수 조회
     *
     * @param employeeId 직원 ID
     * @return int 미읽은 알림 개수
     */
    int countUnreadNotification(Integer employeeId);

    /**
     * 알림 읽음 처리
     *
     * @param notificationId 알림 ID
     */
    void updateIsRead(Integer notificationId);

    /**
     * 모든 알림 읽음 처리
     *
     * @param employeeId 직원 ID
     */
    void updateAllIsRead(Integer employeeId);
}