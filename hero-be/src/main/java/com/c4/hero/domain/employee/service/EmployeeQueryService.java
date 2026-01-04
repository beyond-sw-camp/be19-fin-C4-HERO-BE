package com.c4.hero.domain.employee.service;

import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.employee.dto.request.EmployeeSearchDTO;
import com.c4.hero.domain.employee.dto.response.EmployeeDetailResponseDTO;
import com.c4.hero.domain.employee.dto.response.EmployeeListResponseDTO;
import com.c4.hero.domain.employee.dto.response.EmployeeSearchOptionsResponseDTO;
import com.c4.hero.domain.employee.dto.response.MyInfoResponseDTO;

/**
 * <pre>
 * Class Name: EmployeeQueryService
 * Description: 직원 정보 조회 관련 서비스 인터페이스
 *
 * History
 * 2025/12/12 승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
public interface EmployeeQueryService {
    /**
     * 검색 조건과 페이징을 적용하여 직원 목록을 조회합니다.
     * @param searchDTO 검색 및 페이징 조건
     * @return 페이징 처리된 직원 목록
     */
    PageResponse<EmployeeListResponseDTO> getEmployees(EmployeeSearchDTO searchDTO);

    /**
     * ID로 직원 상세 정보를 조회합니다.
     * @param employeeId 직원 ID
     * @return 직원 상세 정보 DTO
     */
    EmployeeDetailResponseDTO findById(Integer employeeId);

    /**
     * ID로 내 정보를 조회합니다.
     * @param employeeId 직원 ID
     * @return 내 정보 DTO
     */
    MyInfoResponseDTO getMyInfo(Integer employeeId);


    /**
     * 부서, 직급, 직책 목록을 조회합니다.
     *
     * @return 검색 옵션 목록
     */
    EmployeeSearchOptionsResponseDTO getEmployeeSearchOptions();
}
