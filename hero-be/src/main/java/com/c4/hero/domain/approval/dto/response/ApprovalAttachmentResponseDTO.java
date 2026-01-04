package com.c4.hero.domain.approval.dto.response;

import lombok.Data;

/**
 * <pre>
 * Class Name: ApprovalAttachmentResponseDTO
 * Description: 첨부파일 응답 DTO
 *              문서 상세 조회 시 첨부파일 정보를 클라이언트에 전달
 *
 * History
 * 2025/12/26 (민철) 최초작성
 * 2026/01/01 (민철) downloadUrl 필드 추가
 * 2026/01/01 (민철) 필드 주석 추가
 *
 * </pre>
 *
 * @author 민철
 * @version 1.2
 */

@Data
public class ApprovalAttachmentResponseDTO {

    /**
     * 첨부파일 ID (Primary Key)
     */
    private Integer attachmentId;

    /**
     * 원본 파일명
     * 사용자가 업로드한 파일의 원래 이름
     */
    private String originalFilename;

    /**
     * 저장된 파일명
     * S3에 저장된 파일의 고유 이름
     */
    private String storedFilename;

    /**
     * 파일 크기 (Bytes)
     */
    private Long fileSize;

    /**
     * 파일 URL
     * @deprecated downloadUrl 사용 권장
     */
    private String fileUrl;

    /**
     * 업로드 일시
     * 형식: yyyy-MM-dd HH:mm:ss
     */
    private String uploadedAt;

    /**
     * 다운로드 URL (S3 Presigned URL)
     * 유효 기간: 7일
     * 이 URL을 통해 첨부파일을 다운로드할 수 있음
     */
    private String downloadUrl;
}