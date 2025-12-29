package com.c4.hero.domain.payroll.policy.dto.response;

import com.c4.hero.domain.payroll.common.type.PayrollTargetType;

/**
 * <pre>
 * Class Name : ItemPolicyTargetResponse
 * Description : 급여 항목 정책(Item Policy) 적용 대상 조회 응답 DTO
 *
 * 용도
 *  - 항목 정책에 설정된 적용 대상 정보를 조회할 때 사용
 *  - 사원/부서/직급 등 정책 적용 범위를 프론트로 전달
 *
 * 특징
 *  - ItemPolicyTargetRequest와 짝을 이루는 조회 전용 응답 객체
 *  - 대상 타입과 값을 단순히 표현하며, 계산 로직에는 관여하지 않음
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record ItemPolicyTargetResponseDTO(

        /** 적용 대상 유형 (사원 / 부서 / 직급 등) */
        PayrollTargetType payrollTargetType,

        /** 대상 값 (사원 ID, 부서 코드, 직급 코드 등) */
        String targetValue
) {}
