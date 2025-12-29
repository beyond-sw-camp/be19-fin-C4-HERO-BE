package com.c4.hero.domain.payroll.policy.dto.request;

/**
 * <pre>
 * Class Name : PolicyUpdateRequest
 * Description : 급여 정책 기본 정보 수정 요청 DTO
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record PolicyUpdateRequestDTO(

        /** 정책명 */
        String policyName,

        /** 적용 시작 급여월 (YYYY-MM) */
        String salaryMonthFrom,

        /** 적용 종료 급여월 (YYYY-MM, null이면 무기한) */
        String salaryMonthTo,

        /** 활성 여부 (Y/N) */
        String activeYn
) {}
