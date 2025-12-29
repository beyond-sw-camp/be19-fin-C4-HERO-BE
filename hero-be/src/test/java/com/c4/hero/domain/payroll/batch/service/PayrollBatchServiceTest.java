//package com.c4.hero.domain.payroll.batch.service;
//
//import com.c4.hero.common.exception.BusinessException;
//import com.c4.hero.domain.payroll.batch.dto.PayrollBatchTargetEmployeeResponseDTO;
//import com.c4.hero.domain.payroll.batch.entity.PayrollBatch;
//import com.c4.hero.domain.payroll.batch.mapper.PayrollBatchQueryMapper;
//import com.c4.hero.domain.payroll.batch.repository.BatchRepository;
//import com.c4.hero.domain.payroll.batch.repository.PayrollRepository;
//import com.c4.hero.domain.payroll.common.type.PayrollStatus;
//import com.c4.hero.domain.payroll.payment.repository.PaymentHistoryRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.ArgumentMatchers.anyList;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("급여 배치 서비스 테스트")
//class PayrollBatchServiceTest {
//
//    @Mock
//    BatchRepository batchRepository;
//    @Mock PayrollCalculationService calculationService;
//    @Mock PayrollBatchStatusTxService batchStatusTxService;
//    @Mock
//    PayrollRepository payrollRepository;
//    @Mock
//    PayrollBatchQueryMapper batchQueryMapper;
//    @Mock
//    PaymentHistoryRepository paymentHistoryRepository;
//
//    @InjectMocks
//    PayrollBatchService payrollBatchService;
//
//    @Test
//    @DisplayName("배치 생성: 동일 월이 이미 존재하면 예외 발생")
//    void createBatch_duplicate_throw() {
//        // given(준비단계)
//        when(batchRepository.existsBySalaryMonth("2025-12")).thenReturn(true);
//
//        // when & then(실행&검증단계)
//        assertThatThrownBy(() -> payrollBatchService.createBatch("2025-12"))
//                .isInstanceOf(BusinessException.class);
//
//        verify(batchRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("배치 계산: employeeIds가 null이면 targets 조회 후 계산 실행 + READY면 CALCULATED로 전이")
//    void calculate_whenEmployeeIdsNull_thenUseTargetsAndMarkCalculated() {
//        // given
//        PayrollBatch batch = PayrollBatch.create("2025-12"); // READY
//
//        when(batchRepository.findById(1)).thenReturn(Optional.of(batch));
//        when(batchQueryMapper.selectBatchTargetEmployees()).thenReturn(List.of(
//                new PayrollBatchTargetEmployeeResponseDTO(10, "DEV", "사원A"),
//                new PayrollBatchTargetEmployeeResponseDTO(11, "DEV", "사원B")
//        ));
//
//        // when
//        payrollBatchService.calculate(1, null);
//
//        // then
//        verify(calculationService).calculateEmployees(eq(batch), eq(List.of(10, 11)));
//        verify(batchStatusTxService).markCalculatedInNewTx(1);
//    }
//
//    @Test
//    @DisplayName("배치 계산: employeeIds가 비어있으면 targets 조회 후, targets도 비어있으면 예외")
//    void calculate_targetsEmpty_throw() {
//        // given
//        PayrollBatch batch = PayrollBatch.create("2025-12");
//
//        when(batchRepository.findById(1)).thenReturn(Optional.of(batch));
//        when(batchQueryMapper.selectBatchTargetEmployees())
//                .thenReturn(List.of());
//
//        // when & then
//        assertThatThrownBy(() -> payrollBatchService.calculate(1, List.of()))
//                .isInstanceOf(BusinessException.class);
//
//        verify(calculationService, never()).calculateEmployees(any(), anyList());
//        verify(batchStatusTxService, never()).markCalculatedInNewTx(anyInt());
//    }
//
//    @Test
//    @DisplayName("배치 계산: CONFIRMED 상태면 계산 요청 거부")
//    void calculate_lockedBatch_throw() {
//        // given
//        PayrollBatch batch = PayrollBatch.create("2025-12");
//        batch.markCalculated();
//        batch.confirm();
//
//        when(batchRepository.findById(1)).thenReturn(Optional.of(batch));
//
//        // when & then
//        assertThatThrownBy(() -> payrollBatchService.calculate(1, List.of(10)))
//                .isInstanceOf(BusinessException.class);
//
//        verify(calculationService, never()).calculateEmployees(any(), anyList());
//    }
//
//    @Test
//    @DisplayName("배치 확정: FAILED 급여가 존재하면 예외")
//    void confirm_hasFailed_throw() {
//        // given
//        PayrollBatch batch = PayrollBatch.create("2025-12");
//
//        when(batchRepository.findById(1)).thenReturn(Optional.of(batch));
//        when(payrollRepository.existsByBatchIdAndStatus(1, PayrollStatus.FAILED))
//                .thenReturn(true);
//
//        // when & then
//        assertThatThrownBy(() -> payrollBatchService.confirm(1))
//                .isInstanceOf(BusinessException.class);
//
//        // FAILED 있으면 lock 걸면 안됨
//        // 실행 시 오류가 발생 아래 코드로 임시로 고쳤습니다.(2025-12-26 easy가 올림)
////        verify(payrollRepository, never()).updateStatusByBatchId(anyInt());
//        verify(payrollRepository, never())
//                .updateStatusByBatchId(anyInt(), any(PayrollStatus.class));
//    }
//
//    @Test
//    @DisplayName("지급 처리: CONFIRMED 상태가 아니면 예외")
//    void pay_notConfirmed_throw() {
//        // given
//        PayrollBatch batch = PayrollBatch.create("2025-12"); // READY 상태
//
//        when(batchRepository.findById(1)).thenReturn(Optional.of(batch));
//
//        // when & then
//        assertThatThrownBy(() -> payrollBatchService.pay(1))
//                .isInstanceOf(BusinessException.class);
//
//        verify(paymentHistoryRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("지급 처리: payroll이 비어있으면 예외")
//    void pay_emptyPayrolls_throw() {
//        // given
//        PayrollBatch batch = PayrollBatch.create("2025-12");
//        batch.markCalculated();
//        batch.confirm();
//
//        when(batchRepository.findById(1)).thenReturn(Optional.of(batch));
//        when(payrollRepository.existsByBatchIdAndStatus(1, PayrollStatus.FAILED))
//                .thenReturn(false);
//        when(payrollRepository.findAllByBatchId(1))
//                .thenReturn(List.of());
//
//        // when & then
//        assertThatThrownBy(() -> payrollBatchService.pay(1))
//                .isInstanceOf(BusinessException.class);
//
//        verify(paymentHistoryRepository, never()).save(any());
//    }
//}
