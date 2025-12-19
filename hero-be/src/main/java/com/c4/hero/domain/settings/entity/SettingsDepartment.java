package com.c4.hero.domain.settings.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: SettingsDepartment
 * Description: 부서 설정 엔티티
 *
 * History
 * 2025/12/16 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_department")
public class SettingsDepartment {

    @Id
    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "department_name")
    private String departmentName;

    @Column(name = "department_phone")
    private String departmentPhone;

    @Column(name = "depth")
    private Integer depth;

    @Column(name = "parent_department_id")
    private Integer parentDepartmentId;

    @Column(name = "manager_id")
    private Integer managerId;
}
