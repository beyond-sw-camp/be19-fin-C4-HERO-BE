package com.c4.hero.domain.notification.repository;

import com.c4.hero.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <pre>
 * Class Name: NotificationRepository
 * Description: 알림 레포지토리
 *
 * History
 * 2025/12/11 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer>{
}
