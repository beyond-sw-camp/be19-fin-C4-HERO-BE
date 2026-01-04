package com.c4.hero.domain.approval.event;

import lombok.Getter;

/**
 * <pre>
 * Class Name: ApprovalRejectedEvent
 * Description: 결재 반려 완료 이벤트
 *              결재가 반려되었을 때 발행되는 도메인 이벤트
 *              다른 도메인에서 이 이벤트를 수신하여 후속 처리를 진행함
 *              (예: 알림 발송, 통계 집계 등)
 *
 * History
 *   2025/12/28 (승건) 최초 작성
 *   2026/01/01 (민철) 필드 주석 추가
 * </pre>
 *
 * @author 승건
 * @version 1.1
 */
@Getter
public class ApprovalRejectedEvent {

    /**
     * 문서 ID
     * 반려된 결재 문서의 고유 식별자
     */
    private final Integer docId;

    /**
     * 문서 서식 키
     * 어떤 유형의 결재 문서인지 구분
     * 예: vacation(휴가), overtime(초과근무), resign(사직), personnel(인사발령), payroll(급여인상)
     */
    private final String templateKey;

    /**
     * JSON 상세 데이터
     * 결재 문서의 서식별 상세 정보 (JSON 형식)
     */
    private final String details;

    /**
     * 기안자 ID
     * 결재 문서를 작성한 직원의 ID
     * 반려 알림을 받을 대상
     */
    private final Integer drafterId;

    /**
     * 반려 사유
     * 결재자가 작성한 반려 이유
     */
    private final String comment;

    /**
     * ApprovalRejectedEvent 생성자
     *
     * @param docId 문서 ID
     * @param templateKey 문서 서식 키
     * @param details JSON 상세 데이터
     * @param drafterId 기안자 ID
     * @param comment 반려 사유
     */
    public ApprovalRejectedEvent(
            Integer docId,
            String templateKey,
            String details,
            Integer drafterId,
            String comment
    ) {
        this.docId = docId;
        this.templateKey = templateKey;
        this.details = details;
        this.drafterId = drafterId;
        this.comment = comment;
    }
}