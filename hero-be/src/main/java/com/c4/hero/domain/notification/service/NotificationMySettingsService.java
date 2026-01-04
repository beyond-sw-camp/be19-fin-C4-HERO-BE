package com.c4.hero.domain.notification.service;

import com.c4.hero.domain.notification.dto.NotificationSettingsDTO;
import com.c4.hero.domain.notification.mapper.NotificationSettingsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <pre>
 * Class Name: NotificationSettingsService
 * Description: 알림 설정 서비스
 *
 * History
 * 2025/12/17 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationMySettingsService {

    private final NotificationSettingsMapper settingsMapper;

    /**
     * 알림 설정 조회 (없으면 기본값 생성)
     *
     * @param employeeId 직원 ID
     * @return 알림 설정
     */
    @Transactional
    public NotificationSettingsDTO findSettingsByEmployeeId(Integer employeeId) {
        log.info("알림 설정 조회 시작: employeeId={}", employeeId);

        NotificationSettingsDTO settings = settingsMapper.selectSettingsByEmployeeId(employeeId);

        // 설정이 없으면 기본값으로 생성
        if (settings == null) {
            log.info("알림 설정 없음. 기본값 생성: employeeId={}", employeeId);
            settings = registDefaultSettings(employeeId);
        }

        log.info("알림 설정 조회 완료: {}", settings);
        return settings;
    }

    /**
     * 알림 설정 수정
     *
     * @param settings 알림 설정
     * @return 수정된 설정
     */
    @Transactional
    public NotificationSettingsDTO modifySettings(NotificationSettingsDTO settings) {
        log.info("알림 설정 수정 시작: {}", settings);

        int result = settingsMapper.updateSettings(settings);

        if (result == 0) {
            log.error("알림 설정 수정 실패: employeeId={}", settings.getEmployeeId());
            throw new RuntimeException("알림 설정 수정 실패");
        }

        log.info("알림 설정 수정 완료: employeeId={}", settings.getEmployeeId());

        // 수정된 설정 반환
        return settingsMapper.selectSettingsByEmployeeId(settings.getEmployeeId());
    }

    /**
     * 기본 설정 생성 (모두 활성화)
     *
     * @param employeeId 직원 ID
     * @return 생성된 설정
     */
    private NotificationSettingsDTO registDefaultSettings(Integer employeeId) {
        NotificationSettingsDTO defaultSettings = NotificationSettingsDTO.builder()
                .employeeId(employeeId)
                // 알림 타입 모두 활성화
                .attendanceEnabled(true)
                .payrollEnabled(true)
                .approvalEnabled(true)
                .leaveEnabled(true)
                .evaluationEnabled(true)
                .systemEnabled(true)
                // 브라우저 알림만 활성화
                .browserNotification(true)
                .emailNotification(false)
                .smsNotification(false)
                .build();

        settingsMapper.insertSettings(defaultSettings);

        return defaultSettings;
    }
}