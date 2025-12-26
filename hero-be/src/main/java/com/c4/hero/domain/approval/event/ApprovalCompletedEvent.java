package com.c4.hero.domain.approval.event;

import lombok.Getter;

/**
 * <pre>
 * Class Name: ApprovalCompletedEvent
 * Description: 결재 최종 승인 완료 이벤트
 * 
 * 다른 도메인에서 이 이벤트를 수신하여 후속 처리를 진행함
 * 
 * History
 *   2025/12/26 - 민철 최초 작성
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
@Getter
public class ApprovalCompletedEvent {
    
    private final Integer docId;           // 문서 ID
    private final String templateKey;      // 문서 서식 키 (vacation, overtime, resign 등)
    private final String details;          // JSON 상세 데이터
    private final Integer drafterId;       // 기안자 ID
    private final String title;            // 문서 제목
    
    public ApprovalCompletedEvent(
            Integer docId,
            String templateKey,
            String details,
            Integer drafterId,
            String title
    ) {
        this.docId = docId;
        this.templateKey = templateKey;
        this.details = details;
        this.drafterId = drafterId;
        this.title = title;
    }
}