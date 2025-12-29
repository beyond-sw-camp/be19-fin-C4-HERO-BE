package com.c4.hero.domain.payroll.payment.dto;

import java.util.List;

/**
 * <pre>
 * Class Name : PayrollPaymentDetailResponse
 * Description : 관리자(Admin) 급여 조회 상세 응답 DTO
 *
 * History
 *  2025/12/28 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record PayrollPaymentDetailResponseDTO(
        Integer payrollId,
        String salaryMonth,
        String payrollStatus,

        Integer employeeId,
        String employeeNumber,
        String employeeName,
        String departmentName,
        String jobTitleName,

        Integer baseSalary,
        Integer allowanceTotal,
        Integer deductionTotal,
        Integer totalPay,
        Integer netPay,

        List<PayrollItemRow> items
) {
    /**
     * 급여 항목(수당/공제) 상세 행 정보
     */
    public record PayrollItemRow(
            Integer payrollItemId,
            String itemType,
            String itemId,
            String itemName,
            Integer amount,
            Integer sortOrder
    ) {}
}
