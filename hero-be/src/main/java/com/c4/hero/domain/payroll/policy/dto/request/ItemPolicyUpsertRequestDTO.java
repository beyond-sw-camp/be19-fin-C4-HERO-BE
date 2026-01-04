package com.c4.hero.domain.payroll.policy.dto.request;

import com.c4.hero.domain.payroll.common.type.BaseAmountType;
import com.c4.hero.domain.payroll.common.type.CalcMethod;
import com.c4.hero.domain.payroll.common.type.ItemType;
import com.c4.hero.domain.payroll.common.type.PayrollTargetType;
import com.c4.hero.domain.payroll.common.type.RoundingModeType;

import java.math.BigDecimal;
import java.util.List;

/**
 * <pre>
 * Class Name : ItemPolicyUpsertRequest
 * Description : 급여 정책(Item Policy) 항목 생성/수정 요청 DTO
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record ItemPolicyUpsertRequestDTO(

        /** 수정 시 사용되는 항목 정책 ID (신규 생성 시 null) */
        Integer itemPolicyId,

        /** 항목 유형 (수당 / 공제) */
        ItemType itemType,

        /** 항목 코드 (예: OVERTIME, TAX, MEAL 등) */
        String itemCode,

        /** 계산 방식 (FIXED / RATE / FORMULA) */
        CalcMethod calcMethod,

        /** 고정 금액 (FIXED 방식에서 사용) */
        Integer fixedAmount,

        /** 비율 값 (RATE 방식에서 사용) */
        BigDecimal rate,

        /** 기준 금액 유형 (기본급, 과세대상 금액 등) */
        BaseAmountType baseAmountType,

        /** 반올림 단위 (1, 10, 100 등) */
        Integer roundingUnit,

        /** 반올림 방식 */
        RoundingModeType roundingMode,

        /** 적용 시작 급여월 (YYYY-MM, 필수) */
        String salaryMonthFrom,

        /** 적용 종료 급여월 (YYYY-MM, null이면 현재까지 적용) */
        String salaryMonthTo,

        /** 계산 우선순위 (기본값: 0) */
        Integer priority,

        /** 활성 여부 (Y/N, 기본값: Y) */
        String activeYn,

        /** 항목 정책 적용 대상 목록 */
        List<ItemPolicyTargetRequest> targets
) {

    public record ItemPolicyTargetRequest(
            PayrollTargetType payrollTargetType,
            String targetValue
    ) {}
}
//public record ItemPolicyUpsertRequest(
//        /** 항목 유형 (수당 / 공제 등) */
//        ItemType itemType,
//
//        /** 항목 코드 (예: OVERTIME, TAX, MEAL 등) */
//        String itemCode,
//
//        /** 계산 방식 (FIXED / RATE) */
//        CalcMethod calcMethod,
//
//        /** 고정 금액 (FIXED 방식일 때 사용) */
//        Integer fixedAmount,
//
//        /** 비율 값 (RATE 방식일 때 사용) */
//        BigDecimal rate,
//
//        /** 기준 금액 유형 (기본급, 실지급액 등) */
//        BaseAmountType baseAmountType,
//
//        /** 반올림 단위 */
//        Integer roundingUnit,
//
//        /** 반올림 방식 */
//        RoundingModeType roundingMode,
//
//        /** 적용 시작 급여월 (YYYY-MM, 필수) */
//        String salaryMonthFrom,
//
//        /** 적용 종료 급여월 (YYYY-MM, null이면 현재까지 적용) */
//        String salaryMonthTo,
//
//        /** 계산 우선순위 (기본값: 0) */
//        Integer priority,
//
//        /** 활성 여부 (기본값: Y) */
//        String activeYn
//) {}