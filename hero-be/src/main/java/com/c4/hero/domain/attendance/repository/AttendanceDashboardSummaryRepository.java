package com.c4.hero.domain.attendance.repository;

import com.c4.hero.domain.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface AttendanceDashboardSummaryRepository extends JpaRepository<Employee, Integer> {

    @Query("""
        select count(e.employeeId)
        from Employee e
            join e.employeeDepartment d
        where (:departmentId is null or d.departmentId = :departmentId)
    """)
    long countTotalEmployees(@Param("departmentId") Integer departmentId);

    @Query("""
        select count(e.employeeId)
        from Employee e
            join e.employeeDepartment d
        where (:departmentId is null or d.departmentId = :departmentId)
          and e.employeeId in (
              select e2.employeeId
              from Employee e2
                  join e2.employeeDepartment d2
                  left join Attendance a
                      on a.employee = e2
                     and a.workDate between :startDate and :endDate
              where (:departmentId is null or d2.departmentId = :departmentId)
              group by e2.employeeId
              having (
                  100L
                  - coalesce(sum(case when a.state = '지각' then 1L else 0L end), 0L) * 1L
                  - coalesce(sum(case when a.state = '결근' then 1L else 0L end), 0L) * 2L
              ) >= 95L
           )
      """)
    long countExcellentEmployees(
            @Param("departmentId") Integer departmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
        select count(e.employeeId)
        from Employee e
            join e.employeeDepartment d
        where (:departmentId is null or d.departmentId = :departmentId)
          and e.employeeId in (
              select e2.employeeId
              from Employee e2
                  join e2.employeeDepartment d2
                  left join Attendance a
                      on a.employee = e2
                     and a.workDate between :startDate and :endDate
              where (:departmentId is null or d2.departmentId = :departmentId)
              group by e2.employeeId
              having (
                  100L
                  - coalesce(sum(case when a.state = '지각' then 1L else 0L end), 0L) * 1L
                  - coalesce(sum(case when a.state = '결근' then 1L else 0L end), 0L) * 2L
              ) <= 85L
          )
    """)
    long countRiskyEmployees(
            @Param("departmentId") Integer departmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
