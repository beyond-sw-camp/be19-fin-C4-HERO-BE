package com.c4.hero.domain.payroll.batch.repository;

import com.c4.hero.domain.payroll.batch.entity.PayrollItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <pre>
 * Repository Name : PayrollItemRepository
 * Description     : 급여 항목(PayrollItem) 엔티티 관리 리포지토리
 *
 * 역할
 *  - 급여에 포함된 항목(수당/공제 등) CRUD 관리
 *  - 급여 상세 화면 또는 명세서 출력 시 항목 단위 조회 지원
 *
 * History
 *  2025/12/15 - 동근 최초 작성
 *  2025/12/18 - 동근 항목 단위 삭제 로직 추가
 * </pre>
 *
 *  @author 동근
 *  @version 1.1
 */
public interface PayrollItemRepository extends JpaRepository<PayrollItem, Integer> {

    /**
     * 급여 ID 기준 급여 항목 목록 조회
     *
     * @param payrollId 급여 ID
     * @return 급여에 포함된 항목 목록
     */
    List<PayrollItem> findAllByPayrollId(Integer payrollId);


    /**
     * 급여 항목 삭제 (수당/공제 항목 수정 시 기존 항목을 제거할 때 사용)
     * @param payrollId  급여 ID
     * @param itemType 항목 타입(ALLOWANCE 수당 / DEDUCTION 공제 등)
     * @param itemCode 항목 코드
     */
    void deleteByPayrollIdAndItemTypeAndItemCode(
            Integer payrollId,
            String itemType,
            String itemCode
    );
}
