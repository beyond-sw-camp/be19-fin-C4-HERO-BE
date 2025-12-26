package com.c4.hero.domain.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name  : ApprovalLineDTO
 * Description : 결재선 DTO (요청/응답 공통)
 *
 * 주요 용도
 *   - 결재 문서 작성 시 결재자 정보 전달
 *   - 결재선 조회 시 결재자 정보 반환
 *
 * History
 *   2025/12/25 - 민철 최초 작성
 *   2025/12/26 - 민철 프론트엔드 타입과 일치하도록 필드 수정
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalLineDTO {

    // 기본 정보
    private Integer lineId;           // 결재선 ID (응답 시)
    private Integer approverId;       // 결재자 사원 ID
    private String approverName;      // 결재자 이름

    // 부서/직급 정보
    private Integer departmentId;     // 부서 ID
    private String departmentName;    // 부서명
    private String gradeName;         // 직급명
    private String jobTitleName;      // 직책명

    // 결재 정보
    private Integer seq;              // 결재 순서
    private String lineStatus;        // 결재 상태 (PENDING/APPROVED/REJECTED)
    private String comment;           // 결재 의견
    private String processDate;       // 처리 일시
}