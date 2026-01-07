package com.c4.hero.domain.attendance.entity;

import com.c4.hero.domain.employee.entity.Employee;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * <pre>
 * Entity Name: Attendance
 * Description: 직원의 일별 근태(상태, 근무일, 근무제 등) 정보를 저장하는 엔티티
 *
 * History
 * 2025/12/10 (이지윤) 최초 작성 및 컨벤션 적용
 * </pre>
 *
 * @author 이지윤
 * @version 1.0
 */
@Entity
@Table(name = "tbl_attendance")
@NoArgsConstructor
@Getter
@ToString
public class Attendance {

    /** 근태 PK (식별자) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Integer attendanceId;

    /** 근태 상태 (예: WORK, VACATION, HALF_DAY 등) */
    @Column(name = "state")
    private String state;

    /** 근무 일자 */
    @Column(name = "work_date")
    private LocalDate workDate;

    /** 근무 시작 시간 */
    @Column(name = "start_time")
    private LocalTime startTime;

    /** 근무 종료 시간 */
    @Column(name = "end_time")
    private LocalTime endTime;

    /** 근무 시간 */
    @Column(name = "work_duration")
    private Integer workDuration;

    /** 근태 대상 직원 정보 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    /** 적용 근무제 유형 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_system_type_id")
    private WorkSystemType workSystemType;

    /** 상태값 변경 메서드 */
    public void changeStatus(String status , Integer workDuration) {
        this.state = status;
        this.workDuration = workDuration;
    }
}
