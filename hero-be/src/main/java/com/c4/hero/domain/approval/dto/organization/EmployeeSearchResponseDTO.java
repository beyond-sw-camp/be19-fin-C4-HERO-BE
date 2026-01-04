package com.c4.hero.domain.approval.dto.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * Class Name  : EmployeeSearchResponseDTO
 * Description : 직원 검색 응답 DTO
 *
 * History
 * 2025/12/26 (민철) 최초 작성
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSearchResponseDTO {

    /** 검색 결과 직원 목록 */
    private List<OrganizationEmployeeDTO> employees;

    /** 총 검색 결과 수 */
    private Integer totalCount;
}