package com.c4.hero.domain.evaluation.dto.ai.analysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * Class Name: MemberAnalysisFastApiRequestDTO
 * Description: 스프링에서 파이썬으로 보내는 사원 분석 요청 데이터 DTO
 *
 * History
 * 2025/12/30 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberAnalysisFastApiRequestDTO {

    @JsonProperty("template_name")
    private String templateName;

    @JsonProperty("employee_name")
    private String employeeName;

    @JsonProperty("employee_department")
    private String employeeDepartment;

    @JsonProperty("employee_grade")
    private String employeeGrade;

    @JsonProperty("total_score")
    private Float totalScore;

    @JsonProperty("total_rank")
    private String totalRank;

    @JsonProperty("form_items")
    private List<AiFormItemFastApiDTO> formItems;
}
