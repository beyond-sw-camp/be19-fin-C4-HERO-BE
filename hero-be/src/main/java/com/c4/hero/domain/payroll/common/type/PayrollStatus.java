package com.c4.hero.domain.payroll.common.type;

/**
 * <pre>
 * Enum Name : PayrollStatus
 * Description : 급여 처리 상태를 나타내는 Enum
 *
 * 상태 흐름
 *  READY  -> CALCULATED -> CONFIRMED
 *                or
 *              FAILED
 *
 * 상태 설명
 *  - READY
 *    · 급여 배치에 포함되었으나 아직 계산되지 않은 상태
 *
 *  - CALCULATED
 *    · 급여 계산이 정상적으로 완료된 상태
 *    · 급여 항목(기본급, 수당, 공제)이 모두 산출됨
 *
 *  - FAILED
 *    · 급여 계산 중 오류가 발생한 상태
 *    · 근태 누락, 계산 불가 데이터 등으로 처리 중단
 *
 *  - CONFIRMED
 *    · 급여가 최종 확정된 상태
 *    · 확정 이후에는 수정/재계산 불가 (Lock 상태)
 *
 * History
 *  2025/12/15 - 동근 최초 작성
 * </pre>
 *
 *  @author 동근
 *  @version 1.0
 */
public enum PayrollStatus {
    /**
     * 급여 계산 대기 상태
     */
    READY,

    /**
     * 급여 계산 완료 상태
     */
    CALCULATED,

    /**
     * 급여 계산 실패 상태
     */
    FAILED,

    /**
     * 급여 확정 완료 상태 (수정 불가)
     */
    CONFIRMED
}
