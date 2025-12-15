package com.c4.hero.domain.settings.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
