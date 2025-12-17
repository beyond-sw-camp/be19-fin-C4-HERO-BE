package com.c4.hero.domain.notification.mapper;

import com.c4.hero.domain.notification.dto.NotificationSettingsDTO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <pre>
 * Interface Name: NotificationSettingsMapper
 * Description: 알림 설정 Mapper
 *
 * History
 * 2025/12/17 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Mapper
public interface NotificationSettingsMapper {

    /**
     * 알림 설정 조회
     * @param employeeId 직원 ID
     * @return 알림 설정
     */
    NotificationSettingsDTO selectSettingsByEmployeeId(Integer employeeId);

    /**
     * 알림 설정 생성 (첫 로그인 시)
     * @param settings 알림 설정
     * @return 생성된 행 수
     */
    int insertSettings(NotificationSettingsDTO settings);

    /**
     * 알림 설정 수정
     * @param settings 알림 설정
     * @return 수정된 행 수
     */
    int updateSettings(NotificationSettingsDTO settings);

    /**
     * 알림 설정 존재 여부 확인
     * @param employeeId 직원 ID
     * @return 존재 여부
     */
    int existsByEmployeeId(Integer employeeId);
}