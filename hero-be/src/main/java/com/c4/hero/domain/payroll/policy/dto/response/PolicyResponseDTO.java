package com.c4.hero.domain.payroll.policy.dto.response;

import com.c4.hero.domain.payroll.common.type.PolicyStatus;

/**
 * <pre>
 * Class Name : PolicyResponse
 * Description : 급여 정책 조회 응답 DTO
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record PolicyResponseDTO(

        /** 급여 정책 식별자 */
        Integer policyId,

        /** 급여 정책명 */
        String policyName,

        /** 정책 상태 */
        PolicyStatus status,

        /** 적용 시작 급여월 (YYYY-MM) */
        String salaryMonthFrom,

        /** 적용 종료 급여월 (YYYY-MM, null이면 무기한) */
        String salaryMonthTo,

        /** 활성 여부 (Y/N) */
        String activeYn
) {}