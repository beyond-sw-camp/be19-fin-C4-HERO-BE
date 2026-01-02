package com.c4.hero.domain.retirement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * <pre>
 * Class Name: ForceRetirementRequestDTO
 * Description: 관리자 강제 퇴직 처리 요청 DTO
 *
 * History
 * 2025/12/31 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForceRetirementRequestDTO {

    @NotNull(message = "퇴사일은 필수입니다.")
    private LocalDate terminationDate;

    @NotNull(message = "퇴사 사유 ID는 필수입니다.")
    private Integer terminationReasonId;

    private String terminationReasonDetail;
}
