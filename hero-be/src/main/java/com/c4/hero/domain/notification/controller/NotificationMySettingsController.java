package com.c4.hero.domain.notification.controller;

import com.c4.hero.domain.auth.security.CustomUserDetails;
import com.c4.hero.domain.notification.dto.NotificationSettingsDTO;
import com.c4.hero.domain.notification.service.NotificationMySettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * <pre>
 * Class Name: NotificationSettingsController
 * Description: 알림 설정 REST API 컨트롤러
 * JWT 토큰 기반 인증을 통해 자동으로 employeeId 추출
 *
 * History
 * 2025/12/17 (혜원) 최초 작성
 * 2025/12/21 (혜원) JWT 토큰 기반 employeeId 자동 추출 적용
 * </pre>
 *
 * @author 혜원
 * @version 2.1
 */
@Slf4j
@RestController
@RequestMapping("/api/notifications/settings")
@RequiredArgsConstructor
@Tag(name = "알림 설정 API", description = "알림 설정 조회 및 수정")
public class NotificationMySettingsController {

    private final NotificationMySettingsService settingsService;

    /**
     * 알림 설정 조회
     *
     * GET /api/notifications/settings
     *
     * @param userDetails 인증된 사용자 정보 (Spring Security Context)
     * @return ResponseEntity<NotificationSettingsDTO> 알림 설정
     */
    @Operation(summary = "알림 설정 조회",
            description = "로그인한 사용자의 알림 설정을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = NotificationSettingsDTO.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @GetMapping
    public ResponseEntity<NotificationSettingsDTO> findSettings(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // SecurityContext에 보관된 CustomUserDetails에서 바로 employeeId 추출
        Integer employeeId = userDetails.getEmployeeId();
        log.info("알림 설정 조회 - 사원명: {}, 사원ID: {}", userDetails.getEmployeeName(), employeeId);

        NotificationSettingsDTO settings = settingsService.findSettingsByEmployeeId(employeeId);
        return ResponseEntity.ok(settings);
    }

    /**
     * 알림 설정 수정
     *
     * PUT /api/notifications/settings
     *
     * @param userDetails 인증된 사용자 정보 (Spring Security Context)
     * @param settings 수정할 알림 설정
     * @return ResponseEntity<NotificationSettingsDTO> 수정된 알림 설정
     */
    @Operation(summary = "알림 설정 수정",
            description = "로그인한 사용자의 알림 설정을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = NotificationSettingsDTO.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PutMapping
    public ResponseEntity<NotificationSettingsDTO> modifySettings(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody NotificationSettingsDTO settings) {

        // 보안 검증: 요청 바디의 데이터보다 인증된 토큰의 정보를 우선하여 사원 번호 할당
        Integer employeeId = userDetails.getEmployeeId();
        log.info("알림 설정 수정 - 사원ID: {}, settings: {}", employeeId, settings);

        settings.setEmployeeId(employeeId);

        NotificationSettingsDTO modifiedSettings = settingsService.modifySettings(settings);
        return ResponseEntity.ok(modifiedSettings);
    }
}