package com.c4.hero.domain.settings.service;

import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.approval.repository.ApprovalTemplateRepository;
import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.entity.Grade;
import com.c4.hero.domain.employee.entity.JobTitle;
import com.c4.hero.domain.employee.entity.Role;
import com.c4.hero.domain.employee.repository.EmployeeGradeRepository;
import com.c4.hero.domain.employee.repository.EmployeeJobTitleRepository;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import com.c4.hero.domain.employee.repository.EmployeeRoleRepository;
import com.c4.hero.domain.settings.dto.SettingsDefaultLineDTO;
import com.c4.hero.domain.settings.dto.SettingsDefaultRefDTO;
import com.c4.hero.domain.settings.dto.response.DepartmentResponseDTO;
import com.c4.hero.domain.settings.dto.response.SettingsApprovalResponseDTO;
import com.c4.hero.domain.settings.dto.response.SettingsDepartmentManagerDTO;
import com.c4.hero.domain.settings.dto.response.SettingsDepartmentResponseDTO;
import com.c4.hero.domain.settings.dto.response.SettingsDocumentTemplateResponseDTO;
import com.c4.hero.domain.settings.dto.response.SettingsPermissionsResponseDTO;
import com.c4.hero.domain.settings.entity.SettingsApprovalLine;
import com.c4.hero.domain.settings.entity.SettingsApprovalRef;
import com.c4.hero.domain.settings.entity.SettingsDepartment;
import com.c4.hero.domain.settings.mapper.SettingsMapper;
import com.c4.hero.domain.settings.repository.SettingsApprovalLineRepository;
import com.c4.hero.domain.settings.repository.SettingsApprovalRefRepository;
import com.c4.hero.domain.settings.repository.SettingsDepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <pre>
 * Class Name: SettingsQueryService
 * Description: 환경 설정 관련 조회(Read) 서비스
 *
 * History
 * 2025/12/16 (승건) 최초 작성
 * 2025/12/19 (민철) 설정 페이지 내 서식목록 조회 api
 * 2025/12/21 (민철) 서식별 기본 설정 조회 api
 * 2025/12/22 (혜원) 알림 관련 조회 기능 추가
 * 2025/12/23 (혜원) 알림 관련 SettingsNotificationQueryService로 분리
 * </pre>
 *
 * @author 승건
 * @version 2.0
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SettingsQueryService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeGradeRepository gradeRepository;
    private final EmployeeJobTitleRepository jobTitleRepository;
    private final EmployeeRoleRepository roleRepository;
    private final SettingsApprovalLineRepository settingsApprovalLineRepository;
    private final SettingsApprovalRefRepository settingsApprovalRefRepository;
    private final ApprovalTemplateRepository approvalTemplateRepository;
    private final SettingsDepartmentRepository departmentRepository;

    private static final int ADMIN_DEPARTMENT_ID = 0;
    private static final int TEMP_DEPARTMENT_ID = -1;

    private final SettingsMapper settingsMapper;

    /**
     * 부서 트리 구조 조회
     *
     * @return 부서 트리 목록
     */
    public List<SettingsDepartmentResponseDTO> getDepartmentTree() {
        // 1. 0번과 -1번 부서를 제외한 모든 부서 조회
        List<Integer> excludedIds = List.of(ADMIN_DEPARTMENT_ID, TEMP_DEPARTMENT_ID);
        List<SettingsDepartment> flatList = departmentRepository.findAllByDepartmentIdNotIn(excludedIds);

        // 2. 모든 매니저 ID 수집 (중복 제거, null 제외)
        List<Integer> managerIds = flatList.stream()
                .map(SettingsDepartment::getManagerId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // 3. 매니저 정보 한 번에 조회
        Map<Integer, Employee> managersMap = employeeRepository.findAllById(managerIds).stream()
                .collect(Collectors.toMap(Employee::getEmployeeId, Function.identity()));

        // 4. 엔티티 리스트를 DTO 리스트로 변환하면서 매니저 정보 주입
        List<SettingsDepartmentResponseDTO> dtoList = flatList.stream()
                .map(department -> convertToDto(department, managersMap.get(department.getManagerId())))
                .collect(Collectors.toList());

        // 5. DTO 리스트를 트리 구조로 변환
        return buildTree(dtoList);
    }

    /**
     * 부서 엔티티를 DTO로 변환
     *
     * @param entity  부서 엔티티
     * @param manager 부서장 사원 정보
     * @return 부서 응답 DTO
     */
    private SettingsDepartmentResponseDTO convertToDto(SettingsDepartment entity, Employee manager) {
        SettingsDepartmentManagerDTO managerDTO = null;
        if (manager != null) {
            managerDTO = SettingsDepartmentManagerDTO.builder()
                    .employeeId(manager.getEmployeeId())
                    .employeeNumber(manager.getEmployeeNumber())
                    .employeeName(manager.getEmployeeName())
                    .jobTitle(manager.getJobTitle() != null ? manager.getJobTitle().getJobTitle() : null)
                    .grade(manager.getGrade() != null ? manager.getGrade().getGrade() : null)
                    .build();
        }

        return SettingsDepartmentResponseDTO.builder()
                .departmentId(entity.getDepartmentId())
                .departmentName(entity.getDepartmentName())
                .departmentPhone(entity.getDepartmentPhone())
                .depth(entity.getDepth())
                .parentDepartmentId(entity.getParentDepartmentId())
                .manager(managerDTO)
                .children(new ArrayList<>()) // children 리스트 초기화
                .build();
    }

    /**
     * 평면 리스트를 트리 구조로 변환
     *
     * @param flatList 평면 부서 목록
     * @return 트리 구조 부서 목록
     */
    private List<SettingsDepartmentResponseDTO> buildTree(List<SettingsDepartmentResponseDTO> flatList) {
        Map<Integer, SettingsDepartmentResponseDTO> map = new HashMap<>();
        for (SettingsDepartmentResponseDTO dto : flatList) {
            map.put(dto.getDepartmentId(), dto);
        }

        List<SettingsDepartmentResponseDTO> tree = new ArrayList<>();
        for (SettingsDepartmentResponseDTO dto : flatList) {
            if (dto.getParentDepartmentId() != null) {
                SettingsDepartmentResponseDTO parent = map.get(dto.getParentDepartmentId());
                if (parent != null) {
                    parent.getChildren().add(dto);
                }
            } else {
                // 최상위 부서
                tree.add(dto);
            }
        }
        return tree;
    }

    /**
     * 전체 직급 목록 조회
     *
     * @return 직급 목록
     */
    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    /**
     * 전체 직책 목록 조회
     *
     * @return 직책 목록
     */
    public List<JobTitle> getAllJobTitles() {
        return jobTitleRepository.findAll();
    }

    /**
     * 전체 권한 목록 조회
     *
     * @return 권한 목록
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * 로그인 정책 조회
     *
     * @return 로그인 정책 값
     */
    public Integer getLoginPolicy() {
        return settingsMapper.selectPolicy();
    }

    /**
     * 사원 권한 목록 조회 (페이징)
     *
     * @param pageable 페이징 정보
     * @param query    검색어
     * @return 사원 권한 목록 페이지 응답
     */
    public PageResponse<SettingsPermissionsResponseDTO> getEmployeePermissions(Pageable pageable, String query) {
        Map<String, Object> params = new HashMap<>();
        params.put("query", query);

        List<SettingsPermissionsResponseDTO> content = settingsMapper.findEmployeePermissions(params, pageable);
        int total = settingsMapper.countEmployeePermissions(params);

        return PageResponse.of(content, pageable.getPageNumber(), pageable.getPageSize(), total);
    }

    /**
     * 문서 서식 목록
     *
     * @return List<SettingsDocumentTemplateResponseDTO>
     */
    public List<SettingsDocumentTemplateResponseDTO> getTemplates() {

        List<SettingsDocumentTemplateResponseDTO> list =
                approvalTemplateRepository.findByTemplateWithStepsCount();

        return list;
    }

    /**
     * 서식별 기본 결재선/참조목록 조회
     *
     * @param templateId 서식ID
     * @return response 기본 서식
     */
    public SettingsApprovalResponseDTO getDocSettings(Integer templateId) {

        List<SettingsApprovalLine> defLines = settingsApprovalLineRepository.findByTemplate_TemplateId(templateId);

        List<SettingsApprovalRef> defRefs = settingsApprovalRefRepository.findByTemplate_TemplateId(templateId);

        List<SettingsDefaultLineDTO> lineDTOs = defLines.stream().map(
                defLine -> SettingsDefaultLineDTO.builder()
                        .seq(defLine.getSeq())
                        .departmentId(defLine.getDepartmentId())
                        .targetType(defLine.getDepartmentId() != 0 ? "SPECIFIC_DEPT" : "DRAFTER_DEPT")
                        .build()).collect(Collectors.toList());

        List<SettingsDefaultRefDTO> refDTOs = defRefs.stream().map(
                defRef -> SettingsDefaultRefDTO.builder()
                        .departmentId(defRef.getDepartmentId())
                        .targetType(defRef.getDepartmentId() != 0 ? "SPECIFIC_DEPT" : "DRAFTER_DEPT")
                        .build()).collect(Collectors.toList());

        SettingsApprovalResponseDTO response = SettingsApprovalResponseDTO.builder()
                .lines(lineDTOs)
                .references(refDTOs)
                .build();

        return response;
    }
    /**
     * 부서목록 조회
     *
     * @return list 부서목록
     */
    public List<DepartmentResponseDTO> getApprovalDepartments() {
        List<SettingsDepartment> departments = departmentRepository.findByDepartmentIdGreaterThan(0);
        List<DepartmentResponseDTO> departmentListDTOs = departments.stream().map(
                department -> DepartmentResponseDTO.builder()
                        .departmentId(department.getDepartmentId())
                        .departmentName(department.getDepartmentName())
                        .build()).collect(Collectors.toList());
        return departmentListDTOs;
    }
}