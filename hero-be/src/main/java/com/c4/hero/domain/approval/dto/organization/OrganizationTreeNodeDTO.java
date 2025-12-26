package com.c4.hero.domain.approval.dto.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * Class Name  : OrganizationTreeNodeDTO
 * Description : 조직도 트리 노드 DTO
 *               부서와 직원을 모두 표현할 수 있는 재귀 구조
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
public class OrganizationTreeNodeDTO {

    /** 노드 타입 (department: 부서, employee: 직원) */
    private String type;

    /* ===== 부서 정보 (type = "department"일 때) ===== */

    /** 부서 ID */
    private Integer departmentId;

    /** 부서명 */
    private String departmentName;

    /** 부서 깊이 */
    private Integer depth;

    /** 소속 직원 수 */
    private Integer employeeCount;

    /* ===== 직원 정보 (type = "employee"일 때) ===== */

    /** 직원 ID */
    private Integer employeeId;

    /** 직원명 */
    private String employeeName;

    /** 직급명 */
    private String gradeName;

    /** 직책명 */
    private String jobTitleName;

    /* ===== 공통 ===== */

    /** 자식 노드 (하위 부서 또는 소속 직원) */
    private List<OrganizationTreeNodeDTO> children;


    /**
     * 부서 노드 생성
     */
    public static OrganizationTreeNodeDTO createDepartmentNode(
            Integer departmentId,
            String departmentName,
            Integer depth,
            Integer employeeCount,
            List<OrganizationTreeNodeDTO> children
    ) {
        return OrganizationTreeNodeDTO.builder()
                .type("department")
                .departmentId(departmentId)
                .departmentName(departmentName)
                .depth(depth)
                .employeeCount(employeeCount)
                .children(children)
                .build();
    }

    /**
     * 직원 노드 생성
     */
    public static OrganizationTreeNodeDTO createEmployeeNode(
            Integer employeeId,
            String employeeName,
            String gradeName,
            String jobTitleName
    ) {
        return OrganizationTreeNodeDTO.builder()
                .type("employee")
                .employeeId(employeeId)
                .employeeName(employeeName)
                .gradeName(gradeName)
                .jobTitleName(jobTitleName)
                .build();
    }
}