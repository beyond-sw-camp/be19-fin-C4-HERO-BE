package com.c4.hero.domain.approval.dto.request;

import lombok.Data;

/**
 * <pre>
 * Class Name: ApprovalActionRequestDTO
 * Description: 결재 처리 요청 DTO
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
public class ApprovalActionRequestDTO {
    private Integer docId;
    private Integer lineId;
    private String action;  // APPROVE, REJECT
    private String comment; // 반려 시 필수
}