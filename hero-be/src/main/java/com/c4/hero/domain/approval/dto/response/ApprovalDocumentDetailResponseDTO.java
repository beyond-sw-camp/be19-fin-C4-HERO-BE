package com.c4.hero.domain.approval.dto.response;

import lombok.Data;
import java.util.List;

/**
 * <pre>
 * Class Name: ApprovalDocumentDetailResponseDTO
 * Description: 문서 상세 조회 응답 DTO
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
public class ApprovalDocumentDetailResponseDTO {
    private Integer docId;
    private String docNo;
    private String docStatus;
    private Integer templateId;
    private String templateName;
    private String templateKey;
    private String category;
    private String title;
    private Integer drafterId;
    private String drafter;
    private String drafterDept;
    private String drafterGrade;
    private String draftDate;
    private String submittedAt;
    private String completedAt;
    private String details;
    private List<ApprovalLineResponseDTO> lines;
    private List<ApprovalReferenceResponseDTO> references;
    private List<ApprovalAttachmentResponseDTO> attachments;
}