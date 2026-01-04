package com.c4.hero.domain.retirement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: DepartmentTurnoverDTO
 * Description: 부서별 이직률 통계 정보를 전달하는 DTO 클래스
 *
 * History
 * 2025/12/30 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentTurnoverDTO {

    /** 부서명 */
    private String departmentName;

    /** 현재 인원 수 */
    private Long currentCount;

    /** 퇴사 인원 수 */
    private Long retiredCount;

    /** 이직률 (%) */
    private double turnoverRate;
}
