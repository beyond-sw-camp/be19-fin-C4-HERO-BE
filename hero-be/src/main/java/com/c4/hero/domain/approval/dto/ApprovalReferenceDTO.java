package com.c4.hero.domain.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name  : ApprovalReferenceDTO
 * Description : 결재 참조자 DTO (요청/응답 공통)
 *
 * 주요 용도
 *   - 결재 문서 작성 시 참조자 정보 전달
 *   - 참조자 조회 시 참조자 정보 반환
 *
 * History
 *   2025/12/25 - 민철 최초 작성
 *   2025/12/26 - 민철 프론트엔드 타입과 일치하도록 필드 수정
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalReferenceDTO {

    // 기본 정보
    private Integer refId;            // 참조 ID (응답 시)
    private Integer referencerId;     // 참조자 사원 ID
    private String referencerName;    // 참조자 이름

    // 부서/직급 정보
    private Integer departmentId;     // 부서 ID
    private String departmentName;    // 부서명
    private String gradeName;         // 직급명
    private String jobTitleName;      // 직책명
}