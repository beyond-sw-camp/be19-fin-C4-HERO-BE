package com.c4.hero.domain.notification.controller;

import com.c4.hero.domain.notification.dto.NotificationSettingsDTO;
import com.c4.hero.domain.notification.service.NotificationSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <pre>
 * Class Name: NotificationSettingsController
 * Description: 알림 설정 REST API 컨트롤러
 *
 * History
 * 2025/12/17 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/notifications/settings")
@RequiredArgsConstructor
public class NotificationSettingsController {

    private final NotificationSettingsService settingsService;

    /**
     * 알림 설정 조회
     *
     * @param employeeId 직원 ID
     * @return 알림 설정
     */
    @GetMapping("/{employeeId}")
    public ResponseEntity<NotificationSettingsDTO> findSettings(@PathVariable("employeeId") Integer employeeId) {
        log.info("알림 설정 조회 API 호출: employeeId={}", employeeId);

        NotificationSettingsDTO findedSettings = settingsService.findSettingsByEmployeeId(employeeId);

        return ResponseEntity.ok(findedSettings);
    }

    /**
     * 알림 설정 수정
     *
     * @param employeeId 직원 ID
     * @param settings 알림 설정
     * @return 수정된 설정
     */
    @PutMapping("/{employeeId}")
    public ResponseEntity<NotificationSettingsDTO> modifySettings(
            @PathVariable Integer employeeId,
            @RequestBody NotificationSettingsDTO settings) {

        log.info("알림 설정 수정 API 호출: employeeId={}, settings={}", employeeId, settings);

        // employeeId 설정
        settings.setEmployeeId(employeeId);

        NotificationSettingsDTO modifiedSettings = settingsService.modifySettings(settings);

        return ResponseEntity.ok(modifiedSettings);
    }
}