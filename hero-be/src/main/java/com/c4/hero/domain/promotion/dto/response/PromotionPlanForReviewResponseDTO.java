package com.c4.hero.domain.promotion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <pre>
 * Class Name: PromotionPlanForReviewResponseDTO
 * Description: 심사용 승진 계획 상세 조회를 위한 응답 DTO
 *
 * History
 * 2025/12/23 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PromotionPlanForReviewResponseDTO {
    private Integer promotionId;
    private String planName;                           // 승진 계획 제목
    private LocalDateTime createdAt;                    // 생성일
    private LocalDate nominationDeadlineAt;             // 추천 마감일
    private LocalDate appointmentAt;                    // 발령일
    private String planContent;                         // 계획 내용
    private List<PromotionDetailForReviewResponseDTO> detailPlan;    // 심사용 상세 계획 목록
}
