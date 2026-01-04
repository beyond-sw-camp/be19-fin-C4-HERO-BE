package com.c4.hero.domain.approval.event;

import lombok.Getter;

/**
 * <pre>
 * Class Name: ApprovalCompletedEvent
 * Description: 결재 최종 승인 완료 이벤트
 *              결재가 최종 승인되었을 때 발행되는 도메인 이벤트
 *              다른 도메인에서 이 이벤트를 수신하여 후속 처리를 진행함
 *              (예: 휴가 승인 → 휴가 데이터 생성, 초과근무 승인 → 근태 데이터 반영)
 *
 * History
 *   2025/12/26 (민철) 최초 작성
 *   2026/01/01 (민철) 필드 주석 추가
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */
@Getter
public class ApprovalCompletedEvent {

    /**
     * 문서 ID
     * 승인 완료된 결재 문서의 고유 식별자
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
     * 이벤트 리스너에서 이 데이터를 파싱하여 후속 처리에 활용
     */
    private final String details;

    /**
     * 기안자 ID
     * 결재 문서를 작성한 직원의 ID
     */
    private final Integer drafterId;

    /**
     * 문서 제목
     */
    private final String title;

    /**
     * ApprovalCompletedEvent 생성자
     *
     * @param docId 문서 ID
     * @param templateKey 문서 서식 키
     * @param details JSON 상세 데이터
     * @param drafterId 기안자 ID
     * @param title 문서 제목
     */
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