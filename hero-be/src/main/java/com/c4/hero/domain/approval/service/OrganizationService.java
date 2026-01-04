package com.c4.hero.domain.approval.service;

import com.c4.hero.domain.approval.dto.organization.*;

import java.util.List;

/**
 * <pre>
 * Interface Name : OrganizationService
 * Description    : 조직도 관련 비즈니스 로직 인터페이스
 *                  결재 문서 작성 시 결재선/참조자 선택을 위한 조직도 제공
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
public interface OrganizationService {

    /**
     * 조직도 전체 조회 (계층 구조)
     * <pre>
     * 반환 구조:
     * - 가상 루트 노드 (전체 조직)
     *   - 최상위 부서들 (depth=1)
     *     - 하위 부서들 (depth=2, 3, ...)
     *       - 소속 직원들
     *
     * 노드 타입:
     * - "department": 부서 노드
     * - "employee": 직원 노드
     *
     * 용도:
     * - 결재선/참조자 선택 시 전체 조직도 표시
     * - 트리 형태로 부서 계층 및 소속 직원 확인
     * </pre>
     * @return OrganizationTreeResponseDTO 조직도 트리 구조
     */
    OrganizationTreeResponseDTO getOrganizationTree();

    /**
     * 직원 검색
     * <pre>
     * 검색 조건:
     * - keyword: 직원 이름 (필수, LIKE 검색)
     * - departmentId: 부서 필터 (선택)
     * - gradeId: 직급 필터 (선택)
     *
     * 용도:
     * - 결재선/참조자 선택 시 직원 검색
     * - 빠른 직원 찾기
     * </pre>
     * @param requestDTO 검색 조건 (keyword, departmentId, gradeId)
     * @return EmployeeSearchResponseDTO 검색 결과 (직원 목록, 전체 개수)
     */
    EmployeeSearchResponseDTO searchEmployees(EmployeeSearchRequestDTO requestDTO);

    /**
     * 특정 부서의 직원 목록 조회
     * <pre>
     * 용도:
     * - 조직도에서 특정 부서 클릭 시 해당 부서 직원만 표시
     * - 결재선/참조자 선택 시 부서별로 직원 필터링
     * </pre>
     * @param departmentId 부서 ID
     * @return List<OrganizationEmployeeDTO> 부서 소속 직원 목록
     */
    List<OrganizationEmployeeDTO> getDepartmentEmployees(Integer departmentId);
}