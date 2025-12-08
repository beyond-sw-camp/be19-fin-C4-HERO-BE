package com.c4.hero.domain.payroll.report.dto;

// 급여 이력 차트 정보 DTO
public record PayHistoryChartPointDTO(
        String salaryMonth,
        int netPay ) {}
