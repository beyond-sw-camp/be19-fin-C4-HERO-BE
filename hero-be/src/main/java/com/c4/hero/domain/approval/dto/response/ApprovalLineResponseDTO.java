package com.c4.hero.domain.approval.dto.response;

import lombok.Data;

/**
 * <pre>
 * Class Name: ApprovalLineResponseDTO
 * Description: 결재선 응답 DTO
 *              문서의 결재선 정보 (결재자 정보 및 결재 상태)
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
public class ApprovalLineResponseDTO {

    /**
     * 결재선 ID (Primary Key)
     */
    private Integer lineId;

    /**
     * 결재자 ID
     */
    private Integer approverId;

    /**
     * 결재자 이름
     */
    private String approverName;

    /**
     * 결재자 부서명
     */
    private String departmentName;

    /**
     * 결재자 직급
     */
    private String gradeName;

    /**
     * 결재자 직책
     */
    private String jobTitleName;

    /**
     * 결재 순서
     * - 1: 기안
     * - 2: 1차 결재
     * - 3: 2차 결재
     * - 4: 3차 결재 (최종 결재)
     */
    private Integer seq;

    /**
     * 결재 상태
     * - PENDING: 대기중
     * - APPROVED: 승인
     * - REJECTED: 반려
     */
    private String status;

    /**
     * 결재일시
     * 형식: yyyy-MM-dd HH:mm:ss
     */
    private String approvedAt;

    /**
     * 결재 의견
     * 승인 또는 반려 시 작성한 코멘트
     */
    private String comment;
}