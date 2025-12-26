package com.c4.hero.domain.approval.dto;

import lombok.*;

/**
 * <pre>
 * Class Name: ApprovalDefaultLineDTO
 * Description: 실제 기안자(사용자)별 결재자 정보
 *
 * History
 * 2025/12/25 (민철) 기안자별 결재자 정보
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalDefaultRefDTO {

    private Integer referencerId;
    private String referencerName;
    private Integer departmentId;
    private String departmentName;
    private String gradeName;
    private String jobTitleName;
}
