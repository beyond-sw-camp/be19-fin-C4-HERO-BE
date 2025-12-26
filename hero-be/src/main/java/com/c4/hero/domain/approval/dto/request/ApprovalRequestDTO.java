package com.c4.hero.domain.approval.dto.request;

import com.c4.hero.domain.approval.dto.ApprovalLineDTO;
import com.c4.hero.domain.approval.dto.ApprovalReferenceDTO;
import lombok.Data;
import java.util.List;

@Data
public class ApprovalRequestDTO {
    private String formType;      // vacation, overtime 등
    private String documentType;  // 근태, 인사 등
    private String title;
    private String drafter;
    private String department;
    private String grade;
    private String draftDate;
    private String status;        // draft, submitted

    // 프론트에서 JSON.stringify로 보냈으므로 String으로 받음
    private String details;

    private List<ApprovalLineDTO> approvalLine;
    private List<ApprovalReferenceDTO> references;
}