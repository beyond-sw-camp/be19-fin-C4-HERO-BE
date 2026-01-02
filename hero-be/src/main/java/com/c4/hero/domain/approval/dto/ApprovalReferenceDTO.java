package com.c4.hero.domain.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name  : ApprovalReferenceDTO
 * Description : 결재 참조자 DTO (요청/응답 공통)
 *               결재 문서 작성 시 참조자 정보를 전달하고, 조회 시 참조자 정보를 반환
 *
 * 주요 용도
 *   - 결재 문서 작성 시 참조자 정보 전달
 *   - 참조자 조회 시 참조자 정보 반환
 *
 * History
 *   2025/12/25 (민철) 최초 작성
 *   2025/12/26 (민철) 프론트엔드 타입과 일치하도록 필드 수정
 *   2026/01/01 (민철) 필드 주석 추가
 * </pre>
 *
 * @author 민철
 * @version 1.2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalReferenceDTO {

    /* ========================================== */
    /* 기본 정보 */
    /* ========================================== */

    /**
     * 참조 ID (Primary Key)
     * 응답 시에만 사용 (요청 시에는 null)
     */
    private Integer refId;

    /**
     * 참조자 사원 ID
     */
    private Integer referencerId;

    /**
     * 참조자 이름
     */
    private String referencerName;

    /* ========================================== */
    /* 부서/직급 정보 */
    /* ========================================== */

    /**
     * 참조자 부서 ID
     */
    private Integer departmentId;

    /**
     * 참조자 부서명
     */
    private String departmentName;

    /**
     * 참조자 직급
     */
    private String gradeName;

    /**
     * 참조자 직책
     */
    private String jobTitleName;
}