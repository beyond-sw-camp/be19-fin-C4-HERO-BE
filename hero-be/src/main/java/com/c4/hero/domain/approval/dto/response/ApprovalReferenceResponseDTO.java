package com.c4.hero.domain.approval.dto.response;

import lombok.Data;

/**
 * <pre>
 * Class Name: ApprovalReferenceResponseDTO
 * Description: 참조자 응답 DTO
 *
 * History
 * 2025/12/26 (민철) 최초작성
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */

@Data
public class ApprovalReferenceResponseDTO {
    private Integer referenceId;
    private Integer referencerId;
    private String referencerName;
    private String departmentName;
    private String gradeName;
    private String jobTitleName;
}