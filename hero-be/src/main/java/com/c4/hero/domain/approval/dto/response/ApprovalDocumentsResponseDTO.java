package com.c4.hero.domain.approval.dto.response;

import lombok.Data;
/**
 * <pre>
 * Class Name: ApprovalDocumentsResponseDTO
 * Description: 문서함 내 문서들 목록 조회
 *
 * History
 * 2025/12/17 (민철) 최초작성
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */

@Data
public class ApprovalDocumentsResponseDTO {
    private Integer docId;
    private String docNo;
    private String docStatus;
    private String category;
    private String name;
    private String title;
    private String drafterDept;
    private String drafter;
    private String drafterAt;
}
