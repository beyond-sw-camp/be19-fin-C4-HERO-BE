package com.c4.hero.domain.payroll.report.dto;

import java.util.List;

// 개별 급여명세서 상세 DTO (관리자 페이지에서 사용 할 예정)
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
