package com.c4.hero.domain.attendance.repository;

import com.c4.hero.domain.attendance.dto.AttendanceDashboardDTO;
import com.c4.hero.domain.attendance.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

/**
 * <pre>
 * Interface Name: AttendanceDashboardRepository
 * Description   : 근태 점수 대시보드 조회를 위한 JPA 레포지토리
 *
 * History
 * 2025/12/17 (이지윤) 근태 점수 대시보드 조회 메서드 작성 및 코딩 컨벤션 적용
 * </pre>
 *
 * 특정 기간 동안의 근태 이력을 기준으로,
 * 직원별 지각/결근 횟수 및 근태 점수를 집계하여 대시보드 데이터를 제공합니다.
 * 부서 기준 필터링(전체/특정부서)과 페이지네이션을 지원합니다.
 * 점수 계산 로직:
 * <ul>
 *     <li>기본 점수: 100점</li>
 *     <li>지각(LATE) 1회당 -1점</li>
 *     <li>결근(ABSENT) 1회당 -2점</li>
 * </ul>
 *
 * @author 이지윤
 * @version 1.0
 */
public interface AttendanceDashboardRepository extends JpaRepository<Attendance, Integer> {

    /**
     * 근태 점수 대시보드를 페이지 단위로 조회합니다.
     *
     * <p>조회 조건</p>
     * <ul>
     *     <li>{@code startDate} ~ {@code endDate} 기간의 근태 이력을 기준으로 집계</li>
     *     <li>{@code departmentId}가 null이면 전체 부서, 값이 있으면 해당 부서만 대상</li>
     *     <li>직원별로 지각 횟수, 결근 횟수, 계산된 점수를 함께 반환</li>
     * </ul>
     *
     * @param departmentId 조회 대상 부서 ID (null인 경우 전체 부서)
     * @param startDate    조회 시작일 (yyyy-MM-dd)
     * @param endDate      조회 종료일 (yyyy-MM-dd)
     * @param pageable     페이지/정렬 정보
     * @return 근태 대시보드 DTO의 페이지 결과
     */
    @Query(
            value = """
        select new com.c4.hero.domain.attendance.dto.AttendanceDashboardDTO(
            e.employeeId,
            e.employeeNumber,
            e.employeeName,
            d.departmentId,
            d.departmentName,
            coalesce(sum(case when a.state = '지각' then 1L else 0L end), 0L),
            coalesce(sum(case when a.state = '결근' then 1L else 0L end), 0L),
            100L
              - coalesce(sum(case when a.state = '지각' then 1L else 0L end), 0L)
              - coalesce(sum(case when a.state = '결근' then 1L else 0L end), 0L) * 2L
        )
        from Employee e
            join e.employeeDepartment d
            left join Attendance a
                on a.employee = e
               and a.workDate between :startDate and :endDate
        where (:departmentId is null or d.departmentId = :departmentId)
        group by
            e.employeeId,
            e.employeeNumber,
            e.employeeName,
            d.departmentId,
            d.departmentName
        """,
            countQuery = """
        select count(e.employeeId)
        from Employee e
            join e.employeeDepartment d
        where (:departmentId is null or d.departmentId = :departmentId)
        """
    )
    Page<AttendanceDashboardDTO> findAttendanceDashboard(
            @Param("departmentId") Integer departmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );
}
