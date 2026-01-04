package com.c4.hero.domain.approval.dto.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name  : EmployeeSearchRequestDTO
 * Description : 직원 검색 요청 DTO
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
public class EmployeeSearchRequestDTO {

    /** 검색 키워드 (이름, 부서, 직책) */
    private String keyword;

    /** 부서 ID (선택) */
    private Integer departmentId;

    /** 직급 ID (선택) */
    private Integer gradeId;
}