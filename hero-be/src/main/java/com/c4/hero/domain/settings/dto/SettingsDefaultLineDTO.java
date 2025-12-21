package com.c4.hero.domain.settings.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
/**
 * <pre>
 * Class Name: SettingsDefaultLineDTO
 * Description:
 *  - 결재 단계에 해당하는 결재자의 직책/부서
 *
 * History
 * 2025/12/ (민철) 최초작성
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingsDefaultLineDTO {

    private Integer seq;            // 결재 단계

    private String targetType;      // 결재자가 기안자의 부서이거나 문서 담당 부서일경우
                                    // DRAFTER_DEPT(기안자 부서)/SPECIFIC_DEPT(담당부서)

    private Integer departmentId;    // 결재자 부서(0이라면 상신자의 직속부서장, 담당부서가 존재할 경우 해당 부서)

    private Integer approverId;     // 특정인일 경우(확장 옵션)
}
