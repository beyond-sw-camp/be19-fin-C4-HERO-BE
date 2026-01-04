package com.c4.hero.domain.payroll.adjustment.dto;

import lombok.Data;

/**
 * <pre>
 * DTO Name : PayrollAdjustmentDetailDTO
 * Description : 급여 조정 상세 정보 DTO
 *
 * History
 *  2025/12/31 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Data
public class PayrollAdjustmentDetailDTO {
    /** 조정 대상 급여 ID */
    private Integer payrollId;

    /** 급여 조정 사유 */
    private String reason;

    /** 조정 부호 ("+" / "-") */
    private String sign;

    /** 조정 금액 */
    private Integer amount;

    /** 조정 반영 월 (YYYY-MM) */
    private String effectiveMonth;
}
