package com.c4.hero.domain.approval.service;

import com.c4.hero.domain.approval.dto.organization.*;
import com.c4.hero.domain.approval.mapper.OrganizationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <pre>
 * Class Name  : OrganizationServiceImpl
 * Description : 조직도 관련 비즈니스 로직 구현
 *               결재 문서 작성 시 결재선/참조자 선택을 위한 조직도 제공
 *
 * 주요 기능:
 *   - 조직도 전체 조회 (계층 구조)
 *   - 직원 검색 (이름, 부서, 직급 기준)
 *   - 특정 부서의 직원 목록 조회
 *
 * History
 * 2025/12/26 (민철) 최초 작성
 * 2026/01/03 (민철) 메서드 주석 개선
 *
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationMapper organizationMapper;


    /**
     * 조직도 전체 조회 (계층 구조)
     * <pre>
     * 처리 흐름:
     * 1. 모든 부서 조회 (MyBatis)
     * 2. 모든 직원 조회 (MyBatis)
     * 3. 부서별 직원 그룹핑 (Stream API 사용)
     * 4. depth=1인 최상위 부서만 필터링
     * 5. 각 최상위 부서에 대해 재귀적으로 트리 구조 생성
     *    - 하위 부서 추가
     *    - 소속 직원 추가
     * 6. 가상 루트 노드 생성 (전체 조직을 감싸는 루트)
     *
     * 트리 구조:
     * - 루트 (가상 노드)
     *   - 최상위 부서 1 (depth=1)
     *     - 하위 부서 1-1 (depth=2)
     *       - 직원 1
     *       - 직원 2
     *     - 직원 3
     *   - 최상위 부서 2 (depth=1)
     *     - ...
     *
     * 노드 타입:
     * - "department": 부서 노드
     * - "employee": 직원 노드
     * </pre>
     * @return OrganizationTreeResponseDTO 조직도 트리 구조
     */
    @Override
    public OrganizationTreeResponseDTO getOrganizationTree() {

        List<OrganizationDepartmentDTO> departments = organizationMapper.selectAllDepartments();

        List<OrganizationEmployeeDTO> employees = organizationMapper.selectAllEmployees();

        Map<Integer, List<OrganizationEmployeeDTO>> employeesByDept = employees.stream()
                .collect(Collectors.groupingBy(OrganizationEmployeeDTO::getDepartmentId));

        List<OrganizationTreeNodeDTO> rootChildren = new ArrayList<>();

        List<OrganizationDepartmentDTO> topLevelDepts = departments.stream()
                .filter(dept -> dept.getDepth() != null && dept.getDepth() == 1)
                .collect(Collectors.toList());


        for (OrganizationDepartmentDTO topDept : topLevelDepts) {
            OrganizationTreeNodeDTO deptNode = buildDepartmentNode(topDept, departments, employeesByDept);
            if (deptNode != null) {
                rootChildren.add(deptNode);
            }
        }

        OrganizationTreeNodeDTO virtualRoot = OrganizationTreeNodeDTO.builder()
                .type("department")
                .departmentId(0)
                .departmentName("전체 조직")
                .depth(0)
                .employeeCount(employees.size())
                .children(rootChildren)
                .build();


        return OrganizationTreeResponseDTO.builder()
                .root(virtualRoot)
                .build();
    }


    /**
     * 부서 노드 생성 (재귀 메서드)
     * <pre>
     * 처리 흐름:
     * 1. 하위 부서 찾기
     *    - parentDepartmentId가 현재 부서 ID와 일치하는 부서 필터링
     *    - 각 하위 부서에 대해 재귀 호출
     * 2. 소속 직원 추가
     *    - 현재 부서에 속한 직원들을 children에 추가
     * 3. 부서 노드 생성
     *    - children에 하위 부서 + 소속 직원 포함
     *
     * 재귀 종료 조건:
     * - 하위 부서도 없고 소속 직원도 없는 경우 (leaf 노드)
     * </pre>
     * @param dept            현재 부서
     * @param allDepartments  전체 부서 목록
     * @param employeesByDept 부서별 직원 맵 (key: departmentId, value: 직원 목록)
     * @return OrganizationTreeNodeDTO 부서 노드 (하위 부서 및 소속 직원 포함)
     */
    private OrganizationTreeNodeDTO buildDepartmentNode(
            OrganizationDepartmentDTO dept,
            List<OrganizationDepartmentDTO> allDepartments,
            Map<Integer, List<OrganizationEmployeeDTO>> employeesByDept
    ) {
        List<OrganizationTreeNodeDTO> children = new ArrayList<>();

        List<OrganizationDepartmentDTO> subDepartments = allDepartments.stream()
                .filter(d -> dept.getDepartmentId().equals(d.getParentDepartmentId()))
                .collect(Collectors.toList());

        for (OrganizationDepartmentDTO subDept : subDepartments) {
            children.add(buildDepartmentNode(subDept, allDepartments, employeesByDept));
        }

        List<OrganizationEmployeeDTO> deptEmployees = employeesByDept.getOrDefault(
                dept.getDepartmentId(),
                new ArrayList<>()
        );

        for (OrganizationEmployeeDTO employee : deptEmployees) {
            children.add(OrganizationTreeNodeDTO.createEmployeeNode(
                    employee.getEmployeeId(),
                    employee.getEmployeeName(),
                    employee.getGradeName(),
                    employee.getJobTitleName()
            ));
        }

        return OrganizationTreeNodeDTO.createDepartmentNode(
                dept.getDepartmentId(),
                dept.getDepartmentName(),
                dept.getDepth(),
                deptEmployees.size(),
                children
        );
    }


    /**
     * 직원 검색
     * <pre>
     * 검색 조건:
     * - keyword: 직원 이름으로 검색 (LIKE 검색)
     * - departmentId: 특정 부서로 필터링 (선택)
     * - gradeId: 특정 직급으로 필터링 (선택)
     *
     * 검색 방식:
     * - keyword는 필수 (직원 이름에 포함된 문자열 검색)
     * - departmentId, gradeId는 선택적 필터
     * - AND 조건으로 결합
     * </pre>
     * @param requestDTO 검색 조건 (keyword, departmentId, gradeId)
     * @return EmployeeSearchResponseDTO 검색 결과 (직원 목록, 전체 개수)
     */
    @Override
    public EmployeeSearchResponseDTO searchEmployees(EmployeeSearchRequestDTO requestDTO) {

        List<OrganizationEmployeeDTO> employees = organizationMapper.searchEmployees(
                requestDTO.getKeyword(),
                requestDTO.getDepartmentId(),
                requestDTO.getGradeId()
        );


        return EmployeeSearchResponseDTO.builder()
                .employees(employees)
                .totalCount(employees.size())
                .build();
    }


    /**
     * 특정 부서의 직원 목록 조회
     * <pre>
     * 용도:
     * - 조직도에서 특정 부서 클릭 시 해당 부서 직원만 표시
     * - 결재선/참조자 선택 시 부서별로 직원 필터링
     * </pre>
     * @param departmentId 부서 ID
     * @return List<OrganizationEmployeeDTO> 부서 소속 직원 목록 (직원 정보, 부서명, 직급, 직책 포함)
     */
    @Override
    public List<OrganizationEmployeeDTO> getDepartmentEmployees(Integer departmentId) {

        List<OrganizationEmployeeDTO> employees = organizationMapper.selectEmployeesByDepartment(departmentId);

        return employees;
    }
}