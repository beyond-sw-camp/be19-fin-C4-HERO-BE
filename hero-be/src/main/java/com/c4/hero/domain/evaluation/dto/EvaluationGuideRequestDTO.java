package com.c4.hero.domain.evaluation.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: EvaluationGuideRequestDTO
 * Description: 클라이언트에서 오는 평기 가이드 요청 데이터 DTO
 *
 * History
 * 2025/12/10 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationGuideRequestDTO {

    private String evaluationGuideName;

    private String evaluationGuideContent;

    private LocalDateTime evaluationGuideCreatedAt;

    private Integer evaluationGuideEmployeeId;

    private Integer evaluationGuideDepartmentId;
}
