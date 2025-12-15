package com.c4.hero.domain.notification.controller;

import com.c4.hero.common.event.NotificationEvent;
import com.c4.hero.domain.notification.dto.NotificationDTO;
import com.c4.hero.domain.notification.dto.NotificationRegistDTO;
import com.c4.hero.domain.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * <pre>
 * Class Name: NotificationTestController
 * Description: 알림 테스트용 컨트롤러
 *              개발/테스트 환경에서 알림 발송 테스트
 *
 * @Transactional 필수
 * - NotificationEventListener가 @TransactionalEventListener(AFTER_COMMIT) 사용
 * - AFTER_COMMIT은 트랜잭션 커밋 후 실행되므로 트랜잭션 컨텍스트 필수
 * - 트랜잭션이 없으면 이벤트 리스너가 실행되지 않음
 *
 * TODO: 추후 작업
 * - 각 도메인 컨트롤러/서비스에 알림 이벤트 발행 코드 추가 예정
 *
 * History
 * 2025/12/14 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.1
 */
@Slf4j
@RestController
@RequestMapping("/api/test/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Test", description = "알림 테스트 API")
@Transactional  // AFTER_COMMIT 리스너 실행을 위해 필수
public class NotificationTestController {

    private final ApplicationEventPublisher eventPublisher;
    private final NotificationService notificationService;

    /**
     * 근태 승인 알림 테스트
     *
     * 실제 프로덕션 코드 위치:
     * - AttendanceService.approveAttendance() 메서드에 추가 예정
     * - 근태 승인 처리 → DB 저장 → 알림 이벤트 발행 → 커밋 → 알림 발송
     */
    @PostMapping("/attendance-approved")
    @Operation(summary = "근태 승인 알림 발송", description = "테스트용 근태 승인 알림")
    public NotificationDTO sendAttendanceApproved(
            @RequestParam Integer employeeId,
            @RequestParam Integer attendanceId
    ) {
        log.info("테스트: 근태 승인 알림 발송 - employeeId: {}, attendanceId: {}", employeeId, attendanceId);

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(employeeId)
                .type("ATTENDANCE_APPROVED")
                .title("근태 승인")
                .message("귀하의 근태 신청이 승인되었습니다")
                .link("/attendance/detail/" + attendanceId)
                .attendanceId(attendanceId)
                .build();

        eventPublisher.publishEvent(event);

        return NotificationDTO.builder()
                .employeeId(employeeId)
                .type("ATTENDANCE_APPROVED")
                .title("근태 승인")
                .message("귀하의 근태 신청이 승인되었습니다")
                .build();
    }

    /**
     * 근태 반려 알림 테스트
     *
     * 실제 프로덕션 코드 위치:
     * - AttendanceService.rejectAttendance() 메서드에 추가 예정
     */
    @PostMapping("/attendance-rejected")
    @Operation(summary = "근태 반려 알림 발송")
    public NotificationDTO sendAttendanceRejected(
            @RequestParam Integer employeeId,
            @RequestParam Integer attendanceId
    ) {
        log.info("테스트: 근태 반려 알림 발송 - employeeId: {}, attendanceId: {}", employeeId, attendanceId);

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(employeeId)
                .type("ATTENDANCE_REJECTED")
                .title("근태 반려")
                .message("귀하의 근태 신청이 반려되었습니다")
                .link("/attendance/detail/" + attendanceId)
                .attendanceId(attendanceId)
                .build();

        eventPublisher.publishEvent(event);

        return NotificationDTO.builder()
                .employeeId(employeeId)
                .type("ATTENDANCE_REJECTED")
                .title("근태 반려")
                .message("귀하의 근태 신청이 반려되었습니다")
                .build();
    }

