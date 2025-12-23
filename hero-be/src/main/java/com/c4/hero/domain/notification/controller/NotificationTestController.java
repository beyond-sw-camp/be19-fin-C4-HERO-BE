package com.c4.hero.domain.notification.controller;

import com.c4.hero.common.event.NotificationEvent;
import com.c4.hero.domain.auth.security.CustomUserDetails;
import com.c4.hero.domain.notification.dto.NotificationRegistDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * <pre>
 * Class Name: NotificationTestController
 * Description: 알림 테스트용 컨트롤러
 *              개발/테스트 환경에서 알림 발송 테스트
 *
 * TODO: 추후 작업
 * - 각 도메인 컨트롤러/서비스에 알림 이벤트 발행 코드 추가 예정
 *
 * History
 * 2025/12/14 (혜원) 최초 작성
 * 2025/12/19 (혜원) @Transactional 범위를 클래스에서 메소드 레벨로 축소
 * 2025/12/21 (혜원) 알림 타입 요구사항 명세서 정의대로 설계 및 JWT 토큰 연동
 * </pre>
 *
 * @author 혜원
 * @version 2.0
 */
@Slf4j
@RestController
@RequestMapping("/api/test/notifications")
@RequiredArgsConstructor
@Profile("dev")
@Tag(name = "개발용 알림 테스트 API", description = "토큰의 사용자 정보를 활용하여 모든 타입의 알림 발송을 테스트합니다.")
public class NotificationTestController {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 유효성 검사 유틸리티:
     * 1. employeeId가 파라미터로 없으면 토큰에서 추출
     * 2. 각종 FK ID가 0 이하면 DB 에러 방지를 위해 null 처리
     */
    private Integer getTargetId(Integer inputId, CustomUserDetails user) {
        return (inputId != null && inputId > 0) ? inputId : user.getEmployeeId();
    }

    private Integer validateFk(Integer id) {
        return (id != null && id > 0) ? id : null;
    }

    // ========== 근태 (Attendance) ==========

