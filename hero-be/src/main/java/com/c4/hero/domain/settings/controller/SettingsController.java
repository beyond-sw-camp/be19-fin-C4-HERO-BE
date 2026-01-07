package com.c4.hero.domain.settings.controller;

import com.c4.hero.common.response.CustomResponse;
import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.entity.Grade;
import com.c4.hero.domain.employee.entity.JobTitle;
import com.c4.hero.domain.employee.entity.Role;
import com.c4.hero.domain.notification.dto.NotificationRegistDTO;
import com.c4.hero.domain.notification.service.NotificationCommandService;
import com.c4.hero.domain.settings.dto.response.DepartmentResponseDTO;
import com.c4.hero.domain.settings.dto.request.*;
import com.c4.hero.domain.settings.dto.response.*;
import com.c4.hero.domain.settings.dto.request.SettingsApprovalRequestDTO;
import com.c4.hero.domain.settings.dto.response.SettingsApprovalResponseDTO;
import com.c4.hero.domain.settings.dto.response.SettingsDepartmentResponseDTO;
import com.c4.hero.domain.settings.dto.response.SettingsDocumentTemplateResponseDTO;
import com.c4.hero.domain.settings.dto.response.SettingsPermissionsResponseDTO;
import com.c4.hero.domain.settings.service.SettingsAttendanceService;
import com.c4.hero.domain.settings.service.SettingsCommandService;
import com.c4.hero.domain.settings.service.SettingsNotificationCommandService;
import com.c4.hero.domain.settings.service.SettingsNotificationQueryService;
import com.c4.hero.domain.settings.service.SettingsQueryService;
import com.c4.hero.domain.settings.dto.response.SettingWorkSystemResponseDTO;
import com.c4.hero.domain.settings.dto.request.SettingWorkSystemRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <pre>
 * Class Name: SettingsController
 * Description: 환경 설정 관련 컨트롤러
 *
 * History
 * 2025/12/16 (승건) 최초 작성
 * 2025/12/18 (민철) 결재선 설정을 위한 컨트롤러 메서드 작성
 * 2025/12/21 (민철) 결재 관리 관련 기능 조회 api
 * 2025/12/22 (혜원) 관리자 알림 발송 및 관리 기능 추가
 * 2025/12/24 (혜원) 서비스 파일명 변경 수정, @PreAuthorize로 설정에 진입 가능한 권한체크
 * 2025/12/29 (지윤) 근태 설정 조회 및 삽입문 기능 추가
 * 2026/01/07 (승건) 스웨거 작성
 * </pre>
 *
 * @author 승건
 * @version 2.0
 */
@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'HR_MANAGER')")
@Tag(name = "환경설정 API", description = "부서, 직급, 직책, 권한, 알림, 근태 등 시스템 환경설정 API")
public class SettingsController {

    private final SettingsCommandService settingsCommandService;
    private final SettingsQueryService settingsQueryService;
    private final NotificationCommandService notificationCommandService;
    private final SettingsNotificationCommandService settingsNotificationCommandService;
    private final SettingsNotificationQueryService settingsNotificationQueryService; // 추가
    private final SettingsAttendanceService  settingsAttendanceService;

