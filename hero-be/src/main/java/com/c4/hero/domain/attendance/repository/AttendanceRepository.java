package com.c4.hero.domain.attendance.repository;

import com.c4.hero.domain.attendance.dto.DeptWorkSystemRowDTO;
import com.c4.hero.domain.attendance.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    /**
     * 부서 근태 현황 조회
     *
     * - 특정 부서 + 특정 날짜 기준
     * - 직원별 근태 상태 + 직책 + 근무제 + 근무시간 조회
     * - 페이지네이션 지원
     */
    @Query(
            value = """
            select new com.c4.hero.domain.attendance.dto.DeptWorkSystemRowDTO(
                e.employeeId,
                d.departmentId,
                e.employeeName,
                a.state,
                jt.jobTitle,
                wst.workSystemName,
                tmpl.startTime,
                tmpl.endTime
            )
            from Attendance a
                join a.employee e
                join e.employeeDepartment d
                left join e.jobTitle jt
                join a.workSystemType wst
                join WorkSystemTemplate tmpl
                    on tmpl.workSystemType = wst
            where a.workDate = :workDate
              and d.departmentId = :departmentId
            order by e.employeeName asc
        """,
            countQuery = """
            select count(a)
            from Attendance a
                join a.employee e
                join e.employeeDepartment d
            where a.workDate = :workDate
              and d.departmentId = :departmentId
        """
    )
    Page<DeptWorkSystemRowDTO> findDeptWorkSystemRows(
            @Param("departmentId") Integer departmentId,
            @Param("workDate") LocalDate workDate,
            Pageable pageable
    );
}
