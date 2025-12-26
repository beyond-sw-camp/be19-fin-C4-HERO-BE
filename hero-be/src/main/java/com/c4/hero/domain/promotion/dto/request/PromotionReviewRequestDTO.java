package com.c4.hero.domain.promotion.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 * Class Name: PromotionReviewRequestDTO
 * Description: 승진 후보자 심사 요청 DTO
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
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PromotionReviewRequestDTO {

    @NotNull(message = "후보자 ID는 필수입니다.")
    private Integer candidateId;

    @NotNull(message = "승인 여부는 필수입니다.")
    private Boolean isPassed;

    private String comment;
}
