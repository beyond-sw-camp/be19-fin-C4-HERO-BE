package com.c4.hero.domain.payroll.adjustment.event;

import com.c4.hero.domain.approval.event.ApprovalCompletedEvent;
import com.c4.hero.domain.payroll.adjustment.service.PayrollAdjustmentCommandService;
import com.c4.hero.domain.payroll.adjustment.service.PayrollRaiseCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * <pre>
 * Class Name: PayrollApprovalEventListenerTest
 * Description: 급여(조정/인상) 결재 이벤트 리스너 테스트
 *
 * History
 *   2025/12/31 - 동근 테스트 추가
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("급여 결재 이벤트 리스너 테스트")
class PayrollApprovalEventListenerTest {

    @Mock
    private PayrollAdjustmentCommandService adjustmentService;

    @Mock
    private PayrollRaiseCommandService raiseService;

    @InjectMocks
    private PayrollApprovalEventListener listener;

    @Test
    @DisplayName("payrolladjustment 서식 승인 시 조정 insert 서비스 호출")
    void testAdjustmentAppliedWhenApprovalCompleted() throws Exception {
        // Given
        String details = "{\"payrollId\":101,\"reason\":\"급여 누락분 정정\",\"sign\":\"+\",\"amount\":50000,\"effectiveMonth\":\"2026-01\"}";

        ApprovalCompletedEvent event = new ApprovalCompletedEvent(
                1,
                "payrolladjustment",
                details,
                11,
                "급여 조정 신청서"
        );

        // When
        listener.handleApprovalCompleted(event);

        // Then
        ArgumentCaptor<Integer> docIdCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> createdByCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> payrollIdCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> reasonCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> signCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> amountCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> monthCaptor = ArgumentCaptor.forClass(String.class);

        verify(adjustmentService, times(1)).applyApprovedAdjustment(
                docIdCaptor.capture(),
                createdByCaptor.capture(),
                payrollIdCaptor.capture(),
                reasonCaptor.capture(),
                signCaptor.capture(),
                amountCaptor.capture(),
                monthCaptor.capture()
        );

        assertThat(docIdCaptor.getValue()).isEqualTo(1);
        assertThat(createdByCaptor.getValue()).isEqualTo(11);
        assertThat(payrollIdCaptor.getValue()).isEqualTo(101);
        assertThat(reasonCaptor.getValue()).isEqualTo("급여 누락분 정정");
        assertThat(signCaptor.getValue()).isEqualTo("+");
        assertThat(amountCaptor.getValue()).isEqualTo(50000);
        assertThat(monthCaptor.getValue()).isEqualTo("2026-01");

        verifyNoInteractions(raiseService);
    }

    @Test
    @DisplayName("payrollraise 서식 승인 시 인상 insert 서비스 호출")
    void testRaiseAppliedWhenApprovalCompleted() throws Exception {
        // Given
        String details = "{\"employeeId\":77,\"reason\":\"연봉 조정\",\"beforeSalary\":3000000,\"afterSalary\":3300000,\"effectiveMonth\":\"2026-01\"}";

        ApprovalCompletedEvent event = new ApprovalCompletedEvent(
                2,
                "payrollraise",
                details,
                11,
                "급여 인상 신청서"
        );

        // When
        listener.handleApprovalCompleted(event);

        // Then
        ArgumentCaptor<Integer> docIdCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> requestedByCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> employeeIdCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> beforeCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> afterCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> reasonCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Double> raisePercentCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<String> monthCaptor = ArgumentCaptor.forClass(String.class);

        verify(raiseService, times(1)).applyApprovedRaise(
                docIdCaptor.capture(),
                requestedByCaptor.capture(),
                employeeIdCaptor.capture(),
                beforeCaptor.capture(),
                afterCaptor.capture(),
                reasonCaptor.capture(),
                raisePercentCaptor.capture(),
                monthCaptor.capture()
        );

        assertThat(docIdCaptor.getValue()).isEqualTo(2);
        assertThat(requestedByCaptor.getValue()).isEqualTo(11);
        assertThat(employeeIdCaptor.getValue()).isEqualTo(77);
        assertThat(beforeCaptor.getValue()).isEqualTo(3000000);
        assertThat(afterCaptor.getValue()).isEqualTo(3300000);
        assertThat(reasonCaptor.getValue()).isEqualTo("연봉 조정");
        assertThat(monthCaptor.getValue()).isEqualTo("2026-01");

        verifyNoInteractions(adjustmentService);
    }

    @Test
    @DisplayName("다른 서식 승인 시 아무 것도 호출되지 않음")
    void testNoopForOtherTemplates() {
        // Given
        String details = "{\"foo\":\"bar\"}";
        ApprovalCompletedEvent event = new ApprovalCompletedEvent(
                3,
                "vacation",
                details,
                11,
                "휴가신청서"
        );

        // When
        listener.handleApprovalCompleted(event);

        // Then
        verifyNoInteractions(adjustmentService, raiseService);
    }

    @Test
    @DisplayName("effectiveMonth 누락 시 자동으로 익월(yyyy-MM) 세팅되어 호출됨")
    void testEffectiveMonthDefaultedToNextMonthWhenMissing() throws Exception {
        String details = "{\"employeeId\":77,\"reason\":\"연봉 조정\",\"beforeSalary\":3000000,\"afterSalary\":3300000}";

        ApprovalCompletedEvent event = new ApprovalCompletedEvent(
                4,
                "payrollraise",
                details,
                11,
                "급여 인상 신청서"
        );

        // When
        listener.handleApprovalCompleted(event);

        // Then: 마지막 파라미터(effectiveMonth)만 캡처해서 yyyy-MM 형식 검증
        ArgumentCaptor<String> monthCaptor = ArgumentCaptor.forClass(String.class);

        verify(raiseService, times(1)).applyApprovedRaise(
                anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyString(),nullable(Double.class) ,monthCaptor.capture()
        );

        assertThat(monthCaptor.getValue()).matches("\\d{4}-\\d{2}");
    }
}
