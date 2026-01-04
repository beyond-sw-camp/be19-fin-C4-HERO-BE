package com.c4.hero.domain.promotion.dto;

import com.c4.hero.domain.promotion.type.PromotionCandidateStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <pre>
 * Class Name: PromotionCandidateDTO
 * Description: 승진 후보자 정보를 담는 DTO
 *
 * History
 * 2025/12/19 (승건) 최초 작성
 * 2025/12/22 (승건) 승진 후보자 ID 추가
 * </pre>
 *
 * @author 승건
 * @version 1.1
 */

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PromotionCandidateDTO {
    private Integer candidateId;        // 승진 후보자 ID
    private Integer employeeId;         // 승진 후보자의 사원 ID
    private String employeeName;        // 승진 후보자 이름
    private String employeeNumber;      // 승진 후보자 사번
    private String department;          // 승진 후보자 부서
    private String grade;               // 승진 후보자 직급
    private String nominatorName;       // 추천인 이름
    private String nominationReason;    // 추천 사유
    private PromotionCandidateStatus status; // 승인 여부
    private String rejectionReason;     // 반려 사유
    private Integer evaluationPoint;    // 평가 포인트
}
