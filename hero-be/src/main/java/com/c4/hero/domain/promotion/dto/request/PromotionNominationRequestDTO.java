package com.c4.hero.domain.promotion.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: PromotionNominationRequestDTO
 * Description: 승진 후보자 추천 요청 DTO
 *
 * History
 * 2025/12/22 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Getter
@NoArgsConstructor
public class PromotionNominationRequestDTO {

    @NotNull(message = "후보자 ID는 필수입니다.")
    private Integer candidateId;

    @NotBlank(message = "추천 사유는 필수입니다.")
    private String nominationReason;
}
