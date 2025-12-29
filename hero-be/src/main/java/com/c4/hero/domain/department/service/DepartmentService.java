package com.c4.hero.domain.department.service;

import com.c4.hero.common.util.EncryptionUtil;
import com.c4.hero.domain.department.dto.DepartmentDTO;
import com.c4.hero.domain.department.dto.OrganizationEmployeeDetailDTO;
import com.c4.hero.domain.department.dto.OrganizationNodeDTO;
import com.c4.hero.domain.department.repository.DepartmentRepository;
import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.entity.EmployeeDepartment;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import com.c4.hero.domain.employee.type.EmployeeStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Class Name: DepartmentService
 * Description: 부서(Department) 도메인 관련 비즈니스 로직을 처리하는 서비스
 *
 * History
 * 2025/12/24 (이지윤) 최초 작성 및 백엔드 코딩 컨벤션 적용
 * 2025/12/29 (승건) 조직도 조회 기능 추가
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
 * @version 1.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentService {

    /** 부서(직원-부서 매핑 포함) 조회를 위한 레포지토리 */
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
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
        for (EmployeeDepartment dept : allDepartments) {
            OrganizationNodeDTO node = OrganizationNodeDTO.builder()
                    .departmentId(dept.getDepartmentId())
                    .departmentName(dept.getDepartmentName())
                    .parentDepartmentId(dept.getParentDepartmentId())
                    .depth(dept.getDepth())
                    .children(new ArrayList<>())
                    .employees(new ArrayList<>())
                    .build();
            nodeMap.put(dept.getDepartmentId(), node);
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
                            .gradeName(emp.getGrade() != null ? emp.getGrade().getGrade() : null)
                            .jobTitleName(emp.getJobTitle() != null ? emp.getJobTitle().getJobTitle() : null)
                            .imagePath(emp.getImagePath())
                            .email(decryptedEmail)
                            .phone(decryptedPhone)
                            .address(decryptedAddress)
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

        // 5. 트리 구조 형성 (자식 노드를 부모 노드의 children에 추가)
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
}
