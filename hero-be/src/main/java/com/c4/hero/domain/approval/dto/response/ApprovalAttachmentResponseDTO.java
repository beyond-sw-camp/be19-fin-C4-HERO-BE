package com.c4.hero.domain.approval.dto.response;

import lombok.Data;

/**
 * <pre>
 * Class Name: ApprovalAttachmentResponseDTO
 * Description: 첨부파일 응답 DTO
 *
 * History
 * 2025/12/26 (민철) 최초작성
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */

@Data
public class ApprovalAttachmentResponseDTO {
    private Integer attachmentId;
    private String originalFilename;
    private String storedFilename;
    private Long fileSize;
    private String fileUrl;
    private String uploadedAt;
}