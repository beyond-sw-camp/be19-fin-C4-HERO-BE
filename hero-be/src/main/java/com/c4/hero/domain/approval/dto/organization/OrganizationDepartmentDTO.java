package com.c4.hero.domain.approval.dto.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name  : OrganizationDepartmentDTO
 * Description : 부서 정보 DTO
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
public class OrganizationDepartmentDTO {

    /** 부서 ID */
    private Integer departmentId;

    /** 부서명 */
    private String departmentName;

    /** 부서 깊이 (1: 본부, 2: 팀, 3: 파트) */
    private Integer depth;

    /** 상위 부서 ID */
    private Integer parentDepartmentId;

    /** 부서장 ID */
    private Integer managerId;

    /** 부서장명 */
    private String managerName;

    /** 소속 직원 수 */
    private Integer employeeCount;
}