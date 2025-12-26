package com.c4.hero.domain.approval.dto.response;

import lombok.Data;

/**
 * <pre>
 * Class Name: ApprovalLineResponseDTO
 * Description: 결재선 응답 DTO
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
public class ApprovalLineResponseDTO {
    private Integer lineId;
    private Integer approverId;
    private String approverName;
    private String departmentName;
    private String gradeName;
    private String jobTitleName;
    private Integer seq;
    private String status;
    private String approvedAt;
    private String comment;
}