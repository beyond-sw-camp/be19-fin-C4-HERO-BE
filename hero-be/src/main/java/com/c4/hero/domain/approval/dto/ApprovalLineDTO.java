package com.c4.hero.domain.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name  : ApprovalLineDTO
 * Description : 결재선 DTO (요청/응답 공통)
 *               결재 문서 작성 시 결재자 정보를 전달하고, 조회 시 결재 상태를 반환
 *
 * 주요 용도
 *   - 결재 문서 작성 시 결재자 정보 전달
 *   - 결재선 조회 시 결재자 정보 및 결재 상태 반환
 *
 * History
 *   2025/12/25 (민철) 최초 작성
 *   2025/12/26 (민철) 프론트엔드 타입과 일치하도록 필드 수정
 *   2026/01/01 (민철) 필드 주석 추가
 * </pre>
 *
 * @author 민철
 * @version 1.2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalLineDTO {

    /* ========================================== */
    /* 기본 정보 */
    /* ========================================== */

    /**
     * 결재선 ID (Primary Key)
     * 응답 시에만 사용 (요청 시에는 null)
     */
    private Integer lineId;

    /**
     * 결재자 사원 ID
     */
    private Integer approverId;

    /**
     * 결재자 이름
     */
    private String approverName;

    /* ========================================== */
    /* 부서/직급 정보 */
    /* ========================================== */

    /**
     * 결재자 부서 ID
     */
    private Integer departmentId;

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

    /* ========================================== */
    /* 결재 정보 */
    /* ========================================== */

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
    private String lineStatus;

    /**
     * 결재 의견
     * 승인 또는 반려 시 작성한 코멘트
     */
    private String comment;

    /**
     * 처리 일시
     * 결재를 승인하거나 반려한 일시
     * 형식: yyyy-MM-dd HH:mm:ss
     */
    private String processDate;
}