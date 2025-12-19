package com.c4.hero.domain.payroll.batch.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.domain.payroll.batch.entity.Payroll;
import com.c4.hero.domain.payroll.batch.entity.PayrollBatch;
import com.c4.hero.domain.payroll.batch.repository.PayrollItemRepository;
import com.c4.hero.domain.payroll.batch.repository.PayrollRepository;
import com.c4.hero.domain.payroll.integration.attendance.service.PayrollAttendanceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
@DisplayName("사원 단위 급여 계산 TX 서비스 테스트")
class PayrollEmployeeCalculateTxServiceTest {

    @Mock PayrollRepository payrollRepository;
    @Mock PayrollItemRepository payrollItemRepository;
    @Mock PayrollAttendanceService attendanceService;

    @InjectMocks PayrollEmployeeCalculateTxService txService;

    @Test
    @DisplayName("정상 계산: Payroll 저장 + 연장근무수당 항목 갱신")
    void calculateOne_success_saveCalculatedAndUpsertItem() {
        PayrollBatch batch = PayrollBatch.create("2025-12");
        PayrollBatch spyBatch = spy(batch);
        doReturn(1).when(spyBatch).getBatchId();

        when(attendanceService.getBaseSalary(10)).thenReturn(3_000_000);
        when(attendanceService.calculateOvertime("2025-12", 10)).thenReturn(120_000);

        when(payrollRepository.findByEmployeeIdAndSalaryMonth(10, "2025-12"))
                .thenReturn(Optional.empty());

        when(payrollRepository.save(any(Payroll.class))).thenAnswer(inv -> inv.getArgument(0));

        txService.calculateOne(spyBatch, 10);

        verify(payrollRepository).save(any(Payroll.class));
        verify(payrollItemRepository).deleteByPayrollIdAndItemTypeAndItemCode(anyInt(), eq("ALLOWANCE"), eq("OVERTIME"));
        verify(payrollItemRepository).save(any());
    }

    @Test
    @DisplayName("근태 계산 중 BusinessException 발생: FAILED로 저장한다(락 상태 아니면)")
    void calculateOne_businessException_saveFailed() {
        PayrollBatch batch = PayrollBatch.create("2025-12");
        PayrollBatch spyBatch = spy(batch);
        doReturn(1).when(spyBatch).getBatchId();

        when(attendanceService.getBaseSalary(11)).thenReturn(3_000_000);
        when(attendanceService.calculateOvertime("2025-12", 11))
                .thenThrow(mock(BusinessException.class));

        when(payrollRepository.findByEmployeeIdAndSalaryMonth(11, "2025-12"))
                .thenReturn(Optional.empty());

        when(payrollRepository.save(any(Payroll.class))).thenAnswer(inv -> inv.getArgument(0));

        txService.calculateOne(spyBatch, 11);

        verify(payrollRepository, atLeastOnce()).save(any(Payroll.class));
        verify(payrollItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("이미 CONFIRMED(락)된 Payroll이면 계산을 스킵한다")
    void calculateOne_lockedPayroll_skip() {
        // given(준비단계)
        PayrollBatch batch = PayrollBatch.create("2025-12");
        PayrollBatch spyBatch = spy(batch);
        doReturn(1).when(spyBatch).getBatchId();

        Payroll locked = Payroll.ready(10, 1, "2025-12");
        locked.lock(); // CONFIRMED

        when(payrollRepository.findByEmployeeIdAndSalaryMonth(10, "2025-12"))
                .thenReturn(Optional.of(locked));

        // when(실행단계)
        txService.calculateOne(spyBatch, 10);

        // then(검증단계)
        verify(attendanceService, never()).getBaseSalary(anyInt());
        verify(attendanceService, never()).calculateOvertime(anyString(), anyInt());
        verify(payrollRepository, never()).save(any());
        verify(payrollItemRepository, never()).save(any());
    }
}