package com.c4.hero.domain.payroll.adjustment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <pre>
 * Mapper Name : PayrollRaiseCommandMapper
 * Description : 급여 인상 승인 시 사원 기준 급여(base salary) 갱신을 위한 Command 전용 매퍼
 *
 * History
 *  2026/12/31 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */

@Mapper
public interface PayrollRaiseCommandMapper {
    /**
     * 사원 기준 급여(base salary) 업데이트
     *
     * @param employeeId 사원 ID
     * @param baseSalary 인상 후 기준 급여
     * @return 업데이트된 행 수
     */
    int updateEmployeeBaseSalary(
            @Param("employeeId") Integer employeeId,
            @Param("baseSalary") Integer baseSalary
    );
}
