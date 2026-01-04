package com.c4.hero.domain.evaluation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: EmployeeEvaluationListResponseDTO
 * Description: 특정 직원의 평가 결과 목록 조회 응답 DTO
 *
 * History
 * 2025/01/02 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEvaluationListResponseDTO {

    private Integer evaluationId;
    private String evaluationName;
    private String totalRank;
    private LocalDateTime createdAt;
}