    /**
     * 결재 승인 알림 테스트
     *
     * 실제 프로덕션 코드 위치:
     * - DocumentService.approveDocument() 메서드에 추가 예정
     */
    @PostMapping("/document-approved")
    @Operation(summary = "결재 승인 알림 발송")
    public NotificationDTO sendDocumentApproved(
            @RequestParam Integer employeeId,
            @RequestParam Integer documentId
    ) {
        log.info("테스트: 결재 승인 알림 발송 - employeeId: {}, documentId: {}", employeeId, documentId);

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(employeeId)
                .type("DOCUMENT_APPROVED")
                .title("결재 승인")
                .message("귀하의 결재 요청이 승인되었습니다")
                .link("/document/detail/" + documentId)
                .documentId(documentId)
                .build();

        eventPublisher.publishEvent(event);

        return NotificationDTO.builder()
                .employeeId(employeeId)
                .type("DOCUMENT_APPROVED")
                .title("결재 승인")
                .message("귀하의 결재 요청이 승인되었습니다")
                .build();
    }

    /**
     * 급여 지급 알림 테스트
     *
     * 실제 프로덕션 코드 위치:
     * - PayrollService.createPayroll() 메서드에 추가 예정
     */
    @PostMapping("/payroll-paid")
    @Operation(summary = "급여 지급 알림 발송")
    public NotificationDTO sendPayrollPaid(
            @RequestParam Integer employeeId,
            @RequestParam Integer payrollId
    ) {
        log.info("테스트: 급여 지급 알림 발송 - employeeId: {}, payrollId: {}", employeeId, payrollId);

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(employeeId)
                .type("PAYROLL_PAID")
                .title("급여 지급 완료")
                .message("2025년 12월 급여가 지급되었습니다")
                .link("/payroll/detail/" + payrollId)
                .payrollId(payrollId)
                .build();

        eventPublisher.publishEvent(event);

        return NotificationDTO.builder()
                .employeeId(employeeId)
                .type("PAYROLL_PAID")
                .title("급여 지급 완료")
                .message("2025년 12월 급여가 지급되었습니다")
                .build();
    }

    /**
     * 평가 시작 알림 테스트
     *
     * 실제 프로덕션 코드 위치:
     * - EvaluationService.startEvaluation() 메서드에 추가 예정
     */
    @PostMapping("/evaluation-started")
    @Operation(summary = "평가 시작 알림 발송")
    public NotificationDTO sendEvaluationStarted(
            @RequestParam Integer employeeId,
            @RequestParam Integer evaluationId
    ) {
        log.info("테스트: 평가 시작 알림 발송 - employeeId: {}, evaluationId: {}", employeeId, evaluationId);

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(employeeId)
                .type("EVALUATION_STARTED")
                .title("평가 시작")
                .message("2025년 하반기 성과평가가 시작되었습니다")
                .link("/evaluation/detail/" + evaluationId)
                .evaluationId(evaluationId)
                .build();

        eventPublisher.publishEvent(event);

        return NotificationDTO.builder()
                .employeeId(employeeId)
                .type("EVALUATION_STARTED")
                .title("평가 시작")
                .message("2025년 하반기 성과평가가 시작되었습니다")
                .build();
    }

    /**
     * 커스텀 알림 테스트
     *
     * 자유롭게 알림 타입, 제목, 내용을 지정하여 테스트 가능
     */
    @PostMapping("/custom")
    @Operation(summary = "커스텀 알림 발송", description = "자유롭게 알림 내용 작성")
    public NotificationDTO sendCustomNotification(
            @RequestBody NotificationRegistDTO dto
    ) {
        log.info("테스트: 커스텀 알림 발송 - employeeId: {}, type: {}", dto.getEmployeeId(), dto.getType());

        NotificationEvent event = NotificationEvent.builder()
                .employeeId(dto.getEmployeeId())
                .type(dto.getType())
                .title(dto.getTitle())
                .message(dto.getMessage())
                .link(dto.getLink())
                .attendanceId(dto.getAttendanceId())
                .payrollId(dto.getPayrollId())
                .documentId(dto.getDocumentId())
                .evaluationId(dto.getEvaluationId())
                .build();

        eventPublisher.publishEvent(event);

        return NotificationDTO.builder()
                .employeeId(dto.getEmployeeId())
                .type(dto.getType())
                .title(dto.getTitle())
                .message(dto.getMessage())
                .build();
    }
}