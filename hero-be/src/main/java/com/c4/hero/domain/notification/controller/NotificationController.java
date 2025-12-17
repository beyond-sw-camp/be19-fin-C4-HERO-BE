package com.c4.hero.domain.notification.controller;

import com.c4.hero.domain.notification.dto.NotificationDTO;
import com.c4.hero.domain.notification.service.NotificationCommandService;
import com.c4.hero.domain.notification.service.NotificationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <pre>
 * Class Name: NotificationController
 * Description: 알림 REST API 컨트롤러
 *
 * History
 * 2025/12/11 (혜원) 최초 작성
 * 2025/12/15 (혜원) 알림 삭제 API 추가
 * 2025/12/16 (혜원) CQRS 패턴 적용 - CommandService, QueryService 분리 주입
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@Tag(name = "알림 API", description = "알림 관련 API")
public class NotificationController {

    private final NotificationCommandService notificationCommandService;
    private final NotificationQueryService notificationQueryService;

    /**
     * 알림 목록 조회
     *
     * @param employeeId 직원 ID
     * @return ResponseEntity<List<NotificationDTO>> 알림 목록
     */
    @Operation(summary = "알림 목록 조회", description = "특정 직원의 알림 목록을 최신순으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = NotificationDTO.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @GetMapping("/{employeeId}")
    public ResponseEntity<List<NotificationDTO>> findAllNotification(
            @PathVariable Integer employeeId) {
        List<NotificationDTO> notifications = notificationQueryService.findAllNotification(employeeId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * 미읽은 알림 개수 조회
     *
     * @param employeeId 직원 ID
     * @return ResponseEntity<Integer> 미읽은 알림 개수
     */
    @Operation(summary = "미읽은 알림 개수 조회", description = "특정 직원의 미읽은 알림 개수를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @GetMapping("/{employeeId}/unread-count")
    public ResponseEntity<Integer> findUnreadCount(
            @PathVariable Integer employeeId) {
        int count = notificationQueryService.findUnreadNotification(employeeId);
        return ResponseEntity.ok(count);
    }

    /**
     * 특정 알림 읽음 처리
     *
     * @param notificationId 알림 ID
     * @return ResponseEntity<Void>
     */
    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> modifyIsRead(
            @PathVariable Integer notificationId) {
        notificationCommandService.modifyIsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    /**
     * 모든 알림 읽음 처리
     *
     * @param employeeId 직원 ID
     * @return ResponseEntity<Void>
     */
    @Operation(summary = "모든 알림 읽음 처리", description = "특정 직원의 모든 미읽은 알림을 읽음 상태로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PatchMapping("/{employeeId}/read-all")
    public ResponseEntity<Void> modifyAllIsRead(
            @PathVariable Integer employeeId) {
        notificationCommandService.modifyAllIsRead(employeeId);
        return ResponseEntity.ok().build();
    }

    /**
     * 알림 소프트 삭제
     *
     * @param notificationId 알림 ID
     * @return ResponseEntity<Void>
     */
    @Operation(summary = "알림 소프트 삭제", description = "특정 알림을 소프트 삭제 처리합니다. 30일 후 자동으로 영구 삭제됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PatchMapping("/{notificationId}/delete")
    public ResponseEntity<Void> softRemoveNotification(
            @PathVariable Integer notificationId) {
        notificationCommandService.softRemoveNotification(notificationId);
        return ResponseEntity.ok().build();
    }

    /**
     * 소프트 삭제된 알림 복구
     *
     * @param notificationId 알림 ID
     * @return ResponseEntity<Void>
     */
    @Operation(summary = "알림 복구", description = "소프트 삭제된 알림을 복구합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "복구 성공"),
            @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PatchMapping("/{notificationId}/restore")
    public ResponseEntity<Void> modifyNotification(
            @PathVariable Integer notificationId) {
        notificationCommandService.modifyNotification(notificationId);
        return ResponseEntity.ok().build();
    }

    /**
     * 알림 영구 삭제
     *
     * @param notificationId 알림 ID
     * @return ResponseEntity<Void>
     */
    @Operation(summary = "알림 영구 삭제", description = "알림을 DB에서 완전히 삭제합니다. 복구 불가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })  // ← 닫는 괄호 추가!
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> removeNotification(
            @PathVariable Integer notificationId) {
        notificationCommandService.removeNotification(notificationId);
        return ResponseEntity.ok().build();
    }

    /**
     * 소프트 삭제된 알림 목록 조회
     *
     * @param employeeId 직원 ID
     * @return ResponseEntity<List<NotificationDTO>>
     */
    @Operation(summary = "삭제된 알림 목록 조회", description = "특정 직원의 소프트 삭제된 알림 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @GetMapping("/{employeeId}/deleted")
    public ResponseEntity<List<NotificationDTO>> findDeletedNotifications(
            @PathVariable Integer employeeId) {
        List<NotificationDTO> deletedNotifications = notificationQueryService.findDeletedNotifications(employeeId);
        return ResponseEntity.ok(deletedNotifications);
    }
}