package com.c4.hero.domain.settings.repository;

import com.c4.hero.domain.settings.entity.SettingsNotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <pre>
 * Interface Name: NotificationHistoryRepository
 * Description: 알림 발송 이력 리포지토리
 *
 * History
 * 2025/12/22 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Repository
public interface SettingsNotificationHistoryRepository extends JpaRepository<SettingsNotificationHistory, Integer> {
}