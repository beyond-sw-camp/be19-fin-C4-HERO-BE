package com.c4.hero.domain.attendance.repository;

import com.c4.hero.domain.attendance.dto.DeptWorkSystemDTO;
import com.c4.hero.domain.attendance.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

/**
 * <pre>
 * Interface Name: AttendanceRepository
 * Description   : 근태(Attendance) 엔티티에 대한 JPA 기반 조회/저장 기능을 제공하는 레포지토리
 *
 * History
 * 2025/12/10 (이지윤) 부서 근태 현황 조회 메서드 추가 및 컨벤션 적용
 * 2025/12/10 (이지윤) DB 변경에 따라 코드 수정
 * </pre>
 *
 * Attendance 엔티티를 기본으로 다루며,
 * 부서별 근무제/근태 현황 조회와 같이 JPA로 처리 가능한 조회 로직을 제공합니다.
 *
 * @author 이지윤
 * @version 1.2
 */
public interface DeptWorkSystemRepository extends JpaRepository<Attendance, Integer> {

    /**
     * 부서 근태 현황을 페이지 단위로 조회합니다.
     *
     * <p>주요 특징</p>
     * <ul>
     *     <li>특정 부서({@code departmentId}) + 특정 날짜({@code workDate}) 기준</li>
     *     <li>직원별 근태 상태, 직책, 근무제 이름, 근무제 기준 출퇴근 시간 조회</li>
     *     <li>Pageable을 이용한 페이지네이션 지원</li>
     * </ul>
     *
     * @param departmentId 조회 대상 부서 ID
     * @param workDate     조회 기준 근무일자
     * @param pageable     페이지/정렬 정보
     * @return 부서 근무제/근태 현황 Row DTO의 페이지 결과
     */
    @Query(
            value = """
            select new com.c4.hero.domain.attendance.dto.DeptWorkSystemDTO(
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
              and e.employeeId <> :employeeId
            order by e.employeeName asc
            """,
            countQuery = """
            select count(a)
            from Attendance a
                join a.employee e
                join e.employeeDepartment d
            where a.workDate = :workDate
              and d.departmentId = :departmentId
              and e.employeeId <> :employeeId
            """
    )
    Page<DeptWorkSystemDTO> findDeptWorkSystemRows(
            @Param("employeeId") Integer employeeId,
            @Param("departmentId") Integer departmentId,
            @Param("workDate") LocalDate workDate,
            Pageable pageable
    );

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
}
