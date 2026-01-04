package com.c4.hero.domain.payroll.payslip.dto;

import com.c4.hero.domain.payroll.report.dto.PayItemDTO;

import java.util.List;


/**
 * 급여명세서 상세 DTO
 * <pre>
 * Class Name: PayslipDetailDTO
 * Description: 개별 급여명세서 상세(급여 항목 + PDF URL) 응답 DTO
 *
 * History
 * 2025/12/08 동근 최초 작성
 * 2025/12/14 동근 report 도메인에서 payslip 도메인으로 분리
 * </pre>
 *
 * @author 동근
 * @version 1.1
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
