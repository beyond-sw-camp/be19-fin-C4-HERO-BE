package com.c4.hero.domain.dashboard.controller;

import com.c4.hero.common.response.CustomResponse;
import com.c4.hero.domain.dashboard.dto.WorkSystemTemplateDTO;
import com.c4.hero.domain.notification.service.AttendanceNotificationEventService;
import com.c4.hero.domain.auth.security.CustomUserDetails;
import com.c4.hero.domain.dashboard.dto.ApprovalStatsDTO;
import com.c4.hero.domain.dashboard.dto.AttendanceStatsDTO;
import com.c4.hero.domain.dashboard.dto.ClockInRequestDTO;
import com.c4.hero.domain.dashboard.dto.ClockOutRequestDTO;
import com.c4.hero.domain.dashboard.dto.ClockStatusDTO;
import com.c4.hero.domain.dashboard.dto.MonthlySummaryDTO;
import com.c4.hero.domain.dashboard.dto.VacationStatsDTO;
import com.c4.hero.domain.dashboard.dto.WeeklyStatsDTO;
import com.c4.hero.domain.dashboard.service.DashboardService;
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

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * <pre>
 * Class Name  : DashboardController
 * Description : 대시보드 통계 REST API 컨트롤러
 *
 * History
 * 2025/12/26 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Tag(name = "홈 대시보드", description = "대시보드 통계 API")
