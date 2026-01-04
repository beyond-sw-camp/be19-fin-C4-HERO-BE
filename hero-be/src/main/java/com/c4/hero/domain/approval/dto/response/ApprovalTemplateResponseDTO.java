package com.c4.hero.domain.approval.dto.response;

import lombok.*;

/**
 * <pre>
 * Class Name  : ApprovalTemplateResponseDTO
 * Description : 전자결재 문서 템플릿 정보를 전달하기 위한 DTO
 *               서식 목록 조회 시 사용 (즐겨찾기 정보 포함)
 *
 * History
 * 2025/12/15 (민철) 최초 작성
 * 2025/12/19 (민철) ApprovalTemplate.java 문서 템플릿 필드명 수정에 따른 getter메서드 수정
 * 2026/01/01 (민철) 필드 주석 추가
 * </pre>
 *
 * @author 민철
 * @version 1.2
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalTemplateResponseDTO {

    /**
     * 서식 템플릿 ID
     */
    private Integer templateId;

    /**
     * 서식 템플릿명
     * 예: 휴가신청서, 초과근무신청서, 사직서
     */
    private String templateName;

    /**
     * 서식 템플릿 키
     * 예: vacation, overtime, resign, personnel, payroll
     */
    private String templateKey;

    /**
     * 문서 분류
     * 예: 근태, 인사, 급여
     */
    private String category;

    /**
     * 서식 설명
     * 서식 사용 방법 또는 용도에 대한 설명
     */
    private String description;

    /**
     * 즐겨찾기 여부
     * 현재 로그인한 사용자가 이 서식을 즐겨찾기에 추가했는지 여부
     */
    private boolean bookmarking;
}