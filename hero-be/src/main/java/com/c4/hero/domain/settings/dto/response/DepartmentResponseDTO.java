package com.c4.hero.domain.settings.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: DepartmentResponseDTO
 * Description: 결재 관리 탭 부서목록
 *
 * History
 * 2025/12/21 (민철) 부서 목록 조회, ID/부서명
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
public class DepartmentResponseDTO {
    private Integer departmentId;
    private String departmentName;
}
