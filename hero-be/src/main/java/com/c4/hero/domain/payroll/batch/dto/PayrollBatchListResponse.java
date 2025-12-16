package com.c4.hero.domain.payroll.batch.dto;

import java.time.LocalDateTime;

/**
 * <pre>
 * DTO Name : PayrollBatchListResponse
 * Description : 급여 배치 목록 조회 응답 DTO
 *
 * History
 *  2025/12/15 - 동근 최초 작성
 * </pre>
 *
 *  @author 동근
 *  @version 1.0
 *
 * @param batchId    급여 배치 ID
 * @param salaryMonth 급여월 (YYYY-MM)
 * @param status     배치 상태 (READY / CALCULATED / CONFIRMED / PAID)
 * @param createdAt  배치 생성 시각
 * @param updatedAt  배치 상태 변경 시각
 * @param closedAt   배치 종료(지급 완료) 시각
 */
public record PayrollBatchListResponse(
        Integer batchId,
        String salaryMonth,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime closedAt
) {}
