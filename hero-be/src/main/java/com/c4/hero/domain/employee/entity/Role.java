package com.c4.hero.domain.employee.entity;

import com.c4.hero.domain.employee.type.RoleType;
import com.c4.hero.domain.employee.type.converter.RoleTypeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: Role
 * Description: 시스템의 역할(권한) 정보를 담는 엔티티 클래스
 *
 * History
 * 2025/12/09 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
@Entity
@Table(name = "tbl_roles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {

    /**
     * 역할 ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    /**
     * 역할 이름 (예: EMPLOYEE, ADMIN)
     */
    @Convert(converter = RoleTypeConverter.class)
    @Column(name = "role", length = 50)
    private RoleType role;
}
