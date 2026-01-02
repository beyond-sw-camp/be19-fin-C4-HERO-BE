package com.c4.hero.domain.approval.dto.response;

import lombok.*;

/**
 * <pre>
 * Class Name  : ApprovalTemplateResponseDTO
 * Description : 전자결재 문서 템플릿 정보를 전달하기 위한 DTO
 *
 * History
 * 2025/12/15 (민철) 최초 작성
 * 2025/12/19 (민철) ApprovalTemplate.java 문서 템플릿 필드명 수정에 따른 getter메서드 수정
 * 2025/12/25 (민철)
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalTemplateResponseDTO {

    private Integer templateId;
    private String templateName;
    private String templateKey;
    private String category;
    private String description;
    private boolean bookmarking;
}