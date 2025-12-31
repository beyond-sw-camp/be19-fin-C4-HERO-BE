package com.c4.hero.domain.vacation.entity;

import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.vacation.type.VacationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lombok.Setter;

import java.time.LocalDate;


/**
 * <pre>
 * Entity Name: VacationLog
 * Description: 직원의 휴가 사용 이력(기간, 사유, 상태 등)을 저장하는 엔티티
 *
 * History
 * 2025/12/16 (이지윤) 최초 작성 및 백엔드 코딩 컨벤션 적용
 * </pre>
 *
 * 한 번의 휴가 사용(연차, 반차, 병가 등)에 대한 시작일/종료일, 사유, 결재 상태를 관리하며,
 * 직원(Employee)과 휴가 유형(VacationType)과 연관됩니다.
 *
 * @author 이지윤
 * @version 1.0
 */
@Entity
@Table(name = "tbl_vacation_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class VacationLog {

    /** 휴가 이력 PK (식별자) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vacation_log_id")
    private Integer vacationLogId;

    /** 휴가 시작일 (yyyy-MM-dd) */
    private LocalDate startDate;

    /** 휴가 종료일 (yyyy-MM-dd) */
    private LocalDate endDate;

    /** 휴가 유형 정보 (예: 연차, 반차, 병가 등) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacation_type_id")
    private VacationType vacationType;

    /** 휴가 사유 */
    private String reason;

    /** 휴가 결재 상태 (예: PENDING, APPROVED, REJECTED 등) */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private VacationStatus approvalStatus;

    /** 휴가를 신청/사용한 직원 정보 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;


    public static VacationLog create(
            Employee employee,
            VacationType vacationType,
            LocalDate startDate,
            LocalDate endDate,
            String reason,
            VacationStatus status
    ) {
        VacationLog log = new VacationLog();

        log.employee = employee;
        log.vacationType = vacationType;
        log.startDate = startDate;
        log.endDate = endDate;
        log.reason = reason;
        log.approvalStatus = status;

        return log;
    }

    public void info(String s, Integer employeeId, int vacationTypeId, LocalDate startDate, LocalDate endDate) {
    }
}
