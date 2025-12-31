package com.c4.hero.domain.payroll.adjustment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <pre>
 * Mapper Name : PayrollAdjustmentQueryMapper
 * Description : 급여 조정(수기조정) 배치 반영을 위한 조회 전용 Mapper
 *
 * 역할
 *  - 특정 사원(empId) + 특정 급여월(salaryMonth)에 적용할
 *    승인(APPROVED)된 조정 금액 net 합계를 1방에 조회
 *
 * History
 *  2025/12/31 - 동근 payroll 반영 최적화용 추가
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Mapper
public interface PayrollAdjustmentQueryMapper {
    /**
     * 승인된 조정의 순합(net) 조회
     *  - sign='-'면 음수, 그 외는 양수로 합산
     *  - payroll_id -> tbl_payroll 조인으로 employee_id 매핑(최소안 최적화)
     *
     * @param employeeId  사원 ID
     * @param salaryMonth 급여월(YYYY-MM) = adjustment.effective_month
     * @return net 합계 (없으면 0)
     */
    int sumApprovedAdjustmentNet(
            @Param("employeeId") Integer employeeId,
            @Param("salaryMonth") String salaryMonth
    );
}
