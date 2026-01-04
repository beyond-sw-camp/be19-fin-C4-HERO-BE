package com.c4.hero.domain.promotion.dto.request;

import com.c4.hero.domain.promotion.dto.PromotionDetailPlanDTO;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

/**
 * <pre>
 * Class Name: PromotionPlanRequestDTO
 * Description: 승진 계획 등록을 위한 요청 DTO
 *
 * History
 * 2025-12-19 (이승건) 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PromotionPlanRequestDTO {

    /**
     * 승진 계획명
     */
    @NotBlank(message = "계획명은 필수입니다.")
    private String planName;

    /**
     * 추천 마감일
     */
    @NotNull(message = "추천 마감일은 필수입니다.")
    @Future(message = "추천 마감일은 현재보다 미래여야 합니다.")
    private LocalDate nominationDeadlineAt;

    /**
     * 발령일
     */
    @NotNull(message = "발령일은 필수입니다.")
    @Future(message = "발령일은 현재보다 미래여야 합니다.")
    private LocalDate appointmentAt;

    /**
     * 상세 계획 목록
     */
    @NotEmpty(message = "상세 계획은 최소 1개 이상 포함되어야 합니다.")
    private List<PromotionDetailPlanDTO> detailPlan;

    /**
     * 계획 관련 추가 내용
     */
    private String planContent;
}
