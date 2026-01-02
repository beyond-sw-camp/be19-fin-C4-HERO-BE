package com.c4.hero.domain.approval.dto.request;

import com.c4.hero.domain.approval.dto.ApprovalLineDTO;
import com.c4.hero.domain.approval.dto.ApprovalReferenceDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * Class Name  : ApprovalRequestDTO
 * Description : 결재 문서 작성/상신 요청 DTO
 *               새로운 결재 문서를 생성하거나 임시저장된 문서를 상신할 때 사용
 *
 * 주요 용도
 *   - 임시저장 (DRAFT)
 *   - 상신 (INPROGRESS)
 *   - 임시저장 문서 수정
 *
 * History
 *   2025/12/25 (민철) 최초 작성
 *   2025/12/26 (민철) 주석 추가 및 필드 설명 보완
 *   2026/01/01 (민철) 필드 주석을 JavaDoc 형식으로 개선
 * </pre>
 *
 * @author 민철
 * @version 1.2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalRequestDTO {

    /* ========================================== */
    /* 서식 정보 */
    /* ========================================== */

    /**
     * 서식 타입
     * 예: vacation(휴가), overtime(초과근무), resign(사직), personnel(인사발령), payroll(급여인상)
     */
    private String formType;

    /**
     * 문서 분류
     * 예: 근태, 인사, 급여
     */
    private String documentType;

    /* ========================================== */
    /* 문서 기본 정보 */
    /* ========================================== */

    /**
     * 문서 제목
     * 결재 문서의 제목
     */
    private String title;

    /**
     * 기안자 이름
     */
    private String drafter;

    /**
     * 기안자 부서
     */
    private String department;

    /**
     * 기안자 직급
     */
    private String grade;

    /**
     * 기안일
     * 형식: yyyy-MM-dd
     */
    private String draftDate;

    /**
     * 문서 상태
     * - DRAFT: 임시저장
     * - INPROGRESS: 진행중
     * - APPROVED: 승인완료
     * - REJECTED: 반려
     */
    private String status;

    /**
     * 상신일시
     * 형식: ISO 8601 (예: 2025-12-27T14:30:00)
     */
    private String submittedAt;

    /* ========================================== */
    /* 서식별 상세 데이터 */
    /* ========================================== */

    /**
     * 서식별 상세 데이터 (JSON String)
     * 각 서식 유형에 따라 다른 구조의 JSON 데이터를 저장
     *
     * 예시:
     * - 휴가신청서: {"vacationType":"annual","startDate":"2025-12-27","endDate":"2025-12-28","reason":"개인사유"}
     * - 초과근무신청서: {"workDate":"2025-12-27","startTime":"18:00","endTime":"22:00","reason":"프로젝트 마감"}
     * - 사직서: {"resignType":"개인사정","resignDate":"2025-12-31","reason":"이직"}
     */
    private String details;

    /* ========================================== */
    /* 결재선 & 참조자 */
    /* ========================================== */

    /**
     * 결재선 목록
     * seq 순서대로 결재가 진행됨 (seq=1: 기안, seq=2: 1차 결재, seq=3: 2차 결재 등)
     */
    private List<ApprovalLineDTO> lines;

    /**
     * 참조자 목록
     * 결재 프로세스에는 참여하지 않지만, 결재 완료 시 알림을 수신하는 직원 목록
     */
    private List<ApprovalReferenceDTO> references;
}