    @PostMapping("/attendance/clock-in-missing")
    @Transactional
    @Operation(summary = "출근 미체크 알림")
    public ResponseEntity<String> sendClockInMissing(
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "미입력 시 본인에게 발송") @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false) Integer attendanceId
    ) {
        Integer targetId = getTargetId(employeeId, user);
        log.info("테스트: 출근 미체크 알림 - targetId: {}, attendanceId: {}", targetId, attendanceId);

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(targetId)
                .type("attendance")
                .title("출근 미체크")
                .message("출근 체크가 없습니다. 근태를 확인해주세요.")
                .link("/attendance")
                .attendanceId(validateFk(attendanceId))
                .build();

        eventPublisher.publishEvent(event);
        return ResponseEntity.ok("이벤트 발행 완료 (대상: " + targetId + ")");
    }

    // ========== 연차 (Annual Leave) ==========

    @PostMapping("/annual-leave/expiring")
    @Transactional
    @Operation(summary = "연차 소멸 예정 알림")
    public ResponseEntity<String> sendAnnualLeaveExpiring(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Integer employeeId,
            @RequestParam Integer remainingDays
    ) {
        Integer targetId = getTargetId(employeeId, user);
        log.info("테스트: 연차 소멸 예정 알림 - targetId: {}, remainingDays: {}", targetId, remainingDays);

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(targetId)
                .type("attendance")
                .title("연차 소멸 예정")
                .message("올해 연차 " + remainingDays + "일이 소멸 예정입니다. 연차를 사용해주세요.")
                .link("/annual-leave")
                .build();

        eventPublisher.publishEvent(event);
        return ResponseEntity.ok("이벤트 발행 완료");
    }

    // ========== 인사이동 (Personnel Change) ==========

    @PostMapping("/personnel/change-notice")
    @Transactional
    @Operation(summary = "인사이동 안내 알림")
    public ResponseEntity<String> sendPersonnelChangeNotice(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Integer employeeId,
            @RequestParam String newDept
    ) {
        Integer targetId = getTargetId(employeeId, user);
        log.info("테스트: 인사이동 안내 알림 - targetId: {}, newDept: {}", targetId, newDept);

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(targetId)
                .type("attendance")
                .title("인사이동 안내")
                .message("귀하의 소속이 " + newDept + "로 변경되었습니다.")
                .link("/personnel")
                .build();

        eventPublisher.publishEvent(event);
        return ResponseEntity.ok("이벤트 발행 완료");
    }

    // ========== 결재 (Approval/Document) ==========

    @PostMapping("/approval/request-arrived")
    @Transactional
    @Operation(summary = "결재 요청 도착 알림")
    public ResponseEntity<String> sendApprovalRequestArrived(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false) Integer documentId,
            @RequestParam String documentTitle
    ) {
        Integer targetId = getTargetId(employeeId, user);
        log.info("테스트: 결재 요청 도착 알림 - targetId: {}, documentId: {}", targetId, documentId);

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(targetId)
                .type("approval")
                .title("결재 요청 도착")
                .message("새로운 결재 요청이 도착했습니다. (" + documentTitle + ")")
                .link("/document/detail/" + documentId)
                .documentId(validateFk(documentId))
                .build();

        eventPublisher.publishEvent(event);
        return ResponseEntity.ok("이벤트 발행 완료");
    }

    @PostMapping("/approval/approved")
    @Transactional
    @Operation(summary = "결재 승인 알림")
    public ResponseEntity<String> sendApprovalApproved(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false) Integer documentId,
            @RequestParam String documentTitle
    ) {
        Integer targetId = getTargetId(employeeId, user);

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(targetId)
                .type("approval")
                .title("결재 승인")
                .message("귀하의 결재 요청이 승인되었습니다. (" + documentTitle + ")")
                .link("/document/detail/" + documentId)
                .documentId(validateFk(documentId))
                .build();

        eventPublisher.publishEvent(event);
        return ResponseEntity.ok("이벤트 발행 완료");
    }

    @PostMapping("/approval/rejected")
    @Transactional
    @Operation(summary = "결재 반려 알림")
    public ResponseEntity<String> sendApprovalRejected(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false) Integer documentId,
            @RequestParam String documentTitle
    ) {
        Integer targetId = getTargetId(employeeId, user);

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(targetId)
                .type("approval")
                .title("결재 반려")
                .message("귀하의 결재 요청이 반려되었습니다. (" + documentTitle + ")")
                .link("/document/detail/" + documentId)
                .documentId(validateFk(documentId))
                .build();

        eventPublisher.publishEvent(event);
        return ResponseEntity.ok("이벤트 발행 완료");
    }

    @PostMapping("/approval/recalled")
    @Transactional
    @Operation(summary = "결재 회수 완료 알림")
    public ResponseEntity<String> sendApprovalRecalled(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false) Integer documentId,
            @RequestParam String documentTitle
    ) {
        Integer targetId = getTargetId(employeeId, user);

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(targetId)
                .type("approval")
                .title("결재 회수 완료")
                .message("결재 문서가 회수되었습니다. (" + documentTitle + ")")
                .link("/document/detail/" + documentId)
                .documentId(validateFk(documentId))
                .build();

        eventPublisher.publishEvent(event);
        return ResponseEntity.ok("이벤트 발행 완료");
    }

    @PostMapping("/approval/reminder")
    @Transactional
    @Operation(summary = "결재 대기 독촉 알림")
    public ResponseEntity<String> sendApprovalReminder(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false) Integer documentId,
            @RequestParam String documentTitle
    ) {
        Integer targetId = getTargetId(employeeId, user);

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(targetId)
                .type("approval")
                .title("결재 대기 독촉")
                .message("결재 대기 중인 문서가 있습니다. (" + documentTitle + ")")
                .link("/document/detail/" + documentId)
                .documentId(validateFk(documentId))
                .build();

        eventPublisher.publishEvent(event);
        return ResponseEntity.ok("이벤트 발행 완료");
    }

    // ========== 평가 (Evaluation) ==========

    @PostMapping("/evaluation/period-started")
    @Transactional
    @Operation(summary = "평가 시즌 시작 알림")
    public ResponseEntity<String> sendEvaluationPeriodStarted(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false) Integer evaluationId
    ) {
        Integer targetId = getTargetId(employeeId, user);

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(targetId)
                .type("evaluation")
                .title("평가 시즌 시작")
                .message("2025년 하반기 성과평가가 시작되었습니다.")
                .link("/evaluation/detail/" + evaluationId)
                .evaluationId(validateFk(evaluationId))
                .build();

        eventPublisher.publishEvent(event);
        return ResponseEntity.ok("이벤트 발행 완료");
    }

    @PostMapping("/evaluation/period-ended")
    @Transactional
    @Operation(summary = "평가 시즌 종료 알림")
    public ResponseEntity<String> sendEvaluationPeriodEnded(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false) Integer evaluationId
    ) {
        Integer targetId = getTargetId(employeeId, user);

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(targetId)
                .type("evaluation")
                .title("평가 시즌 종료")
                .message("평가 시즌이 종료되었습니다. 미제출 시 불이익이 있을 수 있습니다.")
                .link("/evaluation/detail/" + evaluationId)
                .evaluationId(validateFk(evaluationId))
                .build();

        eventPublisher.publishEvent(event);
        return ResponseEntity.ok("이벤트 발행 완료");
    }

    @PostMapping("/evaluation/result-announced")
    @Transactional
    @Operation(summary = "평가 결과 발표 알림")
    public ResponseEntity<String> sendEvaluationResultAnnounced(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false) Integer evaluationId
    ) {
        Integer targetId = getTargetId(employeeId, user);

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(targetId)
                .type("evaluation")
                .title("평가 결과 발표")
                .message("2025년 하반기 성과평가 결과가 발표되었습니다.")
                .link("/evaluation/detail/" + evaluationId)
                .evaluationId(validateFk(evaluationId))
                .build();

        eventPublisher.publishEvent(event);
        return ResponseEntity.ok("이벤트 발행 완료");
    }

    // ========== 급여 (Payroll) ==========

    @PostMapping("/payroll/statement-arrived")
    @Transactional
    @Operation(summary = "급여명세서 도착 알림")
    public ResponseEntity<String> sendPayrollStatementArrived(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false) Integer payrollId
    ) {
        Integer targetId = getTargetId(employeeId, user);

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(targetId)
                .type("payroll")
                .title("급여명세서 도착")
                .message("2025년 12월 급여명세서가 발급되었습니다.")
                .link("/payroll/detail/" + payrollId)
                .payrollId(validateFk(payrollId))
                .build();

        eventPublisher.publishEvent(event);
        return ResponseEntity.ok("이벤트 발행 완료");
    }

    @PostMapping("/payroll/batch-calculated")
    @Transactional
    @Operation(summary = "급여 배치 계산 완료 알림")
    public ResponseEntity<String> sendPayrollBatchCalculated(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Integer employeeId
    ) {
        Integer targetId = getTargetId(employeeId, user);

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(targetId)
                .type("payroll")
                .title("급여 배치 계산 완료")
                .message("급여 배치 계산이 완료되었습니다.")
                .link("/payroll")
                .build();

        eventPublisher.publishEvent(event);
        return ResponseEntity.ok("이벤트 발행 완료");
    }

    @PostMapping("/payroll/batch-confirmed")
    @Transactional
    @Operation(summary = "급여 배치 확정 알림")
    public ResponseEntity<String> sendPayrollBatchConfirmed(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Integer employeeId
    ) {
        Integer targetId = getTargetId(employeeId, user);

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(targetId)
                .type("payroll")
                .title("급여 배치 확정")
                .message("급여 배치가 확정되었습니다.")
                .link("/payroll")
                .build();

        eventPublisher.publishEvent(event);
        return ResponseEntity.ok("이벤트 발행 완료");
    }

    @PostMapping("/payroll/adjustment-requested")
    @Transactional
    @Operation(summary = "급여조정 요청 생성 알림")
    public ResponseEntity<String> sendPayrollAdjustmentRequested(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Integer employeeId
    ) {
        Integer targetId = getTargetId(employeeId, user);

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(targetId)
                .type("payroll")
                .title("급여조정 요청 생성")
                .message("새로운 급여조정 요청이 접수되었습니다.")
                .link("/payroll/adjustment")
                .build();

        eventPublisher.publishEvent(event);
        return ResponseEntity.ok("이벤트 발행 완료");
    }

    // ========== 시스템 (System) ==========

    @PostMapping("/system/notice")
    @Transactional
    @Operation(summary = "시스템 공지 발송 알림")
    public ResponseEntity<String> sendSystemNotice(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Integer employeeId,
            @RequestParam String noticeTitle
    ) {
        Integer targetId = getTargetId(employeeId, user);

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(targetId)
                .type("system")
                .title("시스템 공지")
                .message("[공지] " + noticeTitle)
                .link("/notice")
                .build();

        eventPublisher.publishEvent(event);
        return ResponseEntity.ok("이벤트 발행 완료");
    }

    @PostMapping("/system/permission-changed")
    @Transactional
    @Operation(summary = "권한 변경 알림")
    public ResponseEntity<String> sendPermissionChanged(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Integer employeeId
    ) {
        Integer targetId = getTargetId(employeeId, user);

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(targetId)
                .type("system")
                .title("권한 변경")
                .message("귀하의 시스템 권한이 변경되었습니다.")
                .link("/settings/permissions")
                .build();

        eventPublisher.publishEvent(event);
        return ResponseEntity.ok("이벤트 발행 완료");
    }

    // ========== 커스텀 알림 ==========

    @PostMapping("/custom")
    @Transactional
    @Operation(summary = "커스텀 알림 발송")
    public ResponseEntity<String> sendCustomNotification(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody NotificationRegistDTO registDTO
    ) {
        Integer targetId = getTargetId(registDTO.getEmployeeId(), user);

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(targetId)
                .type(registDTO.getType())
                .title(registDTO.getTitle())
                .message(registDTO.getMessage())
                .link(registDTO.getLink())
                .attendanceId(validateFk(registDTO.getAttendanceId()))
                .payrollId(validateFk(registDTO.getPayrollId()))
                .documentId(validateFk(registDTO.getDocumentId()))
                .evaluationId(validateFk(registDTO.getEvaluationId()))
                .build();

        eventPublisher.publishEvent(event);
        return ResponseEntity.ok("커스텀 이벤트 발행 완료");
    }
}