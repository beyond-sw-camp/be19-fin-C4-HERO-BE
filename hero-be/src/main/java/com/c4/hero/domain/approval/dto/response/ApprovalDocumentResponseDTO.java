package com.c4.hero.domain.approval.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name  : ApprovalDocumentResponseDTO
 * Description : 결재 문서 생성/상신 응답 DTO
 *
 * 주요 용도
 *   - 임시저장 완료 후 응답
 *   - 상신 완료 후 응답
 *
 * History
 *   2025/12/26 - 민철 최초 작성
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalDocumentResponseDTO {
    
    private Integer documentId;       // 생성된 문서 ID
    private String documentNumber;    // 문서 번호 (상신 시 생성)
    private String title;             // 문서 제목
    private String status;            // 문서 상태 (DRAFT/PENDING/APPROVED/REJECTED)
    private String createdAt;         // 생성 일시
    private String submittedAt;       // 상신 일시
    private String message;           // 응답 메시지
}