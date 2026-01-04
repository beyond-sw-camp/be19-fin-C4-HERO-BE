package com.c4.hero.domain.approval.dto.response;

import com.c4.hero.domain.employee.entity.EmployeeDepartment;
import com.c4.hero.domain.employee.entity.Grade;
import com.c4.hero.domain.employee.entity.JobTitle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * Class Name: PersonnelTypesResponseDTO
 * Description: 인사발령신청서 작성 시 부서/직급/직책 목록 조회 응답 DTO
 *
 * History
 * 2025/12/28 (민철) 최초작성
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
public class PersonnelTypesResponseDTO {

    /**
     * 부서 목록
     */
    private List<EmployeeDepartment> departments;

    /**
     * 직급 목록
     */
    private List<Grade> grades;

    /**
     * 직책 목록
     */
    private List<JobTitle> jobTitles;
}