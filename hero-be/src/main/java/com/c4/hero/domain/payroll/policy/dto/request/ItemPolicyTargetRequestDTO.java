package com.c4.hero.domain.payroll.policy.dto.request;

import com.c4.hero.domain.payroll.common.type.PayrollTargetType;

/**
 * <pre>
 * Class Name : ItemPolicyTargetRequest
 * Description : 급여 정책(Item Policy) 적용 대상 설정 요청 DTO
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record ItemPolicyTargetRequestDTO(

        /** 적용 대상 유형 (사원, 부서, 직급 등) */
        PayrollTargetType payrollTargetType,

        /** 대상 값 (사원 ID, 부서 코드, 직급 코드 등) */
        String targetValue
) {}
