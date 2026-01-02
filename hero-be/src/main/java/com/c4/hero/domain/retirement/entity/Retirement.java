package com.c4.hero.domain.retirement.entity;

import com.c4.hero.domain.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_employee_exit")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Retirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_exit_id")
    private Integer employeeExitId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exit_reason_id", nullable = false)
    private ExitReasonMaster exitReason;

    @Column(name = "exit_date", nullable = false)
    private LocalDate exitDate;

    @Column(name = "detail_reason")
    private String detailReason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "working_days", nullable = false)
    private Integer workingDays;

    @Builder
    public Retirement(Employee employee, ExitReasonMaster exitReason, LocalDate exitDate, String detailReason, Integer workingDays) {
        this.employee = employee;
        this.exitReason = exitReason;
        this.exitDate = exitDate;
        this.detailReason = detailReason;
        this.workingDays = workingDays;
    }
}
