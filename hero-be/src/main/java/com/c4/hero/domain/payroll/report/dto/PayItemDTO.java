package com.c4.hero.domain.payroll.report.dto;


/**
 * 급여 항목 DTO (수당/공제 공용)
 * 내 급여/명세서/급여 이력에서 각 항목(수당, 공제)을 표현할 때 사용
 * <pre>
 * Class Name: PayItemDTO
 * Description: 급여 수당/공제 단위 항목 응답 DTO
 *
 * History
 * 2025/12/08 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record PayItemDTO(
        String name,
        int amount
) {}
