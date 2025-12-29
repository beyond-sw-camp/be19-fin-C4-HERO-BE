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
 * Class Name: DirectPromotionRequestDTO
 * Description: 즉시 승진(특별 승진) 요청 DTO
 *
 * History
 * 2025/12/28 (승건) 최초 작성
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
public class DirectPromotionRequestDTO {

    @NotNull(message = "대상 직원 ID는 필수입니다.")
    private Integer employeeId;

    @NotNull(message = "변경할 직급 ID는 필수입니다.")
    private Integer targetGradeId;

    @NotBlank(message = "승진 사유는 필수입니다.")
    private String reason;
}
