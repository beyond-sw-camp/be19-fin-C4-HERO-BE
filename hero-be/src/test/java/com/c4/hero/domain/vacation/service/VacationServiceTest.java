//package com.c4.hero.domain.vacation.service;
//
//import com.c4.hero.common.response.PageResponse;
//import com.c4.hero.domain.employee.entity.Employee;
//import com.c4.hero.domain.employee.entity.EmployeeDepartment;
//import com.c4.hero.domain.employee.repository.EmployeeRepository;
//import com.c4.hero.domain.vacation.dto.DepartmentVacationDTO;
//import com.c4.hero.domain.vacation.dto.VacationHistoryDTO;
//import com.c4.hero.domain.vacation.dto.VacationSummaryDTO;
//import com.c4.hero.domain.vacation.entity.VacationLog;
//import com.c4.hero.domain.vacation.entity.VacationType;
//import com.c4.hero.domain.vacation.repository.DepartmentVacationRepository;
//import com.c4.hero.domain.vacation.repository.VacationRepository;
//import com.c4.hero.domain.vacation.repository.VacationSummaryRepository;
//import com.google.api.client.googleapis.json.GoogleJsonResponseException;
//import com.google.api.services.calendar.Calendar;
//import com.google.api.services.calendar.model.Event;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Answers;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.ArgumentMatchers.isNull;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("VacationService 단위 테스트")
//class VacationServiceTest {
//
//    @Mock
//    private VacationRepository vacationRepository;
//
//    @Mock
//    private DepartmentVacationRepository departmentVacationRepository;
//
//    @Mock
//    private VacationSummaryRepository vacationSummaryRepository;
//
//    @Mock
//    private EmployeeRepository employeeRepository;
//
//    /**
//     * Google Calendar client는 체이닝 호출(events().insert().execute())이 많아서
//     * Deep Stub을 사용하면 테스트 작성이 훨씬 단순해집니다.
//     */
//    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
//    private Calendar googleCalendarClient;
//
//    @InjectMocks
//    private VacationService vacationService;
//
//    @BeforeEach
//    void setUp() {
//        // @Value로 주입되는 calendarId는 단위 테스트에서 Reflection으로 세팅
//        ReflectionTestUtils.setField(vacationService, "calendarId", "test-calendar-id");
//    }
//
//    @Nested
//    @DisplayName("findVacationHistory")
//    class FindVacationHistoryTest {
//
//        @Test
//        @DisplayName("page/size가 0 이하이면 안전값(1/10)으로 보정하여 조회한다")
//        void findVacationHistory_PageSizeInvalid_UseSafeValues() {
//            // Given
//            Integer employeeId = 1;
//            LocalDate startDate = LocalDate.of(2025, 1, 1);
//            LocalDate endDate = LocalDate.of(2025, 1, 31);
//
//            List<VacationHistoryDTO> content = List.of(
//                    org.mockito.Mockito.mock(VacationHistoryDTO.class),
//                    org.mockito.Mockito.mock(VacationHistoryDTO.class)
//            );
//
//            Page<VacationHistoryDTO> pageResult =
//                    new PageImpl<>(content, PageRequest.of(0, 10), 2);
//
//            when(vacationRepository.findVacationHistory(
//                    eq(employeeId),
//                    eq(startDate),
//                    eq(endDate),
//                    eq(PageRequest.of(0, 10))
//            )).thenReturn(pageResult);
//
//            // When
//            PageResponse<VacationHistoryDTO> result =
//                    vacationService.findVacationHistory(employeeId, startDate, endDate, 0, 0);
//
//            // Then
//            assertThat(result).isNotNull();
//            verify(vacationRepository, times(1))
//                    .findVacationHistory(eq(employeeId), eq(startDate), eq(endDate), eq(PageRequest.of(0, 10)));
//        }
//    }
//
//    @Nested
//    @DisplayName("findDepartmentVacationCalendar")
//    class FindDepartmentVacationCalendarTest {
//
//        @Test
//        @DisplayName("year/month가 주어지면 해당 월의 1일~말일 범위로 부서 휴가 캘린더를 조회한다")
//        void findDepartmentVacationCalendar_WithYearMonth_UsesMonthRange() {
//            // Given
//            Integer employeeId = 1;
//            Integer year = 2025;
//            Integer month = 12;
//
//            Employee employee = org.mockito.Mockito.mock(Employee.class);
//            EmployeeDepartment department = org.mockito.Mockito.mock(EmployeeDepartment.class);
//
//            when(employee.getEmployeeDepartment()).thenReturn(department);
//            when(department.getDepartmentId()).thenReturn(10);
//
//            when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
//
//            List<DepartmentVacationDTO> rows = List.of(
//                    org.mockito.Mockito.mock(DepartmentVacationDTO.class)
//            );
//
//            LocalDate expectedStart = LocalDate.of(2025, 12, 1);
//            LocalDate expectedEnd = LocalDate.of(2025, 12, 31);
//
//            when(departmentVacationRepository.findApprovedDepartmentVacationByMonth(
//                    eq(10),
//                    eq(expectedStart),
//                    eq(expectedEnd)
//            )).thenReturn(rows);
//
//            // When
//            List<DepartmentVacationDTO> result =
//                    vacationService.findDepartmentVacationCalendar(employeeId, year, month);
//
//            // Then
//            assertThat(result).isSameAs(rows);
//            verify(employeeRepository).findById(employeeId);
//            verify(departmentVacationRepository).findApprovedDepartmentVacationByMonth(
//                    eq(10),
//                    eq(expectedStart),
//                    eq(expectedEnd)
//            );
//        }
//
//        @Test
//        @DisplayName("month가 1~12 범위를 벗어나면 IllegalArgumentException이 발생한다")
//        void findDepartmentVacationCalendar_InvalidMonth_ThrowException() {
//            // Given
//            Integer employeeId = 1;
//
//            // When & Then
//            assertThatThrownBy(() -> vacationService.findDepartmentVacationCalendar(employeeId, 2025, 13))
//                    .isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("month는 1~12 범위");
//
//            verify(employeeRepository, never()).findById(anyInt());
//            verify(departmentVacationRepository, never())
//                    .findApprovedDepartmentVacationByMonth(anyInt(), any(LocalDate.class), any(LocalDate.class));
//        }
//
//        @Test
//        @DisplayName("employeeId가 존재하지 않으면 IllegalArgumentException이 발생한다")
//        void findDepartmentVacationCalendar_EmployeeNotFound_ThrowException() {
//            // Given
//            Integer employeeId = 999;
//            when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());
//
//            // When & Then
//            assertThatThrownBy(() -> vacationService.findDepartmentVacationCalendar(employeeId, 2025, 12))
//                    .isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("존재하지 않는 employeeId");
//
//            verify(employeeRepository).findById(employeeId);
//            verify(departmentVacationRepository, never())
//                    .findApprovedDepartmentVacationByMonth(anyInt(), any(LocalDate.class), any(LocalDate.class));
//        }
//    }
//
//    @Nested
//    @DisplayName("findVacationLeaves")
//    class FindVacationLeavesTest {
//
//        @Test
//        @DisplayName("직원 휴가 요약 정보를 repository에서 조회하여 반환한다")
//        void findVacationLeaves_ReturnRepositoryResult() {
//            // Given
//            Integer employeeId = 1;
//            VacationSummaryDTO summary = org.mockito.Mockito.mock(VacationSummaryDTO.class);
//
//            when(vacationSummaryRepository.findSummaryByEmployeeId(employeeId)).thenReturn(summary);
//
//            // When
//            VacationSummaryDTO result = vacationService.findVacationLeaves(employeeId);
//
//            // Then
//            assertThat(result).isSameAs(summary);
//            verify(vacationSummaryRepository).findSummaryByEmployeeId(employeeId);
//        }
//    }
//
//    @Nested
//    @DisplayName("syncVacationLogsToGoogleCalendar")
//    class SyncVacationLogsToGoogleCalendarTest {
//
//        @Test
//        @DisplayName("month가 1~12 범위를 벗어나면 IllegalArgumentException이 발생한다")
//        void syncVacationLogsToGoogleCalendar_InvalidMonth_ThrowException() {
//            // When & Then
//            assertThatThrownBy(() -> vacationService.syncVacationLogsToGoogleCalendar(2025, 0))
//                    .isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("month는 1~12 범위");
//        }
//
//        @Test
//        @DisplayName("googleEventId가 없으면 insert 후 googleEventId를 저장하고, 있으면 update 한다(404면 insert로 복구)")
//        void syncVacationLogsToGoogleCalendar_InsertUpdateAndRecover404() throws Exception {
//            // Given
//            int year = 2025;
//            int month = 12;
//
//            LocalDate firstDay = LocalDate.of(2025, 12, 1);
//            LocalDate lastDay = LocalDate.of(2025, 12, 31);
//
//            // log1: googleEventId 없음 -> insert
//            VacationLog log1 = org.mockito.Mockito.mock(VacationLog.class);
//            stubVacationLogBasics(log1, 101, LocalDate.of(2025, 12, 10), LocalDate.of(2025, 12, 10), null);
//
//            // log2: googleEventId 있음 -> update 성공
//            VacationLog log2 = org.mockito.Mockito.mock(VacationLog.class);
//            stubVacationLogBasics(log2, 102, LocalDate.of(2025, 12, 11), LocalDate.of(2025, 12, 12), "evt-102");
//
//            // log3: googleEventId 있음 -> update 시 404 -> insert 복구
//            VacationLog log3 = org.mockito.Mockito.mock(VacationLog.class);
//            stubVacationLogBasics(log3, 103, LocalDate.of(2025, 12, 20), LocalDate.of(2025, 12, 22), "evt-103");
//
//            when(vacationRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(lastDay, firstDay))
//                    .thenReturn(List.of(log1, log2, log3));
//
//            // insert 결과 이벤트
//            Event created1 = new Event();
//            created1.setId("new-101");
//
//            Event created3 = new Event();
//            created3.setId("new-103");
//
//            // log1 insert
//            when(googleCalendarClient.events()
//                    .insert(eq("test-calendar-id"), any(Event.class))
//                    .execute()
//            ).thenReturn(created1, created3);
//
//            // log2 update 성공
//            when(googleCalendarClient.events()
//                    .update(eq("test-calendar-id"), eq("evt-102"), any(Event.class))
//                    .execute()
//            ).thenReturn(new Event());
//
//            // log3 update -> 404 예외
//            GoogleJsonResponseException notFound = org.mockito.Mockito.mock(GoogleJsonResponseException.class);
//            when(notFound.getStatusCode()).thenReturn(404);
//
//            when(googleCalendarClient.events()
//                    .update(eq("test-calendar-id"), eq("evt-103"), any(Event.class))
//                    .execute()
//            ).thenThrow(notFound);
//
//            // When
//            VacationService.SyncResult result = vacationService.syncVacationLogsToGoogleCalendar(year, month);
//
//            // Then
//            assertThat(result).isNotNull();
//            assertThat(result.total()).isEqualTo(3);
//            assertThat(result.inserted()).isEqualTo(2);
//            assertThat(result.updated()).isEqualTo(1);
//            assertThat(result.failed()).isEqualTo(0);
//
//            // log1, log3는 insert 후 googleEventId 세팅됨
//            verify(log1).setGoogleEventId("new-101");
//            verify(log3).setGoogleEventId("new-103");
//
//            // log2는 update만 수행되므로 setGoogleEventId 호출 없음
//            verify(log2, never()).setGoogleEventId(any());
//
//            // repository 조회 범위 검증
//            verify(vacationRepository).findByStartDateLessThanEqualAndEndDateGreaterThanEqual(lastDay, firstDay);
//        }
//
//        private void stubVacationLogBasics(
//                VacationLog log,
//                int vacationLogId,
//                LocalDate startDate,
//                LocalDate endDate,
//                String googleEventId
//        ) {
//            when(log.getVacationLogId()).thenReturn(vacationLogId);
//            when(log.getStartDate()).thenReturn(startDate);
//            when(log.getEndDate()).thenReturn(endDate);
//            when(log.getGoogleEventId()).thenReturn(googleEventId);
//
//            // summary/description 생성 시 사용되는 연관 엔티티도 전부 Mock 처리
//            Employee employee = org.mockito.Mockito.mock(Employee.class);
//            when(employee.getEmployeeName()).thenReturn("홍길동");
//
//            VacationType type = org.mockito.Mockito.mock(VacationType.class);
//            when(type.getVacationTypeName()).thenReturn("연차");
//
//            when(log.getEmployee()).thenReturn(employee);
//            when(log.getVacationType()).thenReturn(type);
//            when(log.getReason()).thenReturn("개인 사유");
//        }
//    }
//}
