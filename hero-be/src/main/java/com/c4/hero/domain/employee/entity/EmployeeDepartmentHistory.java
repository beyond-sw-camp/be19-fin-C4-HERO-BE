package com.c4.hero.domain.employee.entity;

import com.c4.hero.domain.employee.type.ChangeType;
import com.c4.hero.domain.employee.type.ChangeTypeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: EmployeeDepartmentHistory
 * Description: 직원의 부서 변경 이력을 담는 엔티티 클래스
 *
 * History
 * 2025/12/09 이승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
@Entity
@Table(name = "tbl_employee_department_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmployeeDepartmentHistory {

    /**
     * 부서 변경 이력 ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_history_id")
    private Integer employeeHistoryId;

    /**
     * 이력의 대상이 되는 직원
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    /**
     * 변경을 수행한 관리자의 계정 ID
     */
    @Column(name = "changed_by")
    private Integer changedBy;

    /**
     * 변경 일시
     */
    @CreationTimestamp
    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    /**
     * 변경 유형 (예: 생성, 변경, 삭제)
     */
    @Convert(converter = ChangeTypeConverter.class)
    @Column(name = "change_type", nullable = false, length = 50)
    private ChangeType changeType;

    /**
     * 변경된 부서의 이름
     */
    @Column(name = "department_name", nullable = false, length = 20)
    private String departmentName;

    @Builder
    public EmployeeDepartmentHistory(Employee employee, Integer changedBy, ChangeType changeType, String departmentName) {
        this.employee = employee;
        this.changedBy = changedBy;
        this.changeType = changeType;
        this.departmentName = departmentName;
    }
}
