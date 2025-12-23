package com.c4.hero.domain.vacation.repository;

import com.c4.hero.domain.vacation.dto.DepartmentVacationDTO;
import com.c4.hero.domain.vacation.entity.VacationLog;
import com.c4.hero.domain.vacation.type.VacationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * <pre>
 * Interface Name: DepartmentVacationRepository
 * Description   : 부서 휴가 캘린더(월 단위) 조회를 위한 JPA 레포지토리
 *
 * - 특정 월 기간과 "겹치는" 휴가 신청 건을 조회
 * - 기본적으로 승인(APPROVED) 상태의 휴가만 조회
 *
 * History
 * 2025/12/16 (이지윤) 최초 작성 및 코딩 컨벤션 적용
 * </pre>
 *
 * 부서 단위 휴가 캘린더(예: 월별 캘린더 화면)에서
 * 직원별 휴가 구간을 렌더링하기 위한 데이터를 제공합니다.
 * VacationLog, Employee, Department, VacationType을 조인하여
 * 하나의 DepartmentVacationDTO로 프로젝션합니다.
 *
 * @author 이지윤
 * @version 1.0
 */
public interface DepartmentVacationRepository extends JpaRepository<VacationLog, Integer> {

    /**
     * 부서 휴가 캘린더(월 단위)에 표시할 휴가 목록을 조회합니다.
     *
     * <p>조회 조건</p>
     * <ul>
     *     <li>{@code v.startDate <= monthEnd} 이면서 {@code v.endDate >= monthStart} 인,
     *         조회 월 기간과 겹치는 휴가만 조회</li>
     *     <li>{@code departmentId}가 null이면 전체 부서(부서 필터 미적용)</li>
     *     <li>{@code status} 기준으로 휴가 승인 상태 필터링 (기본적으로 APPROVED 사용)</li>
     * </ul>
     *
     * @param departmentId 조회 대상 부서 ID (null인 경우 전체 부서)
     * @param monthStart   조회 월의 시작일시 (해당 월의 1일 00:00:00 권장)
     * @param monthEnd     조회 월의 종료일시 (해당 월의 말일 23:59:59 권장)
     * @param status       조회할 휴가 상태 (예: {@link VacationStatus#APPROVED})
     * @return 부서 휴가 캘린더 구성을 위한 휴가 정보 리스트
     */
    @Query(
            """
            select new com.c4.hero.domain.vacation.dto.DepartmentVacationDTO(
                v.vacationLogId,
                e.employeeId,
                e.employeeName,
                vt.vacationTypeName,
                v.startDate,
                v.endDate
            )
            from VacationLog v
                join v.employee e
                join e.employeeDepartment d
                join v.vacationType vt
            where (:departmentId is null or d.departmentId = :departmentId)
              and v.startDate <= :monthEnd
              and v.endDate   >= :monthStart
              and v.approvalStatus = :status
            order by v.startDate asc
            """
    )
    List<DepartmentVacationDTO> findDepartmentVacationByMonth(
            @Param("departmentId") Integer departmentId,
            @Param("monthStart") LocalDate monthStart,
            @Param("monthEnd") LocalDate monthEnd,
            @Param("status") VacationStatus status
    );

    /**
     * 승인(APPROVED) 상태의 휴가만 조회하는 편의 메서드입니다.
     *
     * @param departmentId 조회 대상 부서 ID (null인 경우 전체 부서)
     * @param monthStart   조회 월의 시작일시
     * @param monthEnd     조회 월의 종료일시
     * @return 승인된 휴가에 한정된 부서 휴가 캘린더 정보 리스트
     */
    default List<DepartmentVacationDTO> findApprovedDepartmentVacationByMonth(
            Integer departmentId,
            LocalDate monthStart,
            LocalDate monthEnd
    ) {
        return findDepartmentVacationByMonth(
                departmentId,
                monthStart,
                monthEnd,
                VacationStatus.APPROVED
        );
    }
}
