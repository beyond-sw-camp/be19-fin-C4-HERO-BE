package com.c4.hero.domain.approval.mapper;

import com.c4.hero.domain.approval.dto.organization.OrganizationDepartmentDTO;
import com.c4.hero.domain.approval.dto.organization.OrganizationEmployeeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <pre>
 * Mapper Interface : OrganizationMapper
 * Description      : 조직도 관련 MyBatis Mapper
 *
 * History
 * 2025/12/26 (민철) 최초 작성
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
@Mapper
public interface OrganizationMapper {

    /**
     * 모든 부서 조회
     *
     * @return List<OrganizationDepartmentDTO> 전체 부서 목록
     */
    List<OrganizationDepartmentDTO> selectAllDepartments();

    /**
     * 모든 직원 조회
     *
     * @return List<OrganizationEmployeeDTO> 전체 직원 목록
     */
    List<OrganizationEmployeeDTO> selectAllEmployees();

    /**
     * 직원 검색
     *
     * @param keyword      검색 키워드
     * @param departmentId 부서 ID (선택)
     * @param gradeId      직급 ID (선택)
     * @return List<OrganizationEmployeeDTO> 검색 결과
     */
    List<OrganizationEmployeeDTO> searchEmployees(
            @Param("keyword") String keyword,
            @Param("departmentId") Integer departmentId,
            @Param("gradeId") Integer gradeId
    );

    /**
     * 특정 부서의 직원 목록 조회
     *
     * @param departmentId 부서 ID
     * @return List<OrganizationEmployeeDTO> 부서 소속 직원 목록
     */
    List<OrganizationEmployeeDTO> selectEmployeesByDepartment(
            @Param("departmentId") Integer departmentId
    );
}