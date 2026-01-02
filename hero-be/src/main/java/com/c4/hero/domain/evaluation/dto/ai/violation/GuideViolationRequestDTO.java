package com.c4.hero.domain.evaluation.dto.ai.violation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * <pre>
 * Class Name: GuideViolationRequestDTO
 * Description: 클라이언트에서 오는 평가 가이드 위반 분석 요청 데이터 DTO
 *
 * History
 * 2025/12/31 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuideViolationRequestDTO {

    private String guide;

    private Map<String, Object> template;
}
