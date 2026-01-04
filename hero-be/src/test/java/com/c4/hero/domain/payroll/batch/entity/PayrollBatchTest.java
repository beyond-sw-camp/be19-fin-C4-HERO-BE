//package com.c4.hero.domain.payroll.batch.entity;
//
//import com.c4.hero.common.exception.BusinessException;
//import com.c4.hero.domain.payroll.common.type.PayrollBatchStatus;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//@DisplayName("PayrollBatch 도메인 규칙 테스트")
//class PayrollBatchTest {
//
//    @Test
//    @DisplayName("정상 상태 전이: READY -> CALCULATED -> CONFIRMED -> PAID")
//    void statusFlow_success() {
//        // given (준비 단계)
//        PayrollBatch batch = PayrollBatch.create("2025-12");
//        assertThat(batch.getStatus()).isEqualTo(PayrollBatchStatus.READY);
//
//        // when (실행 단계)
//        batch.markCalculated();
//        batch.confirm();
//        batch.markPaid();
//
//         then (검증 단계)
//        assertThat(batch.getStatus()).isEqualTo(PayrollBatchStatus.PAID);
//        assertThat(batch.getClosedAt()).isNotNull();
//    }
//
//    @Test
//    @DisplayName("잘못된 전이: READY에서 confirm 하면 예외")
//    void confirm_whenNotCalculated_throw() {
//        // given (준비 단계)
//        PayrollBatch batch = PayrollBatch.create("2025-12");
//
//        // when & then (실행 & 검증 단계)
//        assertThatThrownBy(batch::confirm)
//                .isInstanceOf(BusinessException.class);
//    }
//
//    @Test
//    @DisplayName("잘못된 전이: READY에서 paid 처리하면 예외")
//    void markPaid_whenNotConfirmed_throw() {
//        // given (준비 단계)
//        PayrollBatch batch = PayrollBatch.create("2025-12");
//
//        // when & then (실행 & 검증 단계)
//        assertThatThrownBy(batch::markPaid)
//                .isInstanceOf(BusinessException.class);
//    }
//}