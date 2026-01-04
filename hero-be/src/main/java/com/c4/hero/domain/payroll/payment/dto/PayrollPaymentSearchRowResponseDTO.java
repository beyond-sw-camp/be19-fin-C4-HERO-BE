package com.c4.hero.domain.payroll.payment.dto;

/**
 * <pre>
 * Class Name : PayrollPaymentSearchRowResponse
 * Description : 관리자(Admin) 급여 조회 목록(행) 응답 DTO
 *
 * History
 *  2025/12/28 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record PayrollPaymentSearchRowResponseDTO(
        Integer payrollId,
        Integer employeeId,
        String employeeNumber,
        String employeeName,
        String departmentName,
        String jobTitleName,
        Integer baseSalary,
        Integer allowanceTotal,
        Integer deductionTotal,
        Integer netPay,
        String payrollStatus,
        String salaryMonth
) {}
