package com.c4.hero.domain.payroll.payment.dto;

/**
 * <pre>
 * Class Name : PayrollPaymentSearchRequest
 * Description : 관리자(Admin) 급여 조회 검색 조건 요청 DTO
 *
 * History
 *  2025/12/28 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record PayrollPaymentSearchRequestDTO(
        String salaryMonth,
        Integer departmentId,
        Integer jobTitleId,
        String keyword
) {}
