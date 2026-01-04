package com.c4.hero.domain.department.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Class Name: OrganizationNodeDTO
 * Description: 조직도 트리 구조를 위한 노드 DTO
 *
 * History
 * 2025/12/29 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationNodeDTO {

    private Integer departmentId;
    private String departmentName;
    private String departmentPhone;
    private Integer parentDepartmentId;
    private Integer depth;
    private Integer managerId;


    // 하위 부서 목록
    @Builder.Default
    private List<OrganizationNodeDTO> children = new ArrayList<>();

    // 해당 부서에 소속된 직원 목록
    @Builder.Default
    private List<OrganizationEmployeeDetailDTO> employees = new ArrayList<>();
}
