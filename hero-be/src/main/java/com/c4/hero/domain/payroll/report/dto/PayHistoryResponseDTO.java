package com.c4.hero.domain.payroll.report.dto;

import java.util.List;

// 급여 이력 페이지 전체 데이터 DTO
public record PayHistoryResponseDTO(
        int avgNetPay,
        int maxNetPay,
        int minNetPay,
        int monthOverMonthRate,
        int ytdNetPay,
        List<PayHistoryChartPointDTO> chart,
        List<PayHistoryRowDTO> rows ) {}
