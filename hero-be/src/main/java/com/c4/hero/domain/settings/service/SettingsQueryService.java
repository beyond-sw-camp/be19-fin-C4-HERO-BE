package com.c4.hero.domain.settings.service;

import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.entity.Grade;
import com.c4.hero.domain.employee.entity.JobTitle;
import com.c4.hero.domain.employee.entity.Role;
import com.c4.hero.domain.employee.repository.EmployeeGradeRepository;
import com.c4.hero.domain.employee.repository.EmployeeJobTitleRepository;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import com.c4.hero.domain.employee.repository.EmployeeRoleRepository;
import com.c4.hero.domain.settings.dto.response.SettingsDepartmentManagerDTO;
import com.c4.hero.domain.settings.dto.response.SettingsDepartmentResponseDTO;
import com.c4.hero.domain.settings.dto.response.SettingsPermissionsResponseDTO;
import com.c4.hero.domain.settings.entity.SettingsDepartment;
import com.c4.hero.domain.settings.mapper.SettingsMapper;
import com.c4.hero.domain.settings.repository.SettingsDepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SettingsQueryService {

    private final SettingsDepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeGradeRepository gradeRepository;
    private final EmployeeJobTitleRepository jobTitleRepository;
    private final EmployeeRoleRepository roleRepository;

    private static final int ADMIN_DEPARTMENT_ID = 0;
    private static final int TEMP_DEPARTMENT_ID = -1;

    private final SettingsMapper settingsMapper;

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


    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    public List<JobTitle> getAllJobTitles() {
        return jobTitleRepository.findAll();
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Integer getLoginPolicy() {
        return settingsMapper.selectPolicy();
    }

    public PageResponse<SettingsPermissionsResponseDTO> getEmployeePermissions(Pageable pageable, String query) {
        Map<String, Object> params = new HashMap<>();
        params.put("query", query);

        List<SettingsPermissionsResponseDTO> content = settingsMapper.findEmployeePermissions(params, pageable);
        int total = settingsMapper.countEmployeePermissions(params);

        return PageResponse.of(content, pageable.getPageNumber(), pageable.getPageSize(), total);
    }
}
