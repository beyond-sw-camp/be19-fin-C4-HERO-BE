package com.c4.hero.domain.payroll.report.dto;

import java.util.List;


/**
 * 급여 이력 페이지 응답 DTO
 * 급여 이력 화면에서 사용하는 상단 요약 카드 + 차트 데이터 + 테이블 표 데이터를 모두 포함
 * <pre>
 * Class Name: PayHistoryResponseDTO
 * Description: 사원의 급여 이력 통계 및 상세 목록 응답 DTO
 *
 * History
 * 2025/12/08 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record PayHistoryResponseDTO(
        int avgNetPay,
        int maxNetPay,
        int minNetPay,
        int monthOverMonthRate,
        int ytdNetPay,
        List<PayHistoryChartPointDTO> chart,
        List<PayHistoryRowDTO> rows ) {}
