package com.c4.hero.domain.approval.dto.response;

import lombok.Data;

/**
 * <pre>
 * Class Name: ApprovalAttachmentResponseDTO
 * Description: 첨부파일 응답 DTO
 *
 * History
 * 2025/12/26 (민철) 최초작성
 * 2026/01/01 (민철) downloadUrl 필드 추가
 *
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */

@Data
public class ApprovalAttachmentResponseDTO {
    private Integer attachmentId;
    private String originalFilename;
    private String storedFilename;
    private Long fileSize;
    private String fileUrl;
    private String uploadedAt;
    private String downloadUrl;  // S3 Presigned URL (7일 유효)
}