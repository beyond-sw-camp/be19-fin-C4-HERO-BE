package com.c4.hero.domain.payroll.common.type;

/**
 * <pre>
 * Enum Name : PayrollBatchStatus
 * Description : 급여 배치 처리 상태를 나타내는 Enum
 *
 * 배치 상태 흐름
 *  READY -> CALCULATED -> CONFIRMED -> PAID
 *
 * 상태 설명
 *  - READY
 *    -> 급여 배치가 생성된 초기 상태
 *    -> 급여 계산 전 단계
 *
 *  - CALCULATED
 *    -> 배치에 포함된 사원들의 급여 계산이 완료된 상태
 *    -> 일부 사원이 FAILED 상태일 수 있음
 *
 *  - CONFIRMED
 *    -> 배치에 포함된 급여가 최종 확정된 상태
 *    -> 이후 급여 데이터 수정 및 재계산 불가 (Lock)
 *
 *  - PAID
 *    -> 급여 지급 처리가 완료된 상태
 *    -> 지급 이력(tbl_payment_history) 생성 완료
 *
 * History
 *  2025/12/15 - 동근 최초 작성
 * </pre>
 *
 *  @author 동근
 *  @version 1.0
 */
public enum PayrollBatchStatus {

    /**
     * 급여 배치 생성 완료 (계산 전)
     */
    READY,

    /**
     * 급여 계산 완료 상태
     */
    CALCULATED,

    /**
     * 급여 확정 완료 상태 (수정 불가)
     */
    CONFIRMED,

    /**
     * 급여 지급 완료 상태
     */
    PAID
}
