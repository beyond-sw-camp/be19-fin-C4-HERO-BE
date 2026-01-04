package com.c4.hero.domain.payroll.policy.dto.request;

/**
 * <pre>
 * Class Name : PolicyCopyRequest
 * Description : 급여 정책 복사 요청 DTO
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record PolicyCopyRequestDTO(
        /** 신규 정책명 (null이면 원본명 기반 기본값 사용) */
        String policyName,

        /** 적용 시작 급여월 (null이면 원본 값 유지) */
        String salaryMonthFrom,

        /** 적용 종료 급여월 (null이면 원본 값 유지) */
        String salaryMonthTo
) {}