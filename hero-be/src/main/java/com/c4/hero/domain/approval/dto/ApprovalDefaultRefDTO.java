package com.c4.hero.domain.approval.dto;

import lombok.*;

/**
 * <pre>
 * Class Name: ApprovalDefaultRefDTO
 * Description: 기안자별 자동 설정된 참조자 정보 DTO
 *              서식 작성 화면에서 서식 템플릿에 지정된 기본 참조자 정보를 전달
 *
 * History
 * 2025/12/25 (민철) 기안자별 참조자 정보
 * 2026/01/01 (민철) Class Name 오류 수정 및 필드 주석 추가
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
public class ApprovalDefaultRefDTO {

    /**
     * 참조자 ID
     */
    private Integer referencerId;

    /**
     * 참조자 이름
     */
    private String referencerName;

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