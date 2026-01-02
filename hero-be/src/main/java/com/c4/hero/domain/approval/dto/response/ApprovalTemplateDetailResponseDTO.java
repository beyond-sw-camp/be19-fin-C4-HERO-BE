package com.c4.hero.domain.approval.dto.response;

import com.c4.hero.domain.approval.dto.ApprovalDefaultLineDTO;
import com.c4.hero.domain.approval.dto.ApprovalDefaultRefDTO;
import lombok.*;

import java.util.List;

/**
 * <pre>
 * Class Name: ApprovalTemplateDetailResponseDTO
 * Description: 서식 상세 조회 응답 DTO
 *              문서 작성 화면에서 필요한 서식 정보 및 자동 설정되는 결재선/참조자 목록
 *
 * History
 * 2025/12/24 (민철) 서식정보 및 자동결재선/참조목록
 * 2026/01/01 (민철) 필드 주석 추가
 *
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalTemplateDetailResponseDTO {

    /**
     * 서식 템플릿 ID
     */
    private Integer templateId;

    /**
     * 서식 템플릿 키
     * 예: vacation, overtime, resign, personnel, payroll
     */
    private String templateKey;

    /**
     * 서식 템플릿명
     * 예: 휴가신청서, 초과근무신청서, 사직서
     */
    private String templateName;

    /**
     * 문서 분류
     * 예: 근태, 인사, 급여
     */
    private String category;

    /**
     * 자동 지정된 결재선 목록
     * 기안자의 부서/직급에 따라 자동으로 계산된 결재선
     */
    private List<ApprovalDefaultLineDTO> lines;

    /**
     * 자동 지정된 참조자 목록
     * 서식 템플릿에 설정된 기본 참조자
     */
    private List<ApprovalDefaultRefDTO> references;
}