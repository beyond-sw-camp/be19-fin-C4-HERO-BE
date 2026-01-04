package com.c4.hero.domain.attendance.repository;

import com.c4.hero.domain.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

/**
 * <pre>
 * Interface Name: AttendanceDashboardSummaryRepository
 * Description   : 근태 점수 대시보드 상단 요약 통계를 조회하기 위한 JPA 레포지토리
 *
 * History
 * 2025/12/24 (이지윤) 최초 작성 및 코딩 컨벤션 적용
 * </pre>
 *
 * 부서별/기간별 기준으로:
 * <ul>
 *     <li>전체 직원 수</li>
 *     <li>우수 직원 수 (근태 점수 95점 이상)</li>
 *     <li>위험 직원 수 (근태 점수 85점 이하)</li>
 * </ul>
 * 를 집계하는 쿼리를 제공합니다.
 *
 * 근태 점수 계산 로직:
 * <pre>
 * score = 100
 *         - (지각 횟수 × 1)
 *         - (결근 횟수 × 2)
 *
 *  - 지각 상태:  a.state = '지각'
 *  - 결근 상태:  a.state = '결근'
 * </pre>
 *
 * @author 이지윤
 * @version 1.0
 */
public interface AttendanceDashboardSummaryRepository extends JpaRepository<Employee, Integer> {

    /**
     * 전체 직원 수를 조회합니다.
     *
     * <p>
     * departmentId가 null이면 전체 부서를 대상으로,
     * 값이 있으면 해당 부서에 속한 직원만 집계합니다.
     * </p>
     *
     * @param departmentId 조회 대상 부서 ID (null이면 전체 부서)
     * @return 조건에 해당하는 전체 직원 수
     */
    @Query(
            """
            select count(e.employeeId)
            from Employee e
                join e.employeeDepartment d
            where (:departmentId is null or d.departmentId = :departmentId)
            """
    )
    long countTotalEmployees(@Param("departmentId") Integer departmentId);

    /**
     * 우수 직원 수(근태 점수 95점 이상)를 조회합니다.
     *
     * <p>근태 점수 계산</p>
     * <ul>
     *     <li>기본 점수: 100점</li>
     *     <li>지각(상태 = '지각') 1회당 -1점</li>
     *     <li>결근(상태 = '결근') 1회당 -2점</li>
     * </ul>
     *
     * <p>조회 조건</p>
     * <ul>
     *     <li>{@code startDate} ~ {@code endDate} 기간의 근태 이력만 대상</li>
     *     <li>departmentId가 null이면 전체 부서, 값이 있으면 해당 부서만 대상</li>
     *     <li>계산된 점수가 95점 이상인 직원 수를 집계</li>
     * </ul>
     *
     * @param departmentId 조회 대상 부서 ID (null이면 전체 부서)
     * @param startDate    근태 점수 산정 시작일
     * @param endDate      근태 점수 산정 종료일
     * @return 점수 95점 이상인 우수 직원 수
     */
    @Query(
            """
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
            """
    )
    long countExcellentEmployees(
            @Param("departmentId") Integer departmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 위험 직원 수(근태 점수 85점 이하)를 조회합니다.
     *
     * <p>근태 점수 계산 로직은 {@link #countExcellentEmployees(Integer, LocalDate, LocalDate)}와 동일하며,</p>
     * <p>점수 기준만 {@code <= 85}로 변경됩니다.</p>
     *
     * @param departmentId 조회 대상 부서 ID (null이면 전체 부서)
     * @param startDate    근태 점수 산정 시작일
     * @param endDate      근태 점수 산정 종료일
     * @return 점수 85점 이하인 위험 직원 수
     */
    @Query(
            """
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
            """
    )
    long countRiskyEmployees(
            @Param("departmentId") Integer departmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
