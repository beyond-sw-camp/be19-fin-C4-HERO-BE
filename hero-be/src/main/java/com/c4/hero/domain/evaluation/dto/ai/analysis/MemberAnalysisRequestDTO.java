package com.c4.hero.domain.evaluation.dto.ai.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * Class Name: MemberAnalysisRequestDTO
 * Description: 클라이언트에서 오는 사원 분석 요청 데이터 DTO
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
public class MemberAnalysisRequestDTO {

    private String templateName;

    private String employeeName;

    private String employeeDepartment;

    private String employeeGrade;

    private Float totalScore;

    private String totalRank;

    private List<AiFormItemDTO> formItems;
}
