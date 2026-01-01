package com.c4.hero.domain.payroll.adjustment.dto;

import lombok.Data;

/**
 * <pre>
 * DTO Name : PayrollRaiseDetailDTO
 * Description : 급여 인상 상세 정보 DTO
 *
 * History
 *  2025/12/31 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Data
public class PayrollRaiseDetailDTO {
    /** 급여 인상 대상 사원 ID */
    private Integer employeeId;

    /** 급여 인상 사유 */
    private String reason;

    /** 인상 전 급여 */
    private Integer beforeSalary;

    /** 인상 후 급여 */
    private Integer afterSalary;

    /** 급여 인상률 (%) */
    private Double raisePercent;

    /** 인상 적용 급여월 (YYYY-MM) */
    private String effectiveMonth;
}
