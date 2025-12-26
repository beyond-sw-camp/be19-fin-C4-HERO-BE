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
 *
 * History
 * 2025/12/26 (민철) 최초 작성
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationMapper organizationMapper;


    /**
     * 조직도 전체 조회
     * 계층 구조로 조직도를 조회함
     *
     * @return OrganizationTreeResponseDTO 조직도 트리 구조
     */
    @Override
    public OrganizationTreeResponseDTO getOrganizationTree() {
        log.info("📋 조직도 전체 조회 시작");

        // 1. 모든 부서 조회
        List<OrganizationDepartmentDTO> departments = organizationMapper.selectAllDepartments();
        log.info("  - 조회된 부서 수: {}", departments.size());

        // 2. 모든 직원 조회
        List<OrganizationEmployeeDTO> employees = organizationMapper.selectAllEmployees();
        log.info("  - 조회된 직원 수: {}", employees.size());

        // 3. 부서별 직원 그룹핑
        Map<Integer, List<OrganizationEmployeeDTO>> employeesByDept = employees.stream()
                .collect(Collectors.groupingBy(OrganizationEmployeeDTO::getDepartmentId));

        // 4. 가상 루트 노드 생성 (depth=1인 모든 부서를 children으로)
        List<OrganizationTreeNodeDTO> rootChildren = new ArrayList<>();

        // depth=1인 부서들만 필터링 (실제 최상위 부서)
        List<OrganizationDepartmentDTO> topLevelDepts = departments.stream()
                .filter(dept -> dept.getDepth() != null && dept.getDepth() == 1)
                .collect(Collectors.toList());

        log.info("  - 최상위 부서 수 (depth=1): {}", topLevelDepts.size());

        // 각 최상위 부서를 트리로 구성
        for (OrganizationDepartmentDTO topDept : topLevelDepts) {
            OrganizationTreeNodeDTO deptNode = buildDepartmentNode(topDept, departments, employeesByDept);
            if (deptNode != null) {
                rootChildren.add(deptNode);
            }
        }

        // 5. 가상 루트 생성
        OrganizationTreeNodeDTO virtualRoot = OrganizationTreeNodeDTO.builder()
                .type("department")
                .departmentId(0)
                .departmentName("전체 조직")
                .depth(0)
                .employeeCount(employees.size())
                .children(rootChildren)
                .build();

        log.info("✅ 조직도 전체 조회 완료");

        return OrganizationTreeResponseDTO.builder()
                .root(virtualRoot)
                .build();
    }


    /**
     * 부서 노드 생성
     *
     * @param dept            현재 부서
     * @param allDepartments  전체 부서 목록
     * @param employeesByDept 부서별 직원 맵
     * @return OrganizationTreeNodeDTO 부서 노드
     */
    private OrganizationTreeNodeDTO buildDepartmentNode(
            OrganizationDepartmentDTO dept,
            List<OrganizationDepartmentDTO> allDepartments,
            Map<Integer, List<OrganizationEmployeeDTO>> employeesByDept
    ) {
        List<OrganizationTreeNodeDTO> children = new ArrayList<>();

        // 1. 하위 부서 추가
        List<OrganizationDepartmentDTO> subDepartments = allDepartments.stream()
                .filter(d -> dept.getDepartmentId().equals(d.getParentDepartmentId()))
                .collect(Collectors.toList());

        for (OrganizationDepartmentDTO subDept : subDepartments) {
            children.add(buildDepartmentNode(subDept, allDepartments, employeesByDept));
        }

        // 2. 소속 직원 추가
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

        // 3. 부서 노드 생성
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
     * 이름, 부서, 직책으로 직원을 검색함
     *
     * @param requestDTO 검색 조건
     * @return EmployeeSearchResponseDTO 검색 결과
     */
    @Override
    public EmployeeSearchResponseDTO searchEmployees(EmployeeSearchRequestDTO requestDTO) {
        log.info("🔍 직원 검색 시작 - keyword: {}", requestDTO.getKeyword());

        List<OrganizationEmployeeDTO> employees = organizationMapper.searchEmployees(
                requestDTO.getKeyword(),
                requestDTO.getDepartmentId(),
                requestDTO.getGradeId()
        );

        log.info("✅ 직원 검색 완료 - 결과: {}건", employees.size());

        return EmployeeSearchResponseDTO.builder()
                .employees(employees)
                .totalCount(employees.size())
                .build();
    }


    /**
     * 특정 부서의 직원 목록 조회
     * 특정 부서에 속한 모든 직원 목록을 조회함
     *
     * @param departmentId 부서 ID
     * @return List<OrganizationEmployeeDTO> 부서 소속 직원 목록
     */
    @Override
    public List<OrganizationEmployeeDTO> getDepartmentEmployees(Integer departmentId) {
        log.info("👥 부서별 직원 조회 시작 - departmentId: {}", departmentId);

        List<OrganizationEmployeeDTO> employees = organizationMapper.selectEmployeesByDepartment(departmentId);

        log.info("✅ 부서별 직원 조회 완료 - 결과: {}명", employees.size());

        return employees;
    }
}