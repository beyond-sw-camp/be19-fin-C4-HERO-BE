package com.c4.hero.domain.approval.dto.response;

import lombok.Data;

/**
 * <pre>
 * Class Name: ApprovalDocumentsResponseDTO
 * Description: 문서함 내 문서 목록 조회 응답 DTO
 *              문서함에서 문서 목록을 표시할 때 사용하는 요약 정보
 *
 * History
 * 2025/12/17 (민철) 최초작성
 * 2026/01/01 (민철) 필드 주석 추가
 *
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */

@Data
public class ApprovalDocumentsResponseDTO {

    /**
     * 문서 ID (Primary Key)
     */
    private Integer docId;

    /**
     * 문서 번호
     * 예: DOC-2025-001
     */
    private String docNo;

    /**
     * 문서 상태
     * - 진행중: INPROGRESS
     * - 승인완료: APPROVED
     * - 반려: REJECTED
     * - 임시저장: DRAFT
     */
    private String docStatus;

    /**
     * 문서 분류
     * 예: 근태, 인사, 급여
     */
    private String category;

    /**
     * 문서 서식명
     * 예: 휴가신청서, 초과근무신청서
     */
    private String name;

    /**
     * 문서 제목
     */
    private String title;

    /**
     * 기안자 부서
     */
    private String drafterDept;

    /**
     * 기안자 이름
     */
    private String drafter;

    /**
     * 기안일시
     * 형식: yyyy년 MM월 dd일
     */
    private String drafterAt;
}