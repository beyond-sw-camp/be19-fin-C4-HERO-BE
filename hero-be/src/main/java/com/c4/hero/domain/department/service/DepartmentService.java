package com.c4.hero.domain.department.service;

import com.c4.hero.common.util.EncryptionUtil;
import com.c4.hero.domain.department.dto.DepartmentDTO;
import com.c4.hero.domain.department.dto.EmployeeDepartmentHistoryDTO;
import com.c4.hero.domain.department.dto.EmployeeGradeHistoryDTO;
import com.c4.hero.domain.department.dto.OrganizationEmployeeDetailDTO;
import com.c4.hero.domain.department.dto.OrganizationNodeDTO;
import com.c4.hero.domain.department.repository.DepartmentRepository;
import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.entity.EmployeeDepartment;
import com.c4.hero.domain.employee.entity.EmployeeDepartmentHistory;
import com.c4.hero.domain.employee.entity.EmployeeGradeHistory;
import com.c4.hero.domain.employee.repository.EmployeeDepartmentHistoryRepository;
import com.c4.hero.domain.employee.repository.EmployeeGradeHistoryRepository;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import com.c4.hero.domain.employee.type.EmployeeStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <pre>
 * Class Name: DepartmentService
 * Description: 부서(Department) 도메인 관련 비즈니스 로직을 처리하는 서비스
 *
 * History
 * 2025/12/24 (이지윤) 최초 작성 및 백엔드 코딩 컨벤션 적용
 * 2025/12/29 (승건) 조직도 조회 기능 추가
 * 2025/12/29 (승건) 부서/직급 이력 조회 기능 추가
 * </pre>
 *
 * 부서 엔티티(EmployeeDepartment)를 조회하여
 * 화면/클라이언트에 필요한 형태의 DepartmentDTO로 변환하는 역할을 담당합니다.
 *
 * 주 사용처:
 * - 공통 부서 드롭다운
 * - 근태/휴가/평가 등에서의 부서 필터링 옵션
 * - 조직도 조회
 *
 * @author 이지윤
 * @version 1.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentService {

    /** 부서(직원-부서 매핑 포함) 조회를 위한 레포지토리 */
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeDepartmentHistoryRepository employeeDepartmentHistoryRepository;
    private final EmployeeGradeHistoryRepository employeeGradeHistoryRepository;
    private final EncryptionUtil encryptionUtil;

    /**
     * 전체 부서 목록을 조회하여 DepartmentDTO 리스트로 반환합니다.
     *
     * <p>특징</p>
     * <ul>
     *     <li>현재는 모든 부서를 단순 조회하여 ID/이름만 반환</li>
     *     <li>추후 사용 여부(Y/N), 정렬 순서, 상위/하위 조직 구조 등이 필요하면 이 계층에서 가공</li>
     * </ul>
     *
     * @return 부서 정보 DTO 리스트
     */
    public List<DepartmentDTO> getDepartments() {
        List<EmployeeDepartment> departments = departmentRepository.findAll();

        return departments.stream()
                .map(d -> new DepartmentDTO(d.getDepartmentId(), d.getDepartmentName()))
                .toList();
    }

    /**
     * 조직도 트리 구조를 조회합니다.
     * 각 부서 노드에는 하위 부서와 소속 직원 정보가 포함됩니다.
     *
     * @return 최상위 부서 노드 리스트
     */
    public List<OrganizationNodeDTO> getOrganizationChart() {
        // 1. 모든 부서 조회
        List<EmployeeDepartment> allDepartments = departmentRepository.findAll();

        // 2. 퇴사하지 않은 모든 직원 조회
        List<Employee> allEmployees = employeeRepository.findAllByStatusNot(EmployeeStatus.RETIRED);

        // 3. 부서 ID별 노드 맵 생성
        Map<Integer, OrganizationNodeDTO> nodeMap = new HashMap<>();
        // 부서장 ID 매핑을 위한 맵 (부서 ID -> 부서장 ID)
        Map<Integer, Integer> managerMap = new HashMap<>();

        for (EmployeeDepartment dept : allDepartments) {
            OrganizationNodeDTO node = OrganizationNodeDTO.builder()
                    .departmentId(dept.getDepartmentId())
                    .departmentName(dept.getDepartmentName())
                    .parentDepartmentId(dept.getParentDepartmentId())
                    .departmentPhone((dept.getDepartmentPhone()))
                    .managerId(dept.getManagerId())
                    .depth(dept.getDepth())
                    .children(new ArrayList<>())
                    .employees(new ArrayList<>())
                    .build();
            nodeMap.put(dept.getDepartmentId(), node);
            if (dept.getManagerId() != null) {
                managerMap.put(dept.getDepartmentId(), dept.getManagerId());
            }
        }

        // 4. 직원들을 해당 부서 노드에 추가
        for (Employee emp : allEmployees) {
            if (emp.getEmployeeDepartment() != null) {
                Integer deptId = emp.getEmployeeDepartment().getDepartmentId();
                OrganizationNodeDTO node = nodeMap.get(deptId);
                if (node != null) {
                    // 암호화된 필드 복호화
                    String decryptedEmail = null;
                    String decryptedPhone = null;
                    String decryptedAddress = null;

                    try {
                        decryptedEmail = emp.getEmail() != null ? encryptionUtil.decrypt(emp.getEmail()) : null;
                    } catch (Exception e) {
                        log.warn("이메일 복호화 실패 - employeeId: {}, error: {}", emp.getEmployeeId(), e.getMessage());
                    }

                    try {
                        decryptedPhone = emp.getPhone() != null ? encryptionUtil.decrypt(emp.getPhone()) : null;
                    } catch (Exception e) {
                        log.warn("전화번호 복호화 실패 - employeeId: {}, error: {}", emp.getEmployeeId(), e.getMessage());
                    }

                    try {
                        decryptedAddress = emp.getAddress() != null ? encryptionUtil.decrypt(emp.getAddress()) : null;
                    } catch (Exception e) {
                        log.warn("주소 복호화 실패 - employeeId: {}, error: {}", emp.getEmployeeId(), e.getMessage());
                    }

                    OrganizationEmployeeDetailDTO empDto = OrganizationEmployeeDetailDTO.builder()
                            .employeeId(emp.getEmployeeId())
                            .employeeName(emp.getEmployeeName())
                            .employeeNumber(emp.getEmployeeNumber())
                            .gradeId(emp.getGrade() != null ? emp.getGrade().getGradeId() : null)
                            .gradeName(emp.getGrade() != null ? emp.getGrade().getGrade() : null)
                            .jobTitleId(emp.getJobTitle() != null ? emp.getJobTitle().getJobTitleId() : null)
                            .jobTitleName(emp.getJobTitle() != null ? emp.getJobTitle().getJobTitle() : null)
                            .imagePath(emp.getImagePath())
                            .email(decryptedEmail)
                            .birthDate(emp.getBirthDate())
                            .gender(emp.getGender())
                            .hireDate(emp.getHireDate())
                            .contractType(emp.getContractType())
                            .status(emp.getStatus().getDescription())
                            .build();
                    node.getEmployees().add(empDto);
                }
            }
        }

        // 5. 각 부서별 직원 정렬
        for (Map.Entry<Integer, OrganizationNodeDTO> entry : nodeMap.entrySet()) {
            Integer deptId = entry.getKey();
            OrganizationNodeDTO node = entry.getValue();
            Integer managerId = managerMap.get(deptId);

            node.getEmployees().sort((e1, e2) -> {
                // 1. 부서장은 가장 앞으로
                boolean isManager1 = managerId != null && managerId.equals(e1.getEmployeeId());
                boolean isManager2 = managerId != null && managerId.equals(e2.getEmployeeId());

                if (isManager1 && !isManager2) return -1;
                if (!isManager1 && isManager2) return 1;
                if (isManager1 && isManager2) return 0; // 둘 다 부서장인 경우는 없겠지만

                // 2. 직책 기준 정렬 (ID가 높을수록 높은 직책, null은 가장 낮음)
                // 관리자(0번)는 예외 처리 필요할 수 있으나, 요구사항에 "0번 관리자 제외"라고 되어있으므로 일반적인 비교
                // 직책 ID가 높을수록 상위라고 가정 (내림차순 정렬)
                Integer jobTitleId1 = e1.getJobTitleId() != null ? e1.getJobTitleId() : -1;
                Integer jobTitleId2 = e2.getJobTitleId() != null ? e2.getJobTitleId() : -1;

                if (!jobTitleId1.equals(jobTitleId2)) {
                    return jobTitleId2.compareTo(jobTitleId1); // 내림차순
                }

                // 3. 직급 기준 정렬 (ID가 높을수록 높은 직급, null은 가장 낮음)
                // 직급 ID가 높을수록 상위라고 가정 (내림차순 정렬)
                Integer gradeId1 = e1.getGradeId() != null ? e1.getGradeId() : -1;
                Integer gradeId2 = e2.getGradeId() != null ? e2.getGradeId() : -1;

                if (!gradeId1.equals(gradeId2)) {
                    return gradeId2.compareTo(gradeId1); // 내림차순
                }

                // 4. 입사일 기준 정렬 (빠를수록 상위 -> 오름차순)
                // null인 경우 가장 뒤로
                if (e1.getHireDate() == null && e2.getHireDate() == null) return 0;
                if (e1.getHireDate() == null) return 1;
                if (e2.getHireDate() == null) return -1;

                return e1.getHireDate().compareTo(e2.getHireDate());
            });
        }

        // 6. 트리 구조 형성 (자식 노드를 부모 노드의 children에 추가)
        List<OrganizationNodeDTO> roots = new ArrayList<>();
        for (OrganizationNodeDTO node : nodeMap.values()) {
            if (node.getParentDepartmentId() == null || node.getParentDepartmentId() == 0) {
                // 최상위 노드 (부모 ID가 없거나 0인 경우)
                roots.add(node);
            } else {
                // 부모 노드 찾아서 자식으로 추가
                OrganizationNodeDTO parent = nodeMap.get(node.getParentDepartmentId());
                if (parent != null) {
                    parent.getChildren().add(node);
                } else {
                    // 부모를 찾을 수 없는 경우 (데이터 무결성 문제 등), 일단 루트로 취급하거나 로그 남김
                    // 여기서는 루트로 추가
                    roots.add(node);
                }
            }
        }

        return roots;
    }

    /**
     * 특정 직원의 부서 변경 이력을 조회합니다.
     *
     * @param employeeId 직원 ID
     * @return 부서 변경 이력 DTO 리스트
     */
    public List<EmployeeDepartmentHistoryDTO> getDepartmentHistory(Integer employeeId) {
        List<EmployeeDepartmentHistory> histories = employeeDepartmentHistoryRepository.findByEmployee_EmployeeIdOrderByChangedAtDesc(employeeId);
        return histories.stream()
                .map(h -> EmployeeDepartmentHistoryDTO.builder()
                        .employeeHistoryId(h.getEmployeeHistoryId())
                        .employeeId(h.getEmployee().getEmployeeId())
                        .changedBy(h.getChangedBy())
                        .changedAt(h.getChangedAt())
                        .changeType(h.getChangeType())
                        .departmentName(h.getDepartmentName())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 특정 직원의 직급 변경 이력을 조회합니다.
     *
     * @param employeeId 직원 ID
     * @return 직급 변경 이력 DTO 리스트
     */
    public List<EmployeeGradeHistoryDTO> getGradeHistory(Integer employeeId) {
        List<EmployeeGradeHistory> histories = employeeGradeHistoryRepository.findByEmployee_EmployeeIdOrderByChangedAtDesc(employeeId);
        return histories.stream()
                .map(h -> EmployeeGradeHistoryDTO.builder()
                        .employeeHistoryId(h.getEmployeeHistoryId())
                        .employeeId(h.getEmployee().getEmployeeId())
                        .changedBy(h.getChangedBy())
                        .changedAt(h.getChangedAt())
                        .changeType(h.getChangeType())
                        .gradeName(h.getGradeName())
                        .build())
                .collect(Collectors.toList());
    }
}
