package com.c4.hero.domain.attendance.repository;

import com.c4.hero.domain.attendance.dto.AttendanceDashboardDTO;
import com.c4.hero.domain.attendance.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface AttendanceDashboardRepository extends JpaRepository<Attendance, Integer> {

    /**
     * 근태 점수 대시보드 조회
     *
     * - 기간(startDate ~ endDate) 동안의 근태 이력을 기준으로
     *   직원별 지각/결근 횟수 및 점수를 집계
     * - departmentId가 null이면 전체 부서 대상으로 조회
     *
     * 점수 계산:
     *   100 - (지각 × 1) - (결근 × 2)
     */
    @Query(
            value = """
            select new com.c4.hero.domain.attendance.dto.AttendanceDashboardDTO(
                e.employeeId,
                e.employeeNumber,
                e.employeeName,
                d.departmentId,
                d.departmentName,
                sum(case when a.state = 'LATE' then 1 else 0 end),
                sum(case when a.state = 'ABSENT' then 1 else 0 end),
                100
                  - sum(case when a.state = 'LATE' then 1 else 0 end) * 1
                  - sum(case when a.state = 'ABSENT' then 1 else 0 end) * 2
            )
            from Attendance a
                join a.employee e
                join e.employeeDepartment d
            where (:departmentId is null or d.departmentId = :departmentId)
              and a.workDate between :startDate and :endDate
            group by
                e.employeeId,
                e.employeeNumber,
                e.employeeName,
                d.departmentId,
                d.departmentName
            """,
            countQuery = """
            select count(distinct e.employeeId)
            from Attendance a
                join a.employee e
                join e.employeeDepartment d
            where (:departmentId is null or d.departmentId = :departmentId)
              and a.workDate between :startDate and :endDate
            """
    )
    Page<AttendanceDashboardDTO> findAttendanceDashboard(
            @Param("departmentId") Integer departmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );
}
