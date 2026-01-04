package com.c4.hero.domain.promotion.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <pre>
 * Enum Name: PromotionCandidateStatus
 * Description: 승진 후보자의 진행 상태를 관리하는 열거형
 *
 * History
 * 2025/12/22 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum PromotionCandidateStatus {
    WAITING("대기"),
    REJECTED("반려"),
    REVIEW_PASSED("심사 통과"),
    FINAL_APPROVED("최종 승인");

    private final String description;
}
