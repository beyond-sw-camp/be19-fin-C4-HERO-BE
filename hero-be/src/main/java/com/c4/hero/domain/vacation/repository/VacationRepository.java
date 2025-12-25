package com.c4.hero.domain.vacation.repository;

import com.c4.hero.domain.vacation.dto.VacationHistoryDTO;
import com.c4.hero.domain.vacation.entity.VacationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * <pre>
 * Interface Name: VacationRepository
 * Description   : 휴가 이력(VacationLog)에 대한 JPA 기반 조회/저장 기능을 제공하는 레포지토리
 *
 * History
 * 2025/12/16 (이지윤) 휴가 이력 페이지 조회 메서드 추가 및 백엔드 코딩 컨벤션 적용
 * </pre>
 *
 * VacationLog 엔티티를 기반으로,
 * 직원별 휴가 이력 조회 및 VacationHistoryDTO 프로젝션을 제공합니다.
 *
 * @author 이지윤
 * @version 1.0
 */
public interface VacationRepository extends JpaRepository<VacationLog, Integer> {

    /**
     * 직원별 휴가 이력을 페이지 단위로 조회합니다.
     *
     * <p>주요 특징</p>
     * <ul>
     *     <li>특정 직원({@code employeeId}) 기준</li>
     *     <li>{@code startDate}, {@code endDate}는 선택적 기간 필터 (null이면 조건 미적용)</li>
     *     <li>JPQL {@code new} 구문을 사용하여 VacationHistoryDTO로 직접 프로젝션</li>
     *     <li>Pageable을 활용한 페이지네이션 지원</li>
     * </ul>
     *
     * @param employeeId 조회할 직원 ID (필수)
     * @param startDate  조회 시작일 (null인 경우 시작일 제한 없음)
     * @param endDate    조회 종료일 (null인 경우 종료일 제한 없음)
     * @param pageable   페이지/정렬 정보
     * @return 휴가 이력 DTO의 페이지 결과
     */
    @Query(
            """
            select new com.c4.hero.domain.vacation.dto.VacationHistoryDTO(
                v.startDate,
                v.endDate,
                vt.vacationTypeName,
                v.reason,
                v.approvalStatus
            )
            from VacationLog v
                join v.vacationType vt
            where v.employee.employeeId = :employeeId
              and (:startDate is null or v.startDate >= :startDate)
              and (:endDate   is null or v.endDate   <= :endDate)
            order by v.startDate desc
            """
    )
    Page<VacationHistoryDTO> findVacationHistory(
            @Param("employeeId") Integer employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    /**
     * 특정 월(범위)과 "기간이 겹치는" 휴가 로그를 조회합니다.
     *
     * - 조건: v.startDate <= :rangeEnd AND v.endDate >= :rangeStart
     * - 예: 12/31~1/02처럼 월을 가로지르는 휴가도 해당 월 조회에 포함됨
     *
     * @param rangeStart 월 시작일 (예: 2025-12-01)
     * @param rangeEnd   월 종료일 (예: 2025-12-31)
     * @return 해당 월과 겹치는 휴가 로그 목록
     */
    @Query(
            """
            select v
            from VacationLog v
            where v.startDate <= :rangeEnd
              and v.endDate   >= :rangeStart
            order by v.startDate asc
            """
    )
    List<VacationLog> findOverlappingVacationLogs(
            @Param("rangeStart") LocalDate rangeStart,
            @Param("rangeEnd") LocalDate rangeEnd
    );

    List<VacationLog> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
            LocalDate end,
            LocalDate start
    );

}
