package com.c4.hero.domain.notification.controller;

import com.c4.hero.domain.auth.security.CustomUserDetails;
import com.c4.hero.domain.notification.dto.NotificationDTO;
import com.c4.hero.domain.notification.service.NotificationCommandService;
import com.c4.hero.domain.notification.service.NotificationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <pre>
 * Class Name: NotificationController
 * Description: 알림 REST API 컨트롤러
 * JWT 토큰 기반 인증을 통해 자동으로 employeeId 추출
 *
 * History
 * 2025/12/11 (혜원) 최초 작성
 * 2025/12/15 (혜원) 알림 삭제 API 추가
 * 2025/12/16 (혜원) CQRS 패턴 적용
 * 2025/12/21 (혜원) JWT 토큰 기반 employeeId 자동 추출 적용
 * </pre>
 *
 * @author 혜원
 * @version 2.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@Tag(name = "알림 API", description = "알림 관련 API")
public class NotificationController {

    private final NotificationCommandService notificationCommandService;
    private final NotificationQueryService notificationQueryService;

    /**
     * 일반 알림 조회 (삭제되지 않은 알림)
     *
     * GET /api/notifications
     *
     * @param userDetails 인증된 사용자 정보 (Spring Security Context)
     * @return ResponseEntity<List<NotificationDTO>> 알림 목록
     */
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> findAllNotification(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(notificationQueryService.findAllNotification(userDetails.getEmployeeId()));
    }

    /**
     * 삭제된 알림 조회
     *
     * GET /api/notifications/deleted
     *
     * @param userDetails 인증된 사용자 정보 (Spring Security Context)
     * @return ResponseEntity<List<NotificationDTO>> 삭제된 알림 목록
     */
    @GetMapping("/deleted")
    public ResponseEntity<List<NotificationDTO>> findDeletedNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(notificationQueryService.findDeletedNotifications(userDetails.getEmployeeId()));
    }

    /**
     * 미읽은 알림 개수 조회
     *
     * GET /api/notifications/unread-count
     *
     * @param userDetails 인증된 사용자 정보 (Spring Security Context)
     * @return ResponseEntity<Integer> 미읽은 알림 개수
     */
    @Operation(summary = "미읽은 알림 개수 조회",
            description = "로그인한 사용자의 미읽은 알림 개수를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @GetMapping("/unread-count")
    public ResponseEntity<Integer> findUnreadCount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer employeeId = userDetails.getEmployeeId();
        log.info("미읽은 알림 개수 조회 - 사원ID: {}", employeeId);

        int count = notificationQueryService.findUnreadNotification(employeeId);
        return ResponseEntity.ok(count);
    }

    /**
     * 특정 알림 읽음 처리
     *
     * PATCH /api/notifications/{notificationId}/read
     *
     * @param userDetails 인증된 사용자 정보 (Spring Security Context)
     * @param notificationId 알림 ID
     * @return ResponseEntity<Void>
     */
    @Operation(summary = "알림 읽음 처리",
            description = "특정 알림을 읽음 상태로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음", content = @Content)
    })
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> modifyIsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer notificationId) {

        Integer employeeId = userDetails.getEmployeeId();
        log.info("알림 읽음 처리 - 사원ID: {}, notificationId: {}", employeeId, notificationId);

        notificationCommandService.modifyIsRead(notificationId, employeeId);
        return ResponseEntity.ok().build();
    }

    /**
     * 모든 알림 읽음 처리
     *
     * PATCH /api/notifications/read-all
     *
     * @param userDetails 인증된 사용자 정보 (Spring Security Context)
     * @return ResponseEntity<Void>
     */
    @Operation(summary = "모든 알림 읽음 처리",
            description = "로그인한 사용자의 모든 미읽은 알림을 읽음 상태로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PatchMapping("/read-all")
    public ResponseEntity<Void> modifyAllIsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer employeeId = userDetails.getEmployeeId();
        log.info("모든 알림 읽음 처리 - 사원ID: {}", employeeId);

        notificationCommandService.modifyAllIsRead(employeeId);
        return ResponseEntity.ok().build();
    }

    /**
     * 알림 삭제 (소프트 삭제)
     *
     * DELETE /api/notifications/{notificationId}
     *
     * @param userDetails 인증된 사용자 정보 (Spring Security Context)
     * @param notificationId 알림 ID
     * @return ResponseEntity<Void>
     */
    @Operation(summary = "알림 소프트 삭제",
            description = "특정 알림을 소프트 삭제 처리합니다. 30일 후 자동으로 영구 삭제됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음", content = @Content)
    })
    @PatchMapping("/{notificationId}")
    public ResponseEntity<Void> softRemoveNotification(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer notificationId) {

        Integer employeeId = userDetails.getEmployeeId();
        log.info("알림 소프트 삭제 - 사원ID: {}, notificationId: {}", employeeId, notificationId);

        notificationCommandService.softRemoveNotification(notificationId, employeeId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 알림 복구
     *
     * PATCH /api/notifications/{notificationId}/restore
     *
     * @param userDetails 인증된 사용자 정보 (Spring Security Context)
     * @param notificationId 알림 ID
     * @return ResponseEntity<Void>
     */
    @Operation(summary = "알림 복구",
            description = "소프트 삭제된 알림을 복구합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "복구 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음", content = @Content)
    })
    @PatchMapping("/{notificationId}/restore")
    public ResponseEntity<Void> restoreNotification(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer notificationId) {

        Integer employeeId = userDetails.getEmployeeId();
        log.info("알림 복구 - 사원ID: {}, notificationId: {}", employeeId, notificationId);

        notificationCommandService.modifyNotification(notificationId, employeeId);
        return ResponseEntity.ok().build();
    }

    /**
     * 알림 영구 삭제
     *
     * DELETE /api/notifications/{notificationId}/permanent
     *
     * @param userDetails 인증된 사용자 정보 (Spring Security Context)
     * @param notificationId 알림 ID
     * @return ResponseEntity<Void>
     */
    @Operation(summary = "알림 영구 삭제",
            description = "알림을 DB에서 완전히 삭제합니다. 복구 불가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음", content = @Content)
    })
    @DeleteMapping("/{notificationId}/permanent")
    public ResponseEntity<Void> removeNotification(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer notificationId) {

        Integer employeeId = userDetails.getEmployeeId();
        log.info("알림 영구 삭제 - 사원ID: {}, notificationId: {}", employeeId, notificationId);

        notificationCommandService.removeNotification(notificationId, employeeId);
        return ResponseEntity.noContent().build();
    }
}