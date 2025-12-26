package com.c4.hero.domain.approval.dto;

import lombok.Data;
/**
 * <pre>
 * Class Name: ApprovalLineDTO
 * Description: 문서에 들어갈 결재선 단계별 결재자 정보
 *
 * History
 * 2025/12/25 (민철) 기안자별 결재자 정보
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
@Data
public class ApprovalLineDTO {
    private Integer lineId;
    private Integer approverId;
    private String approverName;
    private String role;
    private int seq;
}
