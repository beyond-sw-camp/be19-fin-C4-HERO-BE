package com.c4.hero.domain.payroll.adjustment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <pre>
 * Mapper Name : PayrollAdjustmentQueryMapper
 * Description : 급여 조정(수기조정) 배치 반영을 위한 조회 전용 Mapper
 *
 * History
 *  2025/12/31 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Mapper
public interface PayrollAdjustmentQueryMapper {
    /**
     * 승인된 조정의 순합(net) 조회
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
