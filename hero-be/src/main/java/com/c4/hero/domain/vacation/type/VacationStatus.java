package com.c4.hero.domain.vacation.type;

/**
 * <pre>
 * Enum Name: VacationStatus
 * Description: 휴가 결재 상태를 표현하는 열거형
 *
 * History
 * 2025/12/16 (이지윤) 최초 작성 및 코딩 컨벤션 적용
 * </pre>
 *
 * 휴가 신청(VacationLog)에 대한 결재 진행 상태를 나타냅니다.
 * - PENDING  :  대기
 * - APPROVED :  승인
 * - REJECTED :  반려
 *
 * 서비스/레포지토리 계층에서 상태 필터링 시 사용합니다.
 * (예: DepartmentVacationRepository에서 APPROVED 상태만 조회)
 *
 * @author 이지윤
 * @version 1.0
 */
public enum VacationStatus {

    /** 대기 상태 */
    PENDING,

    /** 승인 상태 */
    APPROVED,

    /** 반려(거절) 상태 */
    REJECTED
}
