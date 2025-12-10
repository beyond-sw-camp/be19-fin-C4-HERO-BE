package com.c4.hero.domain.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: Department
 * Description: 부서 정보를 담는 엔티티 클래스
 *
 * History
 * 2025/12/09 이승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
@Entity
@Table(name = "tbl_department")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmployeeDepartment {

    /**
     * 부서 ID (PK)
     */
    @Id
    @Column(name = "department_id")
    private Integer departmentId;

    /**
     * 부서명
     */
    @Column(name = "department_name", nullable = false, length = 100)
    private String departmentName;

    /**
     * 부서 연락처
     */
    @Column(name = "department_phone", length = 20)
    private String departmentPhone;

    /**
     * 부서 조직도 깊이 (Depth)
     */
    @Column(name = "depth", nullable = false)
    private Integer depth;

    /**
     * 상위 부서 ID
     */
    @Column(name = "parent_department_id")
    private Integer parentDepartmentId;

    /**
     * 부서장(매니저)의 직원 ID
     */
    @Column(name = "manager_id")
    private Integer managerId;
}
