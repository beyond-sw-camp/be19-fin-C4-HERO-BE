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
 *
 * 주요 용도
 *   - 임시저장 (DRAFT)
 *   - 상신 (PENDING)
 *
 * History
 *   2025/12/25 - 민철 최초 작성
 *   2025/12/26 - 민철 주석 추가 및 필드 설명 보완
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalRequestDTO {

    /* ========================================== */
    /* 서식 정보 */
    /* ========================================== */

    private String formType;          // 서식 타입 (vacation, overtime 등)
    private String documentType;      // 문서 분류 (근태, 인사 등)

    /* ========================================== */
    /* 문서 기본 정보 */
    /* ========================================== */

    private String title;             // 문서 제목
    private String drafter;           // 기안자 이름
    private String department;        // 기안자 부서
    private String grade;             // 기안자 직급
    private String draftDate;         // 기안일 (yyyy-MM-dd)
    private String status;            // 문서 상태 (draft, submitted)
    private String submittedAt;       // 상신일시 (ISO 8601)

    /* ========================================== */
    /* 서식별 상세 데이터 */
    /* ========================================== */

    /**
     * 서식별 상세 데이터 (JSON String)
     *
     * 예시:
     * - 휴가: {"vacationType":"annual","startDate":"2025-12-27","endDate":"2025-12-28"}
     * - 초과근무: {"workDate":"2025-12-27","startTime":"18:00","endTime":"22:00"}
     */
    private String details;

    /* ========================================== */
    /* 결재선 & 참조자 */
    /* ========================================== */

    /**
     * 결재선 목록
     * - seq 순서대로 결재 진행
     */
    private List<ApprovalLineDTO> lines;

    /**
     * 참조자 목록
     * - 결재 완료 시 알림 수신
     */
    private List<ApprovalReferenceDTO> references;
}