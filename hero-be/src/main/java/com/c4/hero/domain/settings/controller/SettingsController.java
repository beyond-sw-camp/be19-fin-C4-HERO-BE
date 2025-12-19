package com.c4.hero.domain.settings.controller;

import com.c4.hero.common.response.ApiResponse;
import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.employee.entity.Grade;
import com.c4.hero.domain.employee.entity.JobTitle;
import com.c4.hero.domain.employee.entity.Role;
import com.c4.hero.domain.settings.dto.request.*;
import com.c4.hero.domain.settings.dto.request.SettingsApprovalRequestDTO;
import com.c4.hero.domain.settings.dto.response.SettingsDepartmentResponseDTO;
import com.c4.hero.domain.settings.dto.response.SettingsDocumentTemplateResponseDTO;
import com.c4.hero.domain.settings.dto.response.SettingsPermissionsResponseDTO;
import com.c4.hero.domain.settings.service.SettingsCommandService;
import com.c4.hero.domain.settings.service.SettingsQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
 * 2025/12/16 승건 최초 작성
 * 2025/12/18 민철 - 결재선 설정을 위한 컨트롤러 메서드 작성
 * </pre>
 *
 * @author 승건
 * @version 1.1
 */
@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@Slf4j
public class SettingsController {

	private final SettingsCommandService settingsCommandService;
	private final SettingsQueryService settingsQueryService;

	/**
	 * 부서 목록 조회 (트리 구조)
	 *
	 * @return 부서 트리 구조 목록
	 */
	@GetMapping("/departments")
	public ResponseEntity<ApiResponse<List<SettingsDepartmentResponseDTO>>> getDepartments() {
		List<SettingsDepartmentResponseDTO> departmentTree = settingsQueryService.getDepartmentTree();

		log.info("department: {}", departmentTree);
		return ResponseEntity.ok(ApiResponse.success(departmentTree));
	}

	/**
	 * 부서 정보 트리로 한번에 저장/수정
	 *
	 * @param departments 저장 또는 수정할 부서 정보 목록
	 * @return 성공 메시지
	 */
	@PostMapping("/departments/tree")
	public ResponseEntity<ApiResponse<String>> saveOrUpdateDepartments(@RequestBody List<SettingsDepartmentRequestDTO> departments) {
		settingsCommandService.updateDepartments(departments);
		return ResponseEntity.ok(ApiResponse.success("Departments updated successfully"));
	}

	/**
	 * 직급 목록 조회
	 *
	 * @return 전체 직급 목록
	 */
	@GetMapping("/grades")
	public ResponseEntity<ApiResponse<List<Grade>>> getGrades() {
		List<Grade> grades = settingsQueryService.getAllGrades();

		log.info("grades: {}", grades);
		return ResponseEntity.ok(ApiResponse.success(grades));
	}

	/**
	 * 직급 정보 한번에 저장/수정/삭제
	 *
	 * @param grades 수정할 직급 정보 목록
	 * @return 성공 메시지
	 */
	@PostMapping("/grades/batch")
	public ResponseEntity<ApiResponse<String>> updateGrades(@RequestBody List<SettingsGradeRequestDTO> grades) {
		settingsCommandService.updateGrades(grades);
		return ResponseEntity.ok(ApiResponse.success("Grades updated successfully"));
	}

	/**
	 * 직책 목록 조회
	 *
	 * @return 전체 직책 목록
	 */
	@GetMapping("/job-titles")
	public ResponseEntity<ApiResponse<List<JobTitle>>> getJobTitles() {
		List<JobTitle> jobTitles = settingsQueryService.getAllJobTitles();

		log.info("jobTitles: {}", jobTitles);
		return ResponseEntity.ok(ApiResponse.success(jobTitles));
	}

	/**
	 * 직책 정보 한번에 저장/수정/삭제
	 *
	 * @param jobTitles 수정할 직책 정보 목록
	 * @return 성공 메시지
	 */
	@PostMapping("/job-titles/batch")
	public ResponseEntity<ApiResponse<String>> updateJobTitles(@RequestBody List<SettingsJobTitleRequestDTO> jobTitles) {
		settingsCommandService.updateJobTitles(jobTitles);
		return ResponseEntity.ok(ApiResponse.success("Job titles updated successfully"));
	}

	/**
	 * 로그인 정책 조회
	 *
	 * @return 로그인 정책 값
	 */
	@GetMapping("/login-policy")
	public ResponseEntity<ApiResponse<Integer>> getLoginPolicy() {
		Integer loginPolicy = settingsQueryService.getLoginPolicy();
		return ResponseEntity.ok(ApiResponse.success(loginPolicy));
	}

	/**
	 * 로그인 정책 설정
	 *
	 * @param policy 설정할 로그인 정책
	 * @return 성공 메시지
	 */
	@PutMapping("/login-policy")
	public ResponseEntity<ApiResponse<String>> setLoginPolicy(@RequestBody SettingsLoginPolicyRequestDTO policy) {
		settingsCommandService.setLoginPolicy(policy);
		return ResponseEntity.ok(ApiResponse.success("Login policy updated successfully"));
	}

	/**
	 * 사원 권한 조회
	 *
	 * @param pageable 페이징 정보
	 * @param query    검색어
	 * @return 각 사원들이 들고 있는 권한 정보 List
	 */
	@GetMapping("/permissions")
	public ResponseEntity<ApiResponse<PageResponse<SettingsPermissionsResponseDTO>>> getPermissions(
			Pageable pageable,
			@RequestParam(required = false) String query) {
		PageResponse<SettingsPermissionsResponseDTO> permissions = settingsQueryService.getEmployeePermissions(pageable, query);
		return ResponseEntity.ok(ApiResponse.success(permissions));
	}

	/**
	 * 모든 권한 목록 조회
	 *
	 * @return 전체 권한 목록
	 */
	@GetMapping("/roles")
	public ResponseEntity<ApiResponse<List<Role>>> getRoles() {
		List<Role> roles = settingsQueryService.getAllRoles();
		return ResponseEntity.ok(ApiResponse.success(roles));
	}

	/**
	 * 권한 설정
	 *
	 * @param dto 권한 설정 요청 정보
	 * @return 성공 메시지
	 */
	@PutMapping("/permissions")
	public ResponseEntity<ApiResponse<String>> updatePermissions(@RequestBody SettingsPermissionsRequestDTO dto) {
		settingsCommandService.updatePermissions(dto);
		return ResponseEntity.ok(ApiResponse.success("Permissions updated successfully"));
	}

    /**
     * 서식별 기본 결재선 설정
     *
     * @param   settings 설정값들
     * @return ResponseEntity<>
     */
    @PostMapping("/approvals/{templateId}")
    public ResponseEntity<String> registDefaultLine(
            @PathVariable Integer templateId,
            @RequestBody SettingsApprovalRequestDTO settings){

        String response = settingsCommandService.applySettings(templateId, settings);
        return ResponseEntity.ok().body(response);
    }

    /**
     * 서식목록 조회
     *
     * @param
     * @return List<SettingsDocumentTemplateResponseDTO> 서식 목록 조회
     */
    @GetMapping("/approvals/templates")
    public ResponseEntity<List<SettingsDocumentTemplateResponseDTO>> getTemplates() {

        List<SettingsDocumentTemplateResponseDTO> lists = settingsQueryService.getTemplates();
        return ResponseEntity.ok().body(lists);
    }
}
