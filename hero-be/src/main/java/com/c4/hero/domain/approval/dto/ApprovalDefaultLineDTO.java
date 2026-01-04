package com.c4.hero.domain.approval.dto;

import lombok.*;

/**
 * <pre>
 * Class Name: ApprovalDefaultLineDTO
 * Description: 기안자별 자동 설정된 결재선 정보 DTO
 *              서식 작성 화면에서 기안자의 부서/직급에 따라 자동으로 계산된 결재자 정보를 전달
 *
 * History
 * 2025/12/25 (민철) 기안자별 결재자 정보
 * 2026/01/01 (민철) 필드 주석 추가
 *
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalDefaultLineDTO {

    /**
     * 결재자 ID
     */
    private Integer approverId;

    /**
     * 결재자 이름
     */
    private String approverName;

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

    /**
     * 결재 순서
     * - 1: 기안
     * - 2: 1차 결재
     * - 3: 2차 결재
     * - 4: 3차 결재 (최종 결재)
     */
    private Integer seq;
}