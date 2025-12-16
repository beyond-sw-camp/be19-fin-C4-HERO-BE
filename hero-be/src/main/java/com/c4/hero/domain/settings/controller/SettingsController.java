package com.c4.hero.domain.settings.controller;

import com.c4.hero.common.response.ApiResponse;
import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.employee.entity.Grade;
import com.c4.hero.domain.employee.entity.JobTitle;
import com.c4.hero.domain.employee.entity.Role;
import com.c4.hero.domain.settings.dto.request.SettingsDepartmentRequestDTO;
import com.c4.hero.domain.settings.dto.request.SettingsGradeRequestDTO;
import com.c4.hero.domain.settings.dto.request.SettingsJobTitleRequestDTO;
import com.c4.hero.domain.settings.dto.request.SettingsLoginPolicyRequestDTO;
import com.c4.hero.domain.settings.dto.request.SettingsPermissionsRequestDTO;
import com.c4.hero.domain.settings.dto.response.SettingsDepartmentResponseDTO;
import com.c4.hero.domain.settings.dto.response.SettingsPermissionsResponseDTO;
import com.c4.hero.domain.settings.service.SettingsCommandService;
import com.c4.hero.domain.settings.service.SettingsQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@Slf4j
public class SettingsController {

    private final SettingsCommandService settingsCommandService;
    private final SettingsQueryService settingsQueryService;

    /**
     * 부서 목록 조회 (트리 구조)
     */
    @GetMapping("/departments")
    public ResponseEntity<ApiResponse<List<SettingsDepartmentResponseDTO>>> getDepartments() {
        List<SettingsDepartmentResponseDTO> departmentTree = settingsQueryService.getDepartmentTree();
        log.info("department: {}", departmentTree);
        return ResponseEntity.ok(ApiResponse.success(departmentTree));
    }

    /**
     * 부서 정보 트리로 한번에 저장/수정
     */
    @PostMapping("/departments/tree")
    public ResponseEntity<ApiResponse<String>> saveOrUpdateDepartments(@RequestBody List<SettingsDepartmentRequestDTO> departments) {
        settingsCommandService.updateDepartments(departments);
        return ResponseEntity.ok(ApiResponse.success("Departments updated successfully"));
    }

    /**
     * 직급 목록 조회
     */
    @GetMapping("/grades")
    public ResponseEntity<ApiResponse<List<Grade>>> getGrades() {
        List<Grade> grades = settingsQueryService.getAllGrades();
        log.info("grades: {}", grades);

        return ResponseEntity.ok(ApiResponse.success(grades));
    }

    /**
     * 직급 정보 한번에 저장/수정/삭제
     */
    @PostMapping("/grades/batch")
    public ResponseEntity<ApiResponse<String>> updateGrades(@RequestBody List<SettingsGradeRequestDTO> grades) {
        settingsCommandService.updateGrades(grades);
        return ResponseEntity.ok(ApiResponse.success("Grades updated successfully"));
    }

    /**
     * 직책 목록 조회
     */
    @GetMapping("/job-titles")
    public ResponseEntity<ApiResponse<List<JobTitle>>> getJobTitles() {
        List<JobTitle> jobTitles = settingsQueryService.getAllJobTitles();
        log.info("jobTitles: {}", jobTitles);

        return ResponseEntity.ok(ApiResponse.success(jobTitles));
    }

    /**
     * 직책 정보 한번에 저장/수정/삭제
     */
    @PostMapping("/job-titles/batch")
    public ResponseEntity<ApiResponse<String>> updateJobTitles(@RequestBody List<SettingsJobTitleRequestDTO> jobTitles) {
        settingsCommandService.updateJobTitles(jobTitles);
        return ResponseEntity.ok(ApiResponse.success("Job titles updated successfully"));
    }

    @GetMapping("/login-policy")
    public ResponseEntity<ApiResponse<Integer>> getLoginPolicy() {
        Integer loginPolicy = settingsQueryService.getLoginPolicy();
        return ResponseEntity.ok(ApiResponse.success(loginPolicy));
    }

    /**
     * 로그인 정책 설정
     */
    @PutMapping("/login-policy")
    public ResponseEntity<ApiResponse<String>> setLoginPolicy(@RequestBody SettingsLoginPolicyRequestDTO policy) {
        settingsCommandService.setLoginPolicy(policy);
        return ResponseEntity.ok(ApiResponse.success("Login policy updated successfully"));
    }


    /**
     * 퇴사자 정보 삭제 시기 설정
     */
//    @GetMapping("/retiree-policy")
//    public ResponseEntity<ApiResponse<>> getRetireePolicy() {
//
//    }

    /**
     * 퇴사자 정보 삭제 시기 설정
     */
//    @PostMapping("/retiree-policy")
//    public ResponseEntity<String> setRetireePolicy(@RequestBody Map<String, Object> policy) {
//        settingCommandService.setRetireePolicy(policy);
//        return ResponseEntity.ok("Retiree policy updated successfully");
//    }

    /**
     * 사원 권한 조회
     *
     * @return: 각 사원들이 들고 있는 권한 정보 List
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
     */
    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<Role>>> getRoles() {
        List<Role> roles = settingsQueryService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.success(roles));
    }

    /**
     * 권한 설정
     */
    @PutMapping("/permissions")
    public ResponseEntity<ApiResponse<String>> updatePermissions(@RequestBody SettingsPermissionsRequestDTO dto) {
        settingsCommandService.updatePermissions(dto);
        return ResponseEntity.ok(ApiResponse.success("Permissions updated successfully"));
    }
}
