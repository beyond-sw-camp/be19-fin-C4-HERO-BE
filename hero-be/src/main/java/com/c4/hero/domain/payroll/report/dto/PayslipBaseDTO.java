package com.c4.hero.domain.payroll.report.dto;

// 급여명세서 PDF 다운로드 (후에 안에 내용들 더 가다듬을 예정)
public record PayslipBaseDTO(
        String salaryMonth,
        String employeeName,
        String departmentName,
        int baseSalary,
        int grossPay,
        int totalDeduction,
        int netPay,
        String pdfUrl
) {}

