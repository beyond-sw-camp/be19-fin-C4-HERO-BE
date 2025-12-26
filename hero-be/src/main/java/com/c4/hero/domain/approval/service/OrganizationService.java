package com.c4.hero.domain.approval.service;

import com.c4.hero.domain.approval.dto.organization.*;

import java.util.List;

/**
 * <pre>
 * Interface Name : OrganizationService
 * Description    : 조직도 관련 비즈니스 로직 인터페이스
 *
 * History
 * 2025/12/26 (민철) 최초 작성
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
public interface OrganizationService {

    /**
     * 조직도 전체 조회
     * 계층 구조로 조직도를 조회함
     *
     * @return OrganizationTreeResponseDTO 조직도 트리 구조
     */
    OrganizationTreeResponseDTO getOrganizationTree();

    /**
     * 직원 검색
     * 이름, 부서, 직책으로 직원을 검색함
     *
     * @param requestDTO 검색 조건
     * @return EmployeeSearchResponseDTO 검색 결과
     */
    EmployeeSearchResponseDTO searchEmployees(EmployeeSearchRequestDTO requestDTO);

    /**
     * 특정 부서의 직원 목록 조회
     * 특정 부서에 속한 모든 직원 목록을 조회함
     *
     * @param departmentId 부서 ID
     * @return List<OrganizationEmployeeDTO> 부서 소속 직원 목록
     */
    List<OrganizationEmployeeDTO> getDepartmentEmployees(Integer departmentId);
}