@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService timeClockService;
    private final AttendanceNotificationEventService attendanceNotificationEventService;
    /**
     * 출근 처리
     * POST /api/dashboard/clock-in
     *
     * @param userDetails 인증된 사용자 정보
     * @return 성공 응답
     */
    @Operation(summary = "출근 처리",
            description = "현재 시각으로 출근을 기록합니다. 09시 이후 출근 시 지각 처리됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "출근 처리 성공"),
            @ApiResponse(responseCode = "400", description = "이미 출근 처리됨 (TC001)", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PostMapping("/clock-in")
    public ResponseEntity<CustomResponse<Void>> clockIn(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer employeeId = userDetails.getEmployeeId();
        Integer departmentId = userDetails.getDepartmentId();
        log.info("=== 출근 API 호출 === 사원ID: {}, 부서ID: {}", employeeId, departmentId);

        // 출근 요청 DTO 생성 (현재 시각)
        ClockInRequestDTO dto = new ClockInRequestDTO();
        dto.setWorkDate(LocalDate.now());
        dto.setStartTime(LocalTime.now());
        dto.setWorkSystemTypeId(1);
        dto.setWorkSystemTemplateId(1);

        // 출근 처리
        timeClockService.clockIn(employeeId, departmentId, dto);
        attendanceNotificationEventService.clockIn(employeeId); // 이벤트 서비스 호출
        return ResponseEntity.ok(CustomResponse.success());
    }

    /**
     * 퇴근 처리
     * POST /api/dashboard/clock-out
     *
     * @param userDetails 인증된 사용자 정보
     * @return 성공 응답
     */
    @Operation(summary = "퇴근 처리",
            description = "현재 시각으로 퇴근을 기록합니다. 18시 이전 퇴근 시 조퇴 처리됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "퇴근 처리 성공"),
            @ApiResponse(responseCode = "400", description = "출근 기록 없음 (TC002) 또는 이미 퇴근 처리됨 (TC003)", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PostMapping("/clock-out")
    public ResponseEntity<CustomResponse<Void>> clockOut(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer employeeId = userDetails.getEmployeeId();
        log.info("=== 퇴근 API 호출 === 사원ID: {}", employeeId);

        // 퇴근 요청 DTO 생성 (현재 시각)
        ClockOutRequestDTO dto = new ClockOutRequestDTO();
        dto.setWorkDate(LocalDate.now());
        dto.setEndTime(LocalTime.now());

        // 퇴근 처리
        timeClockService.clockOut(employeeId, dto);
        attendanceNotificationEventService.clockOut(employeeId); // 이벤트 서비스 호출


        return ResponseEntity.ok(CustomResponse.success());
    }

    /**
     * 오늘 출퇴근 상태 조회
     * GET /api/dashboard/status
     *
     * @param userDetails 인증된 사용자 정보
     * @return 출퇴근 상태 정보
     */
    @Operation(summary = "오늘 출퇴근 상태 조회",
            description = "현재 로그인한 사원의 오늘 출퇴근 상태를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @GetMapping("/status")
    public ResponseEntity<CustomResponse<ClockStatusDTO>> getTodayStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer employeeId = userDetails.getEmployeeId();
        log.info("=== 출퇴근 상태 조회 API 호출 === 사원ID: {}", employeeId);

        // 오늘 상태 조회
        ClockStatusDTO status = timeClockService.getTodayStatus(employeeId, LocalDate.now());

        return ResponseEntity.ok(CustomResponse.success(status));
    }

    /**
     * 이번 주 근무 통계 조회 (실시간 근무 중 시간 포함)
     * GET /api/dashboard/weekly-stats
     *
     * @param userDetails 인증된 사용자 정보
     * @return 주간 근무 통계
     */
    @Operation(summary = "이번 주 근무 통계 조회",
            description = "이번 주 월요일부터 오늘까지의 총 근무시간을 조회합니다. 현재 근무 중이면 실시간 시간도 포함됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @GetMapping("/weekly-stats")
    public ResponseEntity<CustomResponse<WeeklyStatsDTO>> getWeeklyStats(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer employeeId = userDetails.getEmployeeId();
        log.info("=== 주간 통계 조회 API 호출 === 사원ID: {}", employeeId);

        // 주간 통계 조회
        WeeklyStatsDTO stats = timeClockService.getWeeklyStats(employeeId);

        return ResponseEntity.ok(CustomResponse.success(stats));
    }

    /**
     * 이번 달 요약 통계 조회
     * GET /api/dashboard/monthly-summary
     *
     * @param userDetails 인증된 사용자 정보
     * @return 월간 요약 통계
     */
    @Operation(summary = "이번 달 요약 통계 조회",
            description = "이번 달 근무일수, 남은 연차, 사용한 휴가일수를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @GetMapping("/monthly-summary")
    public ResponseEntity<CustomResponse<MonthlySummaryDTO>> getMonthlySummary(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer employeeId = userDetails.getEmployeeId();
        log.info("=== 월간 요약 API 호출 === 사원ID: {}", employeeId);

        MonthlySummaryDTO summary = timeClockService.getMonthlySummary(employeeId);

        return ResponseEntity.ok(CustomResponse.success(summary));
    }

    /**
     * 출근 통계 조회 (이번 달)
     * GET /api/dashboard/attendance-stats
     *
     * @param userDetails 인증된 사용자 정보
     * @return 출근 통계
     */
    @Operation(summary = "출근 통계 조회",
            description = "이번 달 정상, 지각, 결근, 조퇴 일수를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @GetMapping("/attendance-stats")
    public ResponseEntity<CustomResponse<AttendanceStatsDTO>> getAttendanceStats(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer employeeId = userDetails.getEmployeeId();
        log.info("=== 출근 통계 API 호출 === 사원ID: {}", employeeId);

        AttendanceStatsDTO stats = timeClockService.getAttendanceStats(employeeId);

        return ResponseEntity.ok(CustomResponse.success(stats));
    }

    /**
     * 휴가 현황 조회 (이번 달)
     * GET /api/dashboard/vacation-stats
     *
     * @param userDetails 인증된 사용자 정보
     * @return 휴가 현황 통계
     */
    @Operation(summary = "휴가 현황 조회",
            description = "이번 달 연차, 반차, 병가, 기타 휴가 사용 일수를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @GetMapping("/vacation-stats")
    public ResponseEntity<CustomResponse<VacationStatsDTO>> getVacationStats(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer employeeId = userDetails.getEmployeeId();
        log.info("=== 휴가 현황 API 호출 === 사원ID: {}", employeeId);

        VacationStatsDTO stats = timeClockService.getVacationStats(employeeId);

        return ResponseEntity.ok(CustomResponse.success(stats));
    }

    /**
     * 결재 현황 조회
     * GET /api/dashboard/approval-stats
     *
     * @param userDetails 인증된 사용자 정보
     * @return 결재 현황 통계
     */
    @Operation(summary = "결재 현황 조회",
            description = "진행 중, 완료, 반려된 결재 건수를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @GetMapping("/approval-stats")
    public ResponseEntity<CustomResponse<ApprovalStatsDTO>> getApprovalStats(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer employeeId = userDetails.getEmployeeId();
        log.info("=== 결재 현황 API 호출 === 사원ID: {}", employeeId);

        ApprovalStatsDTO stats = timeClockService.getApprovalStats(employeeId);

        return ResponseEntity.ok(CustomResponse.success(stats));
    }

    /**
     * 근무제 템플릿 정보 조회
     * GET /api/dashboard/work-system-template/{templateId}
     */
    @GetMapping("/work-system-template/{templateId}")
    public ResponseEntity<CustomResponse<WorkSystemTemplateDTO>> getWorkSystemTemplate(
            @PathVariable("templateId") Integer templateId) {

        WorkSystemTemplateDTO template = timeClockService.getWorkSystemTemplate(templateId);

        return ResponseEntity.ok(CustomResponse.success(template));
    }

    /**
     * 내 기본 근무제 템플릿 정보 조회
     * GET /api/dashboard/my-default-template
     */
    @GetMapping("/my-default-template")
    public ResponseEntity<CustomResponse<WorkSystemTemplateDTO>> getMyDefaultTemplate(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer employeeId = userDetails.getEmployeeId();
        log.info("=== 기본 템플릿 조회 API 호출 === 사원ID: {}", employeeId);

        WorkSystemTemplateDTO template = timeClockService.getEmployeeDefaultTemplate(employeeId);

        return ResponseEntity.ok(CustomResponse.success(template));
    }
}