    /**
     * 부서 목록 조회 (트리 구조)
     *
     * @return 부서 트리 구조 목록
     */
    @Operation(summary = "부서 목록 조회 (트리)", description = "부서 목록을 트리 구조로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    @GetMapping("/departments")
    public ResponseEntity<CustomResponse<List<SettingsDepartmentResponseDTO>>> getDepartments() {
        List<SettingsDepartmentResponseDTO> departmentTree = settingsQueryService.getDepartmentTree();

        log.info("department: {}", departmentTree);
        return ResponseEntity.ok(CustomResponse.success(departmentTree));
    }

    /**
     * 부서 정보 트리로 한번에 저장/수정
     *
     * @param departments 저장 또는 수정할 부서 정보 목록
     * @return 성공 메시지
     */
    @Operation(summary = "부서 정보 일괄 저장/수정", description = "부서 정보를 트리 구조로 일괄 저장하거나 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "저장 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    @PostMapping("/departments/tree")
    public ResponseEntity<CustomResponse<String>> saveOrUpdateDepartments(@RequestBody List<SettingsDepartmentRequestDTO> departments) {
        settingsCommandService.updateDepartments(departments);
        return ResponseEntity.ok(CustomResponse.success("Departments updated successfully"));
    }

    /**
     * 직급 목록 조회
     *
     * @return 전체 직급 목록
     */
    @Operation(summary = "직급 목록 조회", description = "전체 직급 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    @GetMapping("/grades")
    public ResponseEntity<CustomResponse<List<Grade>>> getGrades() {
        List<Grade> grades = settingsQueryService.getAllGrades();

        log.info("grades: {}", grades);
        return ResponseEntity.ok(CustomResponse.success(grades));
    }

    /**
     * 직급 정보 한번에 저장/수정/삭제
     *
     * @param grades 수정할 직급 정보 목록
     * @return 성공 메시지
     */
    @Operation(summary = "직급 정보 일괄 저장/수정", description = "직급 정보를 일괄 저장, 수정 또는 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "저장 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    @PostMapping("/grades/batch")
    public ResponseEntity<CustomResponse<String>> updateGrades(@RequestBody List<SettingsGradeRequestDTO> grades) {
        settingsCommandService.updateGrades(grades);
        return ResponseEntity.ok(CustomResponse.success("Grades updated successfully"));
    }

    /**
     * 직책 목록 조회
     *
     * @return 전체 직책 목록
     */
    @Operation(summary = "직책 목록 조회", description = "전체 직책 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    @GetMapping("/job-titles")
    public ResponseEntity<CustomResponse<List<JobTitle>>> getJobTitles() {
        List<JobTitle> jobTitles = settingsQueryService.getAllJobTitles();

        log.info("jobTitles: {}", jobTitles);
        return ResponseEntity.ok(CustomResponse.success(jobTitles));
    }

    /**
     * 직책 정보 한번에 저장/수정/삭제
     *
     * @param jobTitles 수정할 직책 정보 목록
     * @return 성공 메시지
     */
    @Operation(summary = "직책 정보 일괄 저장/수정", description = "직책 정보를 일괄 저장, 수정 또는 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "저장 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    @PostMapping("/job-titles/batch")
    public ResponseEntity<CustomResponse<String>> updateJobTitles(@RequestBody List<SettingsJobTitleRequestDTO> jobTitles) {
        settingsCommandService.updateJobTitles(jobTitles);
        return ResponseEntity.ok(CustomResponse.success("Job titles updated successfully"));
    }

    /**
     * 로그인 정책 조회
     *
     * @return 로그인 정책 값
     */
    @Operation(summary = "로그인 정책 조회", description = "현재 설정된 로그인 정책(예: 비밀번호 변경 주기 등)을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    @GetMapping("/login-policy")
    public ResponseEntity<CustomResponse<Integer>> getLoginPolicy() {
        Integer loginPolicy = settingsQueryService.getLoginPolicy();
        return ResponseEntity.ok(CustomResponse.success(loginPolicy));
    }

    /**
     * 로그인 정책 설정
     *
     * @param policy 설정할 로그인 정책
     * @return 성공 메시지
     */
    @Operation(summary = "로그인 정책 설정", description = "로그인 정책을 설정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "설정 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    @PutMapping("/login-policy")
    public ResponseEntity<CustomResponse<String>> setLoginPolicy(@RequestBody SettingsLoginPolicyRequestDTO policy) {
        settingsCommandService.setLoginPolicy(policy);
        return ResponseEntity.ok(CustomResponse.success("Login policy updated successfully"));
    }

    /**
     * 사원 권한 조회
     *
     * @param pageable 페이징 정보
     * @param query    검색어
     * @return 각 사원들이 들고 있는 권한 정보 List
     */
    @Operation(summary = "사원 권한 목록 조회", description = "사원별 권한 정보를 페이징하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    @GetMapping("/permissions")
    public ResponseEntity<CustomResponse<PageResponse<SettingsPermissionsResponseDTO>>> getPermissions(
            Pageable pageable,
            @RequestParam(required = false) String query) {
        PageResponse<SettingsPermissionsResponseDTO> permissions = settingsQueryService.getEmployeePermissions(pageable, query);
        return ResponseEntity.ok(CustomResponse.success(permissions));
    }

    /**
     * 모든 권한 목록 조회
     *
     * @return 전체 권한 목록
     */
    @Operation(summary = "전체 권한 목록 조회", description = "시스템에 정의된 모든 권한 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    @GetMapping("/roles")
    public ResponseEntity<CustomResponse<List<Role>>> getRoles() {
        List<Role> roles = settingsQueryService.getAllRoles();
        return ResponseEntity.ok(CustomResponse.success(roles));
    }

    /**
     * 권한 설정
     *
     * @param dto 권한 설정 요청 정보
     * @return 성공 메시지
     */
    @Operation(summary = "사원 권한 설정", description = "특정 사원의 권한을 설정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "설정 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    @PutMapping("/permissions")
    public ResponseEntity<CustomResponse<String>> updatePermissions(@RequestBody SettingsPermissionsRequestDTO dto) {
        settingsCommandService.updatePermissions(dto);
        return ResponseEntity.ok(CustomResponse.success("Permissions updated successfully"));
    }

    /**
     * 결재 관리 탭 서식목록 조회 api
     *
     * @return List<SettingsDocumentTemplateResponseDTO> 서식 목록 조회
     */
    @Operation(summary = "결재 서식 목록 조회", description = "결재 관리 설정에서 사용할 수 있는 모든 문서 서식의 목록을 조회합니다.")
    @GetMapping("/approvals/templates")
    public ResponseEntity<List<SettingsDocumentTemplateResponseDTO>> getTemplates() {

        List<SettingsDocumentTemplateResponseDTO> lists = settingsQueryService.getTemplates();
        return ResponseEntity.ok().body(lists);
    }

    /**
     * 결재 관리 탭 부서목록 조회 api
     *
     * @param
     * @return List<DepartmentResponseDTO> 부서 목록
     */
    @Operation(summary = "결재 부서 목록 조회", description = "결재선 지정 및 관리를 위해 조직 내의 모든 부서 목록을 조회합니다.")
    @GetMapping("/approvals/departments")
    public ResponseEntity<List<DepartmentResponseDTO>> getApprovalDepartments() {
        List<DepartmentResponseDTO> list = settingsQueryService.getApprovalDepartments();
        return ResponseEntity.ok().body(list);
    }

    /**
     * 서식별 기본 결재선/참조목록 설정 조회 api
     *
     * @param templateId 서식 ID
     * @return settings 서식이 가지는 기본 결재선/참조목록 설정
     */
    @Operation(summary = "서식별 결재 설정 상세 조회", description = "특정 서식(templateId)에 설정된 기본 결재선 및 참조자 목록 정보를 조회합니다.")
    @GetMapping("/approvals/templates/{templateId}")
    public ResponseEntity<SettingsApprovalResponseDTO> getApprovalSettings(
            @PathVariable Integer templateId) {

        SettingsApprovalResponseDTO response = settingsQueryService.getDocSettings(templateId);
        return ResponseEntity.ok().body(response);
    }

    /**
     * 서식별 기본 결재선 설정 저장 api
     *
     * @param   settings 설정값들
     * @return ResponseEntity<String>
     */
    @Operation(summary = "서식별 기본 결재선 설정 저장", description = "특정 서식에 적용될 기본 결재선 및 참조 설정을 수정(저장)합니다.")
    @PutMapping("/approvals/templates/{templateId}")
    public ResponseEntity<Void> registDefaultLine(
            @PathVariable Integer templateId,
            @RequestBody SettingsApprovalRequestDTO settings){

        settingsCommandService.applySettings(templateId, settings);
        return ResponseEntity.ok().build();
    }


    /**
     * 전체 직원 대상 알림 발송
     *
     * @param request 알림 내용
     * @return 발송 결과
     */
    @Operation(summary = "전체 알림 발송", description = "전체 직원에게 알림을 발송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "발송 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    @PostMapping("/notifications/broadcast")
    public ResponseEntity<CustomResponse<String>> broadcastNotification(
            @RequestBody SettingsNotificationBroadcastRequestDTO request) {
        settingsNotificationCommandService.broadcastNotification(request);
        return ResponseEntity.ok(CustomResponse.success("Broadcast notification sent successfully"));
    }

    /**
     * 특정 부서/직급별 그룹 알림 발송
     *
     * @param request 알림 내용 및 대상 그룹
     * @return 발송 결과
     */
    @Operation(summary = "그룹 알림 발송", description = "특정 부서나 직급 그룹에게 알림을 발송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "발송 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    @PostMapping("/notifications/group")
    public ResponseEntity<CustomResponse<String>> sendGroupNotification(
            @RequestBody SettingsNotificationGroupRequestDTO request) {
        settingsNotificationCommandService.sendGroupNotification(request);
        return ResponseEntity.ok(CustomResponse.success("Group notification sent successfully"));
    }

    /**
     * 개별 직원 선택 알림 발송
     *
     * @param request 알림 내용 및 대상 직원 목록
     * @return 발송 결과
     */
    @Operation(summary = "개별 알림 발송", description = "선택한 개별 직원들에게 알림을 발송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "발송 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    @PostMapping("/notifications/individual")
    public ResponseEntity<CustomResponse<String>> sendIndividualNotification(
            @RequestBody SettingsNotificationIndividualRequestDTO request) {
        settingsNotificationCommandService.sendIndividualNotification(request);
        return ResponseEntity.ok(CustomResponse.success("Individual notification sent successfully"));
    }

    /**
     * 알림 발송 공통 로직 (Controller에서 직접 처리)
     */
    private int sendNotifications(List<Employee> employees, String title, String message, String type, String link) {
        int successCount = 0;
        for (Employee employee : employees) {
            try {
                NotificationRegistDTO registDTO = NotificationRegistDTO.builder()
                        .employeeId(employee.getEmployeeId())
                        .type(type)
                        .title(title)
                        .message(message)
                        .link(link)
                        .build();
                if (notificationCommandService.registAndSendNotification(registDTO) != null) {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("Failed to send notification to employee {}: {}", employee.getEmployeeId(), e.getMessage());
            }
        }
        return successCount;
    }

    /**
     * 알림 발송 이력 조회
     *
     * @param pageable  페이징 정보
     * @param startDate 조회 시작일 (optional)
     * @param endDate   조회 종료일 (optional)
     * @param type      알림 타입 (optional)
     * @return 발송 이력 목록
     */
    @Operation(summary = "알림 발송 이력 조회", description = "알림 발송 이력을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    @GetMapping("/notifications/history")
    public ResponseEntity<CustomResponse<PageResponse<SettingsNotificationHistoryResponseDTO>>> getNotificationHistory(
            Pageable pageable,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String type) {

        PageResponse<SettingsNotificationHistoryResponseDTO> history =
                settingsNotificationQueryService.getNotificationHistory(pageable, startDate, endDate, type);

        return ResponseEntity.ok(CustomResponse.success(history));
    }

    /**
     * 알림 발송 통계 조회
     *
     * @return 발송 통계 정보
     */
    @Operation(summary = "알림 발송 통계 조회", description = "알림 발송 통계 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    @GetMapping("/notifications/statistics")
    public ResponseEntity<CustomResponse<SettingsNotificationStatisticsResponseDTO>> getNotificationStatistics() {
        SettingsNotificationStatisticsResponseDTO statistics =
                settingsNotificationQueryService.getNotificationStatistics();

        return ResponseEntity.ok(CustomResponse.success(statistics));
    }

    /**
     * WebSocket 연결 상태 Health Check
     *
     * @return WebSocket 연결 상태 정보
     */
    @Operation(summary = "WebSocket 상태 확인", description = "WebSocket 연결 상태를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "확인 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    @GetMapping("/notifications/health")
    public ResponseEntity<CustomResponse<SettingsWebSocketHealthResponseDTO>> checkWebSocketHealth() {
        SettingsWebSocketHealthResponseDTO health =
                settingsNotificationQueryService.checkWebSocketHealth();

        return ResponseEntity.ok(CustomResponse.success(health));
    }

    /**
     * 근무제(Work System Template) 목록 조회
     *
     * @return 근무제 템플릿 목록
     */
    @Operation(summary = "근무제 템플릿 목록 조회", description = "등록된 근무제 템플릿 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    @GetMapping("/attendance/work-system-templates")
    public ResponseEntity<CustomResponse<List<SettingWorkSystemResponseDTO>>> getWorkSystemTemplates() {
        List<SettingWorkSystemResponseDTO> list = settingsAttendanceService.getWorkSystemTemplates();
        return ResponseEntity.ok(CustomResponse.success(list));
    }

    /**
     * 근무제(Work System Template) 저장(업서트)
     * - 신규: workSystemTemplateId = null  -> INSERT
     * - 수정: workSystemTemplateId != null -> UPDATE
     *
     * @param requestList 저장/수정할 근무제 템플릿 목록
     * @return 성공 메시지
     */
    @Operation(summary = "근무제 템플릿 저장/수정", description = "근무제 템플릿을 저장하거나 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "저장 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    @PutMapping("/attendance/work-system-templates")
    public ResponseEntity<CustomResponse<String>> upsertWorkSystemTemplates(
            @RequestBody List<SettingWorkSystemRequestDTO> requestList
    ) {
        settingsAttendanceService.upsertWorkSystemTemplates(requestList);
        return ResponseEntity.ok(CustomResponse.success("Work system templates upserted successfully"));
    }
}