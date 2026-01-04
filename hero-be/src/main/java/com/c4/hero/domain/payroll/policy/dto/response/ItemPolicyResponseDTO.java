package com.c4.hero.domain.payroll.policy.dto.response;

import com.c4.hero.domain.payroll.common.type.BaseAmountType;
import com.c4.hero.domain.payroll.common.type.CalcMethod;
import com.c4.hero.domain.payroll.common.type.ItemType;
import com.c4.hero.domain.payroll.common.type.RoundingModeType;

import java.math.BigDecimal;

/**
 * <pre>
 * Class Name : ItemPolicyResponse
 * Description : 급여 정책(Item Policy) 항목 조회 응답 DTO
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record ItemPolicyResponseDTO(

        /** 아이템 정책 식별자 */
        Integer itemPolicyId,

        /** 소속 급여 정책 ID */
        Integer policyId,

        /** 항목 유형 (수당 / 공제 등) */
        ItemType itemType,

        /** 항목 코드 (예: OVERTIME, TAX, MEAL 등) */
        String itemCode,

        /** 계산 방식 (FIXED / RATE) */
        CalcMethod calcMethod,

        /** 고정 금액 (FIXED 방식일 때 사용) */
        Integer fixedAmount,

        /** 비율 값 (RATE 방식일 때 사용) */
        BigDecimal rate,

        /** 기준 금액 유형 (기본급, 실지급액 등) */
        BaseAmountType baseAmountType,

        /** 반올림 단위 */
        Integer roundingUnit,

        /** 반올림 방식 */
        RoundingModeType roundingMode,

        /** 적용 시작 급여월 (YYYY-MM) */
        String salaryMonthFrom,

        /** 적용 종료 급여월 (YYYY-MM, null이면 현재까지) */
        String salaryMonthTo,

        /** 우선순위 (계산 순서 결정용) */
        Integer priority,

        /** 활성 여부 (Y/N) */
        String activeYn
) {}
