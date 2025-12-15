package com.c4.hero.domain.evaluation.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * Class Name: FormItemRequestDTO
 * Description: 클라이언트에서 오는 평가서 항목 데이터 DTO
 *
 * History
 * 2025/12/14 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormItemRequestDTO {

    private Integer formItemFormId;

    @NotNull(message = "생성할 평가서의 선택된 항목 ID는 필수 입니다.")
    private Integer formItemSelectedItemId;

    @NotNull(message = "생성할 평가서의 선택된 항목 가중치는 필수 입니다.")
    private Float formItemWeight;

    @NotBlank(message = "생성할 평가의 내용은 필수 입니다.")
    private String formItemDescription;

    private ItemScoreRequestDTO itemScore;
}
