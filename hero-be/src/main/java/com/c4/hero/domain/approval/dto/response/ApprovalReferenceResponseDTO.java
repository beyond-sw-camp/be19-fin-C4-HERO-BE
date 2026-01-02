package com.c4.hero.domain.approval.dto.response;

import lombok.Data;

/**
 * <pre>
 * Class Name: ApprovalReferenceResponseDTO
 * Description: 참조자 응답 DTO
 *              문서의 참조자 정보 (결재에는 참여하지 않지만 알림을 받는 직원)
 *
 * History
 * 2025/12/26 (민철) 최초작성
 * 2026/01/01 (민철) 필드 주석 추가
 *
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */

@Data
public class ApprovalReferenceResponseDTO {

    /**
     * 참조자 ID (Primary Key)
     */
    private Integer referenceId;

    /**
     * 참조 직원 ID
     */
    private Integer referencerId;

    /**
     * 참조 직원 이름
     */
    private String referencerName;

    /**
     * 참조 직원 부서명
     */
    private String departmentName;

    /**
     * 참조 직원 직급
     */
    private String gradeName;

    /**
     * 참조 직원 직책
     */
    private String jobTitleName;
}