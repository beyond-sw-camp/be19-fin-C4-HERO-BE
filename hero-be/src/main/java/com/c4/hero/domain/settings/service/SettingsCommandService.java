package com.c4.hero.domain.settings.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.domain.approval.entity.ApprovalTemplate;
import com.c4.hero.domain.approval.repository.ApprovalTemplateRepository;
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
import com.c4.hero.domain.employee.type.RoleType;
import com.c4.hero.domain.settings.dto.SettingsDefaultLineDTO;
import com.c4.hero.domain.settings.dto.SettingsDefaultRefDTO;
import com.c4.hero.domain.settings.dto.request.*;
import com.c4.hero.domain.settings.entity.SettingsApprovalLine;
import com.c4.hero.domain.settings.entity.SettingsApprovalRef;
import com.c4.hero.domain.settings.entity.SettingsDepartment;
import com.c4.hero.domain.settings.entity.SettingsLoginPolicy;
import com.c4.hero.domain.settings.repository.SettingsApprovalLineRepository;
import com.c4.hero.domain.settings.repository.SettingsApprovalRefRepository;
import com.c4.hero.domain.settings.repository.SettingsDepartmentRepository;
import com.c4.hero.domain.settings.repository.SettingsLoginPolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.c4.hero.domain.settings.enums.TargetType.*;

