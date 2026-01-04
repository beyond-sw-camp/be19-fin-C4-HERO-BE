package com.c4.hero.domain.evaluation.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: DashBoardCriteriaResponseDTO
 * Description: 대시보드 그래프 출력을 위한 평가 정보 응답 데이터 DTO
 *
 * History
 * 2025/12/17 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashBoardCriteriaResponseDTO {

    private Integer criteriaId;

    private String criteriaRank;

    private String criteriaDescription;

    private Integer criteriaMinScore;

    private Integer criteriaMaxScore;
}
