package com.c4.hero.domain.payroll.batch.dto;

import java.time.LocalDateTime;

/**
 * <pre>
 * DTO Name : PayrollBatchDetailResponse
 * Description : 급여 배치 상세 조회 응답 DTO
 *
 * History
 *  2025/12/15 - 동근 최초 작성
 * </pre>
 *
 *  @author 동근
 *  @version 1.0
 *
 * @param batchId             급여 배치 ID
 * @param salaryMonth         급여월 (YYYY-MM)
 * @param status              배치 상태 (READY / CALCULATED / CONFIRMED / PAID)
 * @param createdAt           배치 생성 시각
 * @param updatedAt           배치 상태 변경 시각
 * @param closedAt            배치 종료(지급 완료) 시각
 * @param totalEmployeeCount  배치에 포함된 전체 사원 수
 * @param calculatedCount     급여 계산 완료(CALCULATED) 사원 수
 * @param failedCount         급여 계산 실패(FAILED) 사원 수
 * @param confirmedCount      급여 확정(CONFIRMED) 사원 수
 */
public record PayrollBatchDetailResponseDTO(
        Integer batchId,
        String salaryMonth,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime closedAt,
        Integer totalEmployeeCount,
        Integer calculatedCount,
        Integer failedCount,
        Integer confirmedCount
) {}
