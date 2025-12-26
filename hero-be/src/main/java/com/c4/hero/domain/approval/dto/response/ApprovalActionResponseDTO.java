package com.c4.hero.domain.approval.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: ApprovalActionResponseDTO
 * Description: 결재 처리 응답 DTO
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalActionResponseDTO {
    private boolean success;
    private String message;
    private String docStatus;
}