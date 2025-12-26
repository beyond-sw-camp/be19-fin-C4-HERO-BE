package com.c4.hero.domain.approval.dto;

import lombok.Data;
/**
 * <pre>
 * Class Name: ApprovalReferenceDTO
 * Description: 문서에 들어갈 참조 목록 별 참조자 정보
 *
 * History
 * 2025/12/25 (민철) 기안자별 참조자 정보
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
@Data
public class ApprovalReferenceDTO {
    private Integer userId;
    private String name;
}
