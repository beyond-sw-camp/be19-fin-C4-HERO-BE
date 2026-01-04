package com.c4.hero.domain.payroll.report.dto;


/**
 * 급여 이력(12개월) 차트 Point DTO
 * <pre>
 * Class Name: PayHistoryChartPointDTO
 * Description: 월별 급여 실수령액 차트 시각화용 DTO
 *
 * History
 * 2025/12/08 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record PayHistoryChartPointDTO(
        String salaryMonth,
        int netPay ) {}
