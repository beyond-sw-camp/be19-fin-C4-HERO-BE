package com.c4.hero.domain.approval.dto.response;

import com.c4.hero.domain.approval.dto.ApprovalDefaultLineDTO;
import com.c4.hero.domain.approval.dto.ApprovalDefaultRefDTO;
import lombok.*;

import java.util.List;
/**
 * <pre>
 * Class Name: ApprovalTemplateDetailResponseDTO
 * Description: 사용자가 상세 작성 화면으로 이동할 때 서식정보(서식/분류) 및 기본 자동 결재선/참조목록
 *
 * History
 * 2025/12/24 (민철) 서식정보 및 자동결재선/참조목록
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalTemplateDetailResponseDTO {
    private Integer templateId;
    private String templateKey;
    private String templateName;
    private String category;
    private List<ApprovalDefaultLineDTO> lines;
    private List<ApprovalDefaultRefDTO> references;
}
