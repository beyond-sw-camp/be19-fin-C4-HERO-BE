package com.c4.hero.domain.approval.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: ApprovalActionResponseDTO
 * Description: 결재 처리 응답 DTO
 *              결재 승인/반려 처리 후 결과를 클라이언트에 전달
 *
 * History
 * 2025/12/26 (민철) 최초작성
 * 2026/01/01 (민철) 필드 주석 추가
 *
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalActionResponseDTO {

    /**
     * 처리 성공 여부
     */
    private boolean success;

    /**
     * 처리 결과 메시지
     * 예: "결재가 승인되었습니다", "문서가 반려되었습니다"
     */
    private String message;

    /**
     * 문서 상태
     * - INPROGRESS: 진행중 (다음 결재자 대기)
     * - APPROVED: 승인완료 (최종 승인 완료)
     * - REJECTED: 반려
     */
    private String docStatus;

    /**
     * 문서 번호
     * 예: DOC-2025-001
     */
    private String docNo;
}