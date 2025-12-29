//package com.c4.hero.domain.payroll.batch.controller;
//
//import com.c4.hero.domain.payroll.batch.mapper.PayrollBatchQueryMapper;
//import com.c4.hero.domain.payroll.batch.service.PayrollBatchService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import java.util.List;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.ArgumentMatchers.isNull;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.verifyNoMoreInteractions;
//import static org.mockito.Mockito.when;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("급여 배치 컨트롤러 테스트 (순수 MockMvc)")
//class BatchControllerTest {
//
//    private MockMvc mockMvc;
//    private ObjectMapper objectMapper;
//
//    @Mock
//    private PayrollBatchService batchService;
//
//    @Mock
//    private PayrollBatchQueryMapper batchQueryMapper;
//
//    @BeforeEach
//    void setUp() {
//        BatchController controller = new BatchController(batchService, batchQueryMapper);
//
//        this.mockMvc = MockMvcBuilders
//                .standaloneSetup(controller)
//                .build();
//
//        this.objectMapper = new ObjectMapper();
//    }
//
//    @Test
//    @DisplayName("POST /api/admin/payroll/batches : 배치 생성 성공(200, batchId)")
//    void create_success() throws Exception {
//        // given (준비 단계)
//        when(batchService.createBatch("2025-12")).thenReturn(1);
//
//        // when & then (실행 & 검증 단계)
//        mockMvc.perform(post("/api/admin/payroll/batches")
//                        .param("month", "2025-12"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("1"));
//
//        verify(batchService).createBatch("2025-12");
//        verifyNoMoreInteractions(batchService, batchQueryMapper);
//    }
//
//    @Test
//    @DisplayName("POST /api/admin/payroll/batches/{batchId}/calculate : employeeIds 없이 호출 가능(바디 생략)")
//    void calculate_withoutBody_success() throws Exception {
//        // given (준비 단계)
//        doNothing().when(batchService).calculate(eq(1), isNull());
//
//        // when & then (실행 & 검증 단계)
//        mockMvc.perform(post("/api/admin/payroll/batches/1/calculate")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        verify(batchService).calculate(1, null);
//        verifyNoMoreInteractions(batchService, batchQueryMapper);
//    }
//
//    @Test
//    @DisplayName("POST /api/admin/payroll/batches/{batchId}/calculate : employeeIds 바디로 호출 가능")
//    void calculate_withBody_success() throws Exception {
//        // given (준비 단계)
//        List<Integer> ids = List.of(10, 11);
//        doNothing().when(batchService).calculate(eq(1), eq(ids));
//
//        // when & then (실행 & 검증 단계)
//        mockMvc.perform(post("/api/admin/payroll/batches/1/calculate")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(ids)))
//                .andExpect(status().isOk());
//
//        verify(batchService).calculate(1, ids);
//        verifyNoMoreInteractions(batchService, batchQueryMapper);
//    }
//
//    @Test
//    @DisplayName("POST /api/admin/payroll/batches/{batchId}/confirm : 확정 호출 성공")
//    void confirm_success() throws Exception {
//        // given (준비 단계 )
//        doNothing().when(batchService).confirm(1);
//
//        // when & then (실행 & 검증 단계)
//        mockMvc.perform(post("/api/admin/payroll/batches/1/confirm"))
//                .andExpect(status().isOk());
//
//        verify(batchService).confirm(1);
//        verifyNoMoreInteractions(batchService, batchQueryMapper);
//    }
//
//    @Test
//    @DisplayName("POST /api/admin/payroll/batches/{batchId}/pay : 지급 호출 성공")
//    void pay_success() throws Exception {
//        // given (준비 단계)
//        doNothing().when(batchService).pay(1);
//
//        // when & then (실행 & 검증 단계)
//        mockMvc.perform(post("/api/admin/payroll/batches/1/pay"))
//                .andExpect(status().isOk());
//
//        verify(batchService).pay(1);
//        verifyNoMoreInteractions(batchService, batchQueryMapper);
//    }
//}
