package com.c4.hero.domain.settings.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.domain.employee.entity.Account;
import com.c4.hero.domain.employee.entity.AccountRole;
import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.entity.Grade;
import com.c4.hero.domain.employee.entity.JobTitle;
import com.c4.hero.domain.employee.entity.Role;
import com.c4.hero.domain.employee.repository.EmployeeAccountRepository;
import com.c4.hero.domain.employee.repository.EmployeeAccountRoleRepository;
import com.c4.hero.domain.employee.repository.EmployeeGradeRepository;
import com.c4.hero.domain.employee.repository.EmployeeJobTitleRepository;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import com.c4.hero.domain.employee.repository.EmployeeRoleRepository;
import com.c4.hero.domain.employee.service.EmployeeCommandService;
import com.c4.hero.domain.employee.type.ChangeType;
import com.c4.hero.domain.settings.dto.request.SettingsDepartmentRequestDTO;
import com.c4.hero.domain.settings.dto.request.SettingsGradeRequestDTO;
import com.c4.hero.domain.settings.dto.request.SettingsJobTitleRequestDTO;
import com.c4.hero.domain.settings.dto.request.SettingsLoginPolicyRequestDTO;
import com.c4.hero.domain.settings.dto.request.SettingsPermissionsRequestDTO;
import com.c4.hero.domain.settings.entity.SettingsDepartment;
import com.c4.hero.domain.settings.entity.SettingsLoginPolicy;
import com.c4.hero.domain.settings.repository.SettingsDepartmentRepository;
import com.c4.hero.domain.settings.repository.SettingsLoginPolicesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SettingsCommandService {

    private final SettingsDepartmentRepository departmentRepository;
    private final SettingsLoginPolicesRepository loginPolicesRepository;

    private final EmployeeRepository employeeRepository;
    private final EmployeeGradeRepository gradeRepository;
    private final EmployeeJobTitleRepository jobTitleRepository;
    private final EmployeeAccountRepository accountRepository;
    private final EmployeeAccountRoleRepository accountRoleRepository;
    private final EmployeeRoleRepository roleRepository;

    private final EmployeeCommandService employeeCommandService;

    private static final int ADMIN_DEPARTMENT_ID = 0;
    private static final int TEMP_DEPARTMENT_ID = -1;
    private static final int ADMIN_ID = 0;

    public void updateDepartments(List<SettingsDepartmentRequestDTO> departmentDtos) {
        // 1. DB에 있는 모든 부서 ID를 가져와 Set에 저장 (단, 0번과 -1번은 제외)
        Set<Integer> existingDeptIds = departmentRepository.findAll().stream()
                .map(SettingsDepartment::getDepartmentId)
                .filter(id -> id != ADMIN_DEPARTMENT_ID && id != TEMP_DEPARTMENT_ID) // 0번과 -1번은 삭제 대상 후보에서 제외
                .collect(Collectors.toSet());

        // 2. 새로운 ID 생성을 위해 현재 MAX ID 조회
        Integer maxId = departmentRepository.findMaxDepartmentId();
        AtomicInteger newIdCounter = new AtomicInteger(maxId == null ? 0 : maxId);

        // 3. 재귀적으로 부서를 저장/업데이트하면서 Set에서 해당 ID를 제거
        // 최상위 부서이므로 depth는 1부터 시작
        for (SettingsDepartmentRequestDTO departmentDto : departmentDtos) {
            saveOrUpdateDepartment(departmentDto, null, 1, existingDeptIds, newIdCounter);
        }

        // 4. Set에 남아있는 ID는 요청에 포함되지 않은 부서이므로 삭제
        // 삭제 시 외래 키 제약 조건(자식 부서 참조)을 피하기 위해 depth 역순(자식 -> 부모)으로 삭제
        if (!existingDeptIds.isEmpty()) {
            // 4-1. 삭제될 부서에 속한 사원들을 조회
            List<Employee> employeesToUpdate = employeeRepository.findAllByEmployeeDepartment_DepartmentIdIn(List.copyOf(existingDeptIds));

            // 4-2. 해당 사원들의 부서 변경 이력을 기록
            for (Employee employee : employeesToUpdate) {
                employeeCommandService.addDepartmentHistory(employee, ChangeType.UPDATE, "발령 대기 부서");
            }

            // 4-3. 해당 사원들의 부서를 임시 부서(-1)로 일괄 변경
            employeeRepository.updateDepartmentByDepartmentIds(TEMP_DEPARTMENT_ID, List.copyOf(existingDeptIds));

            // 4-4. 자식 부서부터 삭제하기 위해 depth 역순으로 정렬 후 삭제
            List<SettingsDepartment> departmentsToDelete = departmentRepository.findAllById(existingDeptIds);
            departmentsToDelete.sort(Comparator.comparingInt(SettingsDepartment::getDepth).reversed());
            departmentRepository.deleteAll(departmentsToDelete);
        }
    }

    private void saveOrUpdateDepartment(SettingsDepartmentRequestDTO dto, Integer parentId, int currentDepth, Set<Integer> existingDeptIds, AtomicInteger newIdCounter) {
        Integer departmentId = dto.getDepartmentId();

        // 0번, -1번 부서에 대한 수정 요청이 들어오면 무시
        if (departmentId != null && (departmentId == ADMIN_DEPARTMENT_ID || departmentId == TEMP_DEPARTMENT_ID)) {
            return;
        }

        if (departmentId != null) {
            existingDeptIds.remove(departmentId);
        } else {
            departmentId = newIdCounter.incrementAndGet();
        }

        SettingsDepartment department = SettingsDepartment.builder()
                .departmentId(departmentId)
                .departmentName(dto.getDepartmentName())
                .departmentPhone(dto.getDepartmentPhone())
                .depth(currentDepth) // 서버에서 계산된 depth 사용
                .parentDepartmentId(parentId)
                .managerId(dto.getManagerId())
                .build();

        SettingsDepartment savedDepartment = departmentRepository.save(department);

        if (dto.getChildren() != null && !dto.getChildren().isEmpty()) {
            for (SettingsDepartmentRequestDTO childDto : dto.getChildren()) {
                // 자식 부서는 현재 depth + 1
                saveOrUpdateDepartment(childDto, savedDepartment.getDepartmentId(), currentDepth + 1, existingDeptIds, newIdCounter);
            }
        }
    }

    public void updateGrades(List<SettingsGradeRequestDTO> gradeDtos) {
        Set<Integer> existingGradeIds = gradeRepository.findAll().stream()
                .map(Grade::getGradeId)
                .filter(id -> id != ADMIN_ID)
                .collect(Collectors.toSet());

        Integer maxId = gradeRepository.findMaxGradeId();
        AtomicInteger newIdCounter = new AtomicInteger(maxId == null ? 0 : maxId);

        for (SettingsGradeRequestDTO dto : gradeDtos) {
            Integer gradeId = dto.getGradeId();

            if (gradeId != null && gradeId == ADMIN_ID) {
                throw new BusinessException(ErrorCode.CANNOT_MODIFY_ADMIN_DATA);
            }

            if (gradeId == null || gradeId == 0) {
                gradeId = newIdCounter.incrementAndGet();
            }

            Grade grade = gradeRepository.findById(gradeId)
                    .orElse(new Grade());
            grade.setGradeId(gradeId);
            grade.setGrade(dto.getGradeName());
            grade.setRequiredPoint(dto.getRequiredPoint() != null ? dto.getRequiredPoint() : 0);
            gradeRepository.save(grade);
            existingGradeIds.remove(gradeId);
        }

        if (!existingGradeIds.isEmpty()) {
            List<Employee> employeesToUpdate = employeeRepository.findAllByGrade_GradeIdIn(List.copyOf(existingGradeIds));
            for (Employee employee : employeesToUpdate) {
                employeeCommandService.addGradeHistory(employee, ChangeType.UPDATE, "미지정");
            }
            employeeRepository.updateGradeByGradeIds(List.copyOf(existingGradeIds));
            gradeRepository.deleteAllById(existingGradeIds);
        }
    }

    public void updateJobTitles(List<SettingsJobTitleRequestDTO> jobTitleDtos) {
        Set<Integer> existingJobTitleIds = jobTitleRepository.findAll().stream()
                .map(JobTitle::getJobTitleId)
                .filter(id -> id != ADMIN_ID)
                .collect(Collectors.toSet());

        Integer maxId = jobTitleRepository.findMaxJobTitleId();
        AtomicInteger newIdCounter = new AtomicInteger(maxId == null ? 0 : maxId);

        for (SettingsJobTitleRequestDTO dto : jobTitleDtos) {
            Integer jobTitleId = dto.getJobTitleId();

            if (jobTitleId != null && jobTitleId == ADMIN_ID) {
                throw new BusinessException(ErrorCode.CANNOT_MODIFY_ADMIN_DATA);
            }

            if (jobTitleId == null || jobTitleId == 0) {
                jobTitleId = newIdCounter.incrementAndGet();
            }

            JobTitle jobTitle = jobTitleRepository.findById(jobTitleId)
                    .orElse(new JobTitle());
            jobTitle.setJobTitleId(jobTitleId);
            jobTitle.setJobTitle(dto.getJobTitleName());
            jobTitleRepository.save(jobTitle);
            existingJobTitleIds.remove(jobTitleId);
        }

        if (!existingJobTitleIds.isEmpty()) {
            jobTitleRepository.deleteAllById(existingJobTitleIds);
        }
    }

    public void setLoginPolicy(SettingsLoginPolicyRequestDTO policy) {
        Integer value = policy.getValue();

        SettingsLoginPolicy loginPolicy = loginPolicesRepository.findById(1).orElse(null);

        if (loginPolicy != null) {
            loginPolicy.setValue(value);
            loginPolicesRepository.save(loginPolicy);
        } else {
            SettingsLoginPolicy newPolicy = SettingsLoginPolicy.builder()
                    .value(value)
                    .build();
            loginPolicesRepository.save(newPolicy);
        }
    }

    public void updatePermissions(SettingsPermissionsRequestDTO dto) {
        Account account = accountRepository.findByEmployee_EmployeeId(dto.getEmployeeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

        accountRoleRepository.deleteAllByAccount(account);

        List<AccountRole> newRoles = dto.getRoleIds().stream()
                .map(roleId -> {
                    Role role = roleRepository.findById(roleId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
                    return AccountRole.builder()
                            .account(account)
                            .role(role)
                            .build();
                })
                .collect(Collectors.toList());

        accountRoleRepository.saveAll(newRoles);
    }
}
