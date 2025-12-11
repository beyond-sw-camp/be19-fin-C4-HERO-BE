package com.c4.hero.domain.payroll.report.dto;

import java.util.List;


/**
 * 급여명세서 상세 DTO
 * <pre>
 * Class Name: PayslipDetailDTO
 * Description: 개별 급여명세서 상세(급여 항목 + PDF URL) 응답 DTO
 *
 * History
 * 2025/12/08 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record PayslipDetailDTO(
        String salaryMonth,
        String employeeName,
        String departmentName,
        int baseSalary,
        List<PayItemDTO> allowances,
        List<PayItemDTO> deductions,
        int grossPay,
        int totalDeduction,
        int netPay,
        String pdfUrl ) {}
