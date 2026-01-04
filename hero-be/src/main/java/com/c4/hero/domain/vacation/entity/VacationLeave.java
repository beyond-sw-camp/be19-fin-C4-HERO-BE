package com.c4.hero.domain.vacation.entity;

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

import java.time.LocalDate;

/**
 * <pre>
 * Entity Name: VacationLeave
 * Description: 직원에게 부여된 연차(휴가) 일수와 사용/잔여 일수를 관리하는 엔티티
 *
 * History
 * 2025/12/18 (이지윤) 최초 작성 및 코딩 컨벤션 적용
 * 2026/01/03 (이지윤) DB 변경데 따라 기능 수정
 *
 * 직원별 연차 부여·사용 현황을 관리하는 테이블에 매핑되며,
 * 휴가 요약(VacationSummaryDTO) 계산의 기초 데이터로 활용됩니다.
 *
 * @author 이지윤
 * @version 1.1
 */
@Entity
@Table(name = "tbl_leave")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VacationLeave {

    /** 연차(휴가) 부여 이력 PK (식별자) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leave_id")
    private Integer leaveId;

    @Column(name = "grant_date")
    private LocalDate grantDate;

    /** 부여된 총 연차(휴가) 일수 */
    @Column(name = "grant_days")
    private Integer grantDays;

    /** 사용한 연차(휴가) 일수 */
    @Column(name = "used_days")
    private Integer usedDays;

    /** 남은 연차(휴가) 일수 */
    @Column(name = "remaining_days")
    private Integer remainingDays;

    /** 연차(휴가) 부여 대상 직원 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