/**
 * <pre>
 * Class Name: SettingsCommandService
 * Description: 환경 설정 관련 명령(CUD) 서비스
 *
 * History
 * 2025/12/16 (승건) 최초 작성
 * 2025/12/19 (민철) 기본 결재선 / 참조 목록 설정적용
 * 2025/12/22 (혜원) 관리자 알림 발송 기능 추가
 * 2025/12/23 (혜원) 알림 관련 SettingsNotificationCommandService로 분리
 * </pre>
 *
 * @author 승건
 * @version 1.1
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SettingsCommandService {

    private final SettingsDepartmentRepository departmentRepository;
    private final SettingsLoginPolicyRepository loginPolicyRepository;

    private final EmployeeRepository employeeRepository;
    private final EmployeeGradeRepository gradeRepository;
    private final EmployeeJobTitleRepository jobTitleRepository;
    private final EmployeeAccountRepository accountRepository;
    private final EmployeeAccountRoleRepository accountRoleRepository;
    private final EmployeeRoleRepository roleRepository;
    private final ApprovalTemplateRepository templateRepository;
    private final SettingsApprovalLineRepository settingsApprovalLineRepository;
    private final SettingsApprovalRefRepository settingsApprovalRefRepository;

    private final EmployeeCommandService employeeCommandService;

    private static final int ADMIN_DEPARTMENT_ID = 0;
    private static final int TEMP_DEPARTMENT_ID = -1;
    private static final int ADMIN_ID = 0;

    /**
     * 부서 정보 트리 저장 및 수정
     *
     * @param departmentDtos 부서 정보 목록
     */
    public void updateDepartments(List<SettingsDepartmentRequestDTO> departmentDtos) {
        // 1. DEPT_MANAGER 역할 조회
        Role deptManagerRole = roleRepository.findByRole(RoleType.DEPT_MANAGER)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND, "DEPT_MANAGER 역할을 찾을 수 없습니다."));

        // 2. DB에 있는 모든 부서 ID를 가져와 Set에 저장 (단, 0번과 -1번은 제외)
        Set<Integer> existingDeptIds = departmentRepository.findAll().stream()
                .map(SettingsDepartment::getDepartmentId)
                .filter(id -> id != ADMIN_DEPARTMENT_ID && id != TEMP_DEPARTMENT_ID) // 0번과 -1번은 삭제 대상 후보에서 제외
                .collect(Collectors.toSet());

        // 3. 새로운 ID 생성을 위해 현재 MAX ID 조회
        Integer maxId = departmentRepository.findMaxDepartmentId();
        AtomicInteger newIdCounter = new AtomicInteger(maxId == null ? 0 : maxId);

        // 4. 재귀적으로 부서를 저장/업데이트하면서 Set에서 해당 ID를 제거
        for (SettingsDepartmentRequestDTO departmentDto : departmentDtos) {
            saveOrUpdateDepartment(departmentDto, null, 1, existingDeptIds, newIdCounter, deptManagerRole);
        }

        // 5. Set에 남아있는 ID는 요청에 포함되지 않은 부서이므로 삭제
        if (!existingDeptIds.isEmpty()) {
            List<SettingsDepartment> departmentsToDelete = departmentRepository.findAllById(existingDeptIds);

            // 5-1. 삭제될 부서의 부서장들에게서 DEPT_MANAGER 역할 회수
            departmentsToDelete.stream()
                    .map(SettingsDepartment::getManagerId)
                    .filter(Objects::nonNull)
                    .forEach(managerId -> updateDeptManagerRole(managerId, null, deptManagerRole));

            // 5-2. 삭제될 부서에 속한 직원들을 임시 부서(-1)로 이동
            List<Employee> employeesToUpdate = employeeRepository.findAllByEmployeeDepartment_DepartmentIdIn(List.copyOf(existingDeptIds));
            for (Employee employee : employeesToUpdate) {
                employeeCommandService.addDepartmentHistory(employee, ChangeType.UPDATE, "발령 대기 부서");
            }
            employeeRepository.updateDepartmentByDepartmentIds(TEMP_DEPARTMENT_ID, List.copyOf(existingDeptIds));

            // 5-3. 자식 부서부터 삭제하기 위해 depth 역순으로 정렬 후 삭제
            departmentsToDelete.sort(Comparator.comparingInt(SettingsDepartment::getDepth).reversed());
            departmentRepository.deleteAll(departmentsToDelete);
        }
    }

    /**
     * 개별 부서 정보 저장 또는 수정 (재귀 호출)
     *
     * @param dto             부서 요청 DTO
     * @param parentId        상위 부서 ID
     * @param currentDepth    현재 깊이
     * @param existingDeptIds 기존 부서 ID 집합
     * @param newIdCounter    새 ID 카운터
     * @param deptManagerRole 부서장 역할
     */
    private void saveOrUpdateDepartment(SettingsDepartmentRequestDTO dto, Integer parentId, int currentDepth, Set<Integer> existingDeptIds, AtomicInteger newIdCounter, Role deptManagerRole) {
        Integer departmentId = dto.getDepartmentId();

        // 0번, -1번 부서에 대한 수정 요청이 들어오면 무시
        if (departmentId != null && (departmentId == ADMIN_DEPARTMENT_ID || departmentId == TEMP_DEPARTMENT_ID)) {
            return;
        }

        SettingsDepartment originalDept = null;
        if (departmentId != null) {
            originalDept = departmentRepository.findById(departmentId).orElse(null);
            existingDeptIds.remove(departmentId);
        } else {
            departmentId = newIdCounter.incrementAndGet();
        }

        Integer oldManagerId = (originalDept != null) ? originalDept.getManagerId() : null;
        Integer newManagerId = dto.getManagerId();

        // 부서장 소속 검증
        if (newManagerId != null) {
            Employee manager = employeeRepository.findById(newManagerId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND, "부서장으로 지정된 사원을 찾을 수 없습니다."));
            if (!Objects.equals(manager.getEmployeeDepartment().getDepartmentId(), departmentId)) {
                throw new BusinessException(ErrorCode.MANAGER_NOT_IN_DEPARTMENT);
            }
        }

        SettingsDepartment department = SettingsDepartment.builder()
                .departmentId(departmentId)
                .departmentName(dto.getDepartmentName())
                .departmentPhone(dto.getDepartmentPhone())
                .depth(currentDepth)
                .parentDepartmentId(parentId)
                .managerId(newManagerId)
                .build();
        SettingsDepartment savedDepartment = departmentRepository.save(department);

        if (!Objects.equals(oldManagerId, newManagerId)) {
            updateDeptManagerRole(oldManagerId, newManagerId, deptManagerRole);
        }

        if (dto.getChildren() != null && !dto.getChildren().isEmpty()) {
            for (SettingsDepartmentRequestDTO childDto : dto.getChildren()) {
                saveOrUpdateDepartment(childDto, savedDepartment.getDepartmentId(), currentDepth + 1, existingDeptIds, newIdCounter, deptManagerRole);
            }
        }
    }

    /**
     * 부서장 역할 업데이트
     *
     * @param oldManagerId    이전 부서장 ID
     * @param newManagerId    새 부서장 ID
     * @param deptManagerRole 부서장 역할
     */
    private void updateDeptManagerRole(Integer oldManagerId, Integer newManagerId, Role deptManagerRole) {
        // 이전 부서장에게서 역할 제거
        if (oldManagerId != null) {
            accountRepository.findByEmployee_EmployeeId(oldManagerId).ifPresent(account -> {
                boolean isSystemAdmin = account.getAccountRoles().stream()
                        .anyMatch(ar -> ar.getRole().getRole() == RoleType.SYSTEM_ADMIN);
                if (!isSystemAdmin) {
                    List<AccountRole> rolesToRemove = account.getAccountRoles().stream()
                            .filter(ar -> ar.getRole().getRoleId().equals(deptManagerRole.getRoleId()))
                            .collect(Collectors.toList());
                    if (!rolesToRemove.isEmpty()) {
                        account.getAccountRoles().removeAll(rolesToRemove);
                        accountRoleRepository.deleteAll(rolesToRemove);
                        accountRoleRepository.flush();
                    }
                }
            });
        }

        // 새 부서장에게 역할 추가
        if (newManagerId != null) {
            accountRepository.findByEmployee_EmployeeId(newManagerId).ifPresent(account -> {
                boolean alreadyHasRole = account.getAccountRoles().stream()
                        .anyMatch(ar -> ar.getRole().getRoleId().equals(deptManagerRole.getRoleId()));
                if (!alreadyHasRole) {
                    AccountRole newDeptManagerRole = AccountRole.builder()
                            .account(account)
                            .role(deptManagerRole)
                            .build();
                    account.getAccountRoles().add(newDeptManagerRole);
                }
            });
        }
    }

    /**
     * 직급 정보 일괄 수정
     *
     * @param gradeDtos 직급 정보 목록
     */
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

    /**
     * 직책 정보 일괄 수정
     *
     * @param jobTitleDtos 직책 정보 목록
     */
    public void updateJobTitles(List<SettingsJobTitleRequestDTO> jobTitleDtos) {
        log.info("updateJobTitles: {}", jobTitleDtos);

        Set<Integer> existingJobTitleIds = jobTitleRepository.findAll().stream()
                .map(JobTitle::getJobTitleId)
                .filter(id -> id != ADMIN_ID)
                .collect(Collectors.toSet());

        log.info("existingJobTitleIds: {}", existingJobTitleIds);

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

    /**
     * 로그인 정책 설정
     *
     * @param policy 로그인 정책 요청 DTO
     */
    public void setLoginPolicy(SettingsLoginPolicyRequestDTO policy) {
        Integer value = policy.getValue();

        SettingsLoginPolicy loginPolicy = loginPolicyRepository.findById(1).orElse(null);

        if (loginPolicy != null) {
            loginPolicy.setValue(value);
            loginPolicyRepository.save(loginPolicy);
        } else {
            SettingsLoginPolicy newPolicy = SettingsLoginPolicy.builder()
                    .value(value)
                    .build();
            loginPolicyRepository.save(newPolicy);
        }
    }

    /**
     * 사원 권한 수정
     *
     * @param dto 권한 수정 요청 DTO
     */
    public void updatePermissions(SettingsPermissionsRequestDTO dto) {
        Account account = accountRepository.findByEmployee_EmployeeId(dto.getEmployeeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

        // 기존 권한에 SYSTEM_ADMIN이 있는지 확인
        boolean hasSystemAdmin = account.getAccountRoles().stream()
                .anyMatch(ar -> ar.getRole().getRole() == RoleType.SYSTEM_ADMIN);

        // 새로운 권한 목록에 SYSTEM_ADMIN이 있는지 확인
        List<Role> newRoles = roleRepository.findAllById(dto.getRoleIds());
        boolean requestedSystemAdmin = newRoles.stream()
                .anyMatch(r -> r.getRole() == RoleType.SYSTEM_ADMIN);

        // SYSTEM_ADMIN 권한 변경 시도 감지
        if (hasSystemAdmin != requestedSystemAdmin) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "시스템 관리자 권한은 변경할 수 없습니다.");
        }

        // 기존 권한을 모두 제거
        account.getAccountRoles().clear();

        // 새로운 권한 추가
        List<AccountRole> newAccountRoles = newRoles.stream()
                .map(role -> AccountRole.builder()
                        .account(account)
                        .role(role)
                        .build())
                .collect(Collectors.toList());

        account.getAccountRoles().addAll(newAccountRoles);
    }

    /**
     * 서식별 기본 결재선 / 참조목록 설정
     *
     * @param settings 결재선/참조목록
     * @return String 설정 저장 성공 / 실패 메시지
     */
    @Transactional
    public void applySettings(Integer templateId, SettingsApprovalRequestDTO settings) {

        ApprovalTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        settingsApprovalLineRepository.deleteAllByTemplate(template);
        settingsApprovalRefRepository.deleteAllByTemplate(template);

        List<SettingsApprovalLine> lines = settings.getLines().stream()
                .filter(this::isValidLineTarget)
                .map(lineDto -> SettingsApprovalLine.builder()
                        .seq(lineDto.getSeq())
                        .template(template)
                        .departmentId(lineDto.getDepartmentId())
                        .build())
                .collect(Collectors.toList());

        if (!lines.isEmpty()) {
            settingsApprovalLineRepository.saveAll(lines);
        }

        List<SettingsApprovalRef> refs = settings.getReferences().stream()
                .filter(this::isValidRefTarget)
                .map(refDto -> SettingsApprovalRef.builder()
                        .template(template)
                        .departmentId(refDto.getDepartmentId())
                        .build())
                .collect(Collectors.toList());

        if (!refs.isEmpty()) {
            settingsApprovalRefRepository.saveAll(refs);
        }

    }

    private boolean isValidLineTarget(SettingsDefaultLineDTO line) {
        String type = line.getTargetType();
        return SPECIFIC_DEPT.name().equals(type) || DRAFTER_DEPT.name().equals(type);
    }

    private boolean isValidRefTarget(SettingsDefaultRefDTO ref) {
        String type = ref.getTargetType();
        return SPECIFIC_DEPT.name().equals(type) || DRAFTER_DEPT.name().equals(type);
    }
}