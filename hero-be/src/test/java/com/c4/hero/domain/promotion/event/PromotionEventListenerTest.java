//package com.c4.hero.domain.promotion.event;
//
//import com.c4.hero.domain.approval.event.ApprovalCompletedEvent;
//import com.c4.hero.domain.approval.event.ApprovalRejectedEvent;
//import com.c4.hero.domain.promotion.dto.request.PromotionReviewRequestDTO;
//import com.c4.hero.domain.promotion.service.PromotionCommandService;
//import tools.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
///**
// * <pre>
// * Class Name: PromotionEventListenerTest
// * Description: 승진 이벤트 리스너 테스트
// *
// * History
// *   2025/12/29 - (승건) 최초 작성
// * </pre>
// *
// * @author 승건
// * @version 1.0
// */
//@ExtendWith(MockitoExtension.class)
//@DisplayName("승진 이벤트 리스너 테스트")
//class PromotionEventListenerTest {
//
//    @Mock
//    private PromotionCommandService promotionCommandService;
//
//    // @InjectMocks 대신 수동으로 주입하여 실제 ObjectMapper를 사용
//    private PersonnelAppointmentEventListener promotionEventListener;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @BeforeEach
//    void setUp() {
//        // SUT(System Under Test) 수동 생성 및 의존성 주입
//        promotionEventListener = new PersonnelAppointmentEventListener(promotionCommandService, objectMapper);
//    }
//
//    @Test
//    @DisplayName("[성공] 정기 승진 결재 완료 시, confirmFinalApproval 호출")
//    void handleRegularPromotionApproval() {
//        // Given: 정기 승진 결재 완료 이벤트
//        String details = "{\"promotionType\":\"REGULAR\",\"candidateId\":123}";
//        ApprovalCompletedEvent event = new ApprovalCompletedEvent(1, "personnelappointment", details, 11, "정기승진");
//
//        // When: 이벤트 수신
//        promotionEventListener.handleApprovalCompleted(event);
//
//        // Then: confirmFinalApproval 메서드 호출 검증
//        ArgumentCaptor<PromotionReviewRequestDTO> captor = ArgumentCaptor.forClass(PromotionReviewRequestDTO.class);
//        verify(promotionCommandService, times(1)).confirmFinalApproval(captor.capture());
//
//        // Then: 파라미터 검증
//        PromotionReviewRequestDTO capturedDto = captor.getValue();
//        assertThat(capturedDto.getCandidateId()).isEqualTo(123);
//        assertThat(capturedDto.getIsPassed()).isTrue();
//    }
//
//    @Test
//    @DisplayName("[성공] 특별 승진 결재 완료 시, confirmDirectPromotion 호출")
//    void handleSpecialPromotionApproval() {
//        // Given: 특별 승진 결재 완료 이벤트
//        String details = "{\"promotionType\":\"SPECIAL\",\"employeeId\":456,\"targetGradeId\":5,\"reason\":\"탁월한 성과\"}";
//        ApprovalCompletedEvent event = new ApprovalCompletedEvent(2, "personnelappointment", details, 11, "특별승진");
//
//        // When: 이벤트 수신
//        promotionEventListener.handleApprovalCompleted(event);
//
//        // Then: confirmDirectPromotion 메서드 호출 검증
//        ArgumentCaptor<Integer> empIdCaptor = ArgumentCaptor.forClass(Integer.class);
//        ArgumentCaptor<Integer> gradeIdCaptor = ArgumentCaptor.forClass(Integer.class);
//        ArgumentCaptor<String> reasonCaptor = ArgumentCaptor.forClass(String.class);
//
//        verify(promotionCommandService, times(1)).confirmDirectPromotion(
//                empIdCaptor.capture(),
//                gradeIdCaptor.capture(),
//                reasonCaptor.capture()
//        );
//
//        // Then: 파라미터 검증
//        assertThat(empIdCaptor.getValue()).isEqualTo(456);
//        assertThat(gradeIdCaptor.getValue()).isEqualTo(5);
//        assertThat(reasonCaptor.getValue()).isEqualTo("탁월한 성과");
//    }
//
//    @Test
//    @DisplayName("[성공] 정기 승진 결재 반려 시, confirmFinalApproval(false) 호출")
//    void handleRegularPromotionRejection() {
//        // Given: 정기 승진 결재 반려 이벤트
//        String details = "{\"promotionType\":\"REGULAR\",\"candidateId\":123}";
//        String rejectionComment = "시기상조";
//        ApprovalRejectedEvent event = new ApprovalRejectedEvent(3, "personnelappointment", details, 11, rejectionComment);
//
//        // When: 이벤트 수신
//        promotionEventListener.handleApprovalRejected(event);
//
//        // Then: confirmFinalApproval 메서드 호출 검증
//        ArgumentCaptor<PromotionReviewRequestDTO> captor = ArgumentCaptor.forClass(PromotionReviewRequestDTO.class);
//        verify(promotionCommandService, times(1)).confirmFinalApproval(captor.capture());
//
//        // Then: 파라미터 검증
//        PromotionReviewRequestDTO capturedDto = captor.getValue();
//        assertThat(capturedDto.getCandidateId()).isEqualTo(123);
//        assertThat(capturedDto.getIsPassed()).isFalse();
//        assertThat(capturedDto.getComment()).isEqualTo(rejectionComment);
//    }
//
//    @Test
//    @DisplayName("[무시] 특별 승진 결재 반려 시, 아무 동작도 하지 않음")
//    void handleSpecialPromotionRejection() {
//        // Given: 특별 승진 결재 반려 이벤트
//        String details = "{\"promotionType\":\"SPECIAL\",\"employeeId\":456}";
//        ApprovalRejectedEvent event = new ApprovalRejectedEvent(4, "personnelappointment", details, 11, "반려");
//
//        // When: 이벤트 수신
//        promotionEventListener.handleApprovalRejected(event);
//
//        // Then: PromotionCommandService의 어떤 메서드도 호출되지 않음
//        verify(promotionCommandService, never()).confirmFinalApproval(any());
//        verify(promotionCommandService, never()).confirmDirectPromotion(any(), any(), any());
//    }
//}