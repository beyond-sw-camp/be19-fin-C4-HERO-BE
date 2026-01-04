package com.c4.hero.domain.vacation.repository;

import com.c4.hero.domain.vacation.dto.VacationSummaryDTO;
import com.c4.hero.domain.vacation.entity.VacationLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * <pre>
 * Interface Name : VacationSummaryRepository
 * Description    : 직원별 연차 요약(총/사용/잔여)을 조회하기 위한 레포지토리
 *
 * History
 * 2025/12/21 (이지윤) 최초 작성
 * 2025/12/21 (이지윤) DB 변경에 따라 수정
 * </pre>
 */
public interface VacationSummaryRepository extends JpaRepository<VacationLeave, Integer> {

    /**
     * 특정 직원의 연차 요약 정보를 조회합니다.
     *
     * - 현재 테이블(tbl_leave)에 직원당 1건만 있다고 가정하고 단일 DTO를 반환합니다.
     * - 나중에 연도별로 여러 건이 생기면 ORDER BY + List 반환으로 확장해서
     *   서비스 계층에서 "가장 최신 1건"을 선택하면 됩니다.
     *
     * @param employeeId 조회할 직원 ID
     * @return 해당 직원의 연차 요약 DTO (없으면 null 반환)
     */
    @Query("""
        select new com.c4.hero.domain.vacation.dto.VacationSummaryDTO(
            l.leaveId,
            l.grantDays,
            l.usedDays,
            l.remainingDays
        )
        from VacationLeave l
        where l.employee.employeeId = :employeeId
        order by l.grantDate desc, l.leaveId desc
        """)
    List<VacationSummaryDTO> findSummaryByEmployeeId(@Param("employeeId") Integer employeeId);
}
