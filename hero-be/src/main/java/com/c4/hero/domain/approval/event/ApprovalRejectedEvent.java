package com.c4.hero.domain.approval.event;

import lombok.Getter;

/**
 * <pre>
 * Class Name: ApprovalRejectedEvent
 * Description: 결재 반려 완료 이벤트
 *
 * History
 *   2025/12/28 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Getter
public class ApprovalRejectedEvent {

    private final Integer docId;           // 문서 ID
    private final String templateKey;      // 문서 서식 키
    private final String details;          // JSON 상세 데이터
    private final Integer drafterId;       // 기안자 ID
    private final String comment;          // 반려 사유

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
