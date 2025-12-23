package com.c4.hero.domain.payroll.batch.repository;

import com.c4.hero.domain.payroll.batch.entity.Payroll;
import com.c4.hero.domain.payroll.common.type.PayrollStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * <pre>
 * Repository Name : PayrollRepository
 * Description     : 사원별 급여(Payroll) 엔티티 관리 리포지토리
 *
 * 역할
 *  - 사원 단위 급여 엔티티 CRUD 관리
 *  - (사원 + 급여월) 기준 중복 급여 생성 방지
 *
 * 도메인 규칙
 *  - 동일 사원(employeeId)은 동일 급여월(salaryMonth)에 대해
 *    하나의 급여 데이터만 가질 수 있음
 *
 * History
 *  2025/12/15 - 동근 최초 작성
 *  2025/12/18 - 동근 배치 상태 처리 기능 확장
 * </pre>
 *
 *  @author 동근
 *  @version 1.1
 */

public interface PayrollRepository extends JpaRepository<Payroll, Integer> {

    /**
     * 사원 + 급여월 기준 급여 조회
     *
     * @param employeeId 사원 ID
     * @param salaryMonth 급여월 (YYYY-MM)
     * @return 급여 엔티티 Optional
     */
    Optional<Payroll> findByEmployeeIdAndSalaryMonth(Integer employeeId, String salaryMonth);

    /**
     * 사원 + 급여월 기준 급여 존재 여부 확인
     *
     * @param employeeId 사원 ID
     * @param salaryMonth 급여월 (YYYY-MM)
     * @return 존재 여부
     */
    boolean existsByEmployeeIdAndSalaryMonth(Integer employeeId, String salaryMonth);

    /**
     * 배치 내에 특정 급여 상태를 가진 급여가 존재하는지 확인
     */
    boolean existsByBatchIdAndStatus(Integer batchId, PayrollStatus status);

    /**
     * 배치 단위 급여 일괄 상태 변경 (벌크 업데이트 쿼리 사용)
     *
     * - 배치 내 모든 급여를 특정 상태로 일괄 전환
     * - 이미 해당 상태인 급여는 제외
     *
     * @param batchId 배치 ID
     */
    @Modifying
    @Query("""
        update Payroll p
           set p.status = :status
         where p.batchId = :batchId
           and p.status <> :status
        """)
    int updateStatusByBatchId(
            @Param("batchId") Integer batchId,
            @Param("status") PayrollStatus status
    );

    /**
     * 해당 배치에 소속된 급여가 1건 이상 존재하는지 확인
     * @param batchId 배치ID
     */
    boolean existsByBatchId(Integer batchId);

    /**
     * 특정 배치에 속한 모든 급여 목록 조회
     *
     * @param batchId 배치 ID
     * @return 급여 리스트
     */
    List<Payroll> findAllByBatchId(Integer batchId);
}
