package com.c4.hero.domain.attendance.repository;

import com.c4.hero.domain.attendance.dto.AttendanceEmployeeHalfSummaryDTO;
import com.c4.hero.domain.attendance.dto.AttendanceEmployeeMonthlyStatDTO;
import com.c4.hero.domain.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * <pre>
 * Interface Name : AttendanceEmployeeDashboardRepository
 * Description    : 직원 상세 근태 차트(상/하반기) 조회용 JPA Repository
 *
 * History
 * 2025/12/24 이지윤 최초 작성
 * </pre>
 *
 * - 월별 집계(차트용)
 * - 반기 요약(상단 카드용)
 *
 * @author 이지윤
 * @version 1.0
 */
public interface AttendanceEmployeeDashboardRepository extends JpaRepository<Attendance, Integer> {

    /**
     * 직원의 기간 내 월별 근태 집계(차트용)
     * - month(1~12) 기준 group by
     * - 없는 달(0건)은 결과가 안 내려오므로 Service에서 0 채우기 필요
     */
    @Query("""
        select new com.c4.hero.domain.attendance.dto.AttendanceEmployeeMonthlyStatDTO(
            cast(function('month', a.workDate) as integer),
            coalesce(sum(case when a.state <> '결근' then 1L else 0L end), 0L),
            coalesce(sum(case when a.state = '지각' then 1L else 0L end), 0L),
            coalesce(sum(case when a.state = '결근' then 1L else 0L end), 0L)
        )
        from Attendance a
        where a.employee.employeeId = :employeeId
          and a.workDate between :startDate and :endDate
        group by function('month', a.workDate)
        order by function('month', a.workDate) asc
    """)
    List<AttendanceEmployeeMonthlyStatDTO> findEmployeeMonthlyStats(
            @Param("employeeId") Integer employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 직원의 기간 내 반기 요약(상단 카드용)
     */
    @Query("""
        select new com.c4.hero.domain.attendance.dto.AttendanceEmployeeHalfSummaryDTO(
            coalesce(sum(case when a.state <> '결근' then 1L else 0L end), 0L),
            coalesce(sum(case when a.state = '지각' then 1L else 0L end), 0L),
            coalesce(sum(case when a.state = '결근' then 1L else 0L end), 0L)
        )
        from Attendance a
        where a.employee.employeeId = :employeeId
          and a.workDate between :startDate and :endDate
    """)
    AttendanceEmployeeHalfSummaryDTO findEmployeeHalfSummary(
            @Param("employeeId") Integer employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
