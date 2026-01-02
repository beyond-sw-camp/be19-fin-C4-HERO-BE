package com.c4.hero.domain.approval.dto.request;

import lombok.Data;

/**
 * <pre>
 * Class Name: ApprovalActionRequestDTO
 * Description: 결재 처리 요청 DTO
 *              결재자가 문서를 승인하거나 반려할 때 사용하는 DTO
 *
 * History
 * 2025/12/26 (민철) 최초작성
 * 2026/01/01 (민철) 필드 주석 개선
 *
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */

@Data
public class ApprovalActionRequestDTO {

    /**
     * 문서 ID
     * 처리할 결재 문서의 고유 식별자
     */
    private Integer docId;

    /**
     * 결재선 ID
     * 현재 결재자의 결재선 항목 ID
     */
    private Integer lineId;

    /**
     * 결재 처리 액션
     * - APPROVE: 승인
     * - REJECT: 반려
     */
    private String action;

    /**
     * 결재 의견 (코멘트)
     * 반려 시 필수 입력 항목
     */
    private String comment;
}