package com.c4.hero.domain.vacation.service;

import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.entity.EmployeeDepartment;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import com.c4.hero.domain.vacation.dto.DepartmentVacationDTO;
import com.c4.hero.domain.vacation.dto.VacationHistoryDTO;
import com.c4.hero.domain.vacation.dto.VacationSummaryDTO;
import com.c4.hero.domain.vacation.entity.VacationLog;
import com.c4.hero.domain.vacation.entity.VacationType;
import com.c4.hero.domain.vacation.repository.DepartmentVacationRepository;
import com.c4.hero.domain.vacation.repository.VacationRepository;
import com.c4.hero.domain.vacation.repository.VacationSummaryRepository;
import com.c4.hero.domain.vacation.repository.VacationTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("휴가 서비스 단위 테스트")
class VacationServiceTest {

    @Mock private VacationRepository vacationRepository;
    @Mock private DepartmentVacationRepository departmentVacationRepository;
    @Mock private VacationSummaryRepository vacationSummaryRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private VacationTypeRepository vacationTypeRepository;

    @InjectMocks
    private VacationService vacationService;

    // =========================================================
    // findVacationHistory()
    // =========================================================
    @Nested
    @DisplayName("findVacationHistory()")
    class FindVacationHistoryTests {

        @Test
        @DisplayName("유효한 page/size면 PageRequest로 조회하고 PageResponse로 변환한다")
        void givenValidPageAndSize_whenFindVacationHistory_thenReturnPageResponse() {
            // Given
            Integer employeeId = 2;
            LocalDate startDate = LocalDate.of(2026, 1, 1);
            LocalDate endDate = LocalDate.of(2026, 1, 31);
            int page = 2;
            int size = 5;

            PageRequest expectedPageable = PageRequest.of(page - 1, size);

            List<VacationHistoryDTO> content = List.of(mock(VacationHistoryDTO.class));
            Page<VacationHistoryDTO> repoPage = new PageImpl<>(content, expectedPageable, 11);

            given(vacationRepository.findVacationHistory(eq(employeeId), eq(startDate), eq(endDate), any(PageRequest.class)))
                    .willReturn(repoPage);

            ArgumentCaptor<PageRequest> pageableCaptor = ArgumentCaptor.forClass(PageRequest.class);

            // When
            PageResponse<VacationHistoryDTO> result =
                    vacationService.findVacationHistory(employeeId, startDate, endDate, page, size);

            // Then
            then(vacationRepository).should()
                    .findVacationHistory(eq(employeeId), eq(startDate), eq(endDate), pageableCaptor.capture());

            PageRequest captured = pageableCaptor.getValue();
            assertThat(captured.getPageNumber()).isEqualTo(1);
            assertThat(captured.getPageSize()).isEqualTo(5);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEqualTo(content);
            assertThat(result.getPage()).isEqualTo(2);        // 서비스는 1-based로 반환
            assertThat(result.getSize()).isEqualTo(5);
            assertThat(result.getTotalElements()).isEqualTo(11);
        }

        @Test
        @DisplayName("page<=0이면 1로, size<=0이면 10으로 보정한다")
        void givenInvalidPageOrSize_whenFindVacationHistory_thenClampToDefault() {
            // Given
            Integer employeeId = 2;
            LocalDate startDate = null;
            LocalDate endDate = null;
            int page = 0;
            int size = 0;

            PageRequest expectedPageable = PageRequest.of(0, 10);
            Page<VacationHistoryDTO> repoPage = new PageImpl<>(List.of(), expectedPageable, 0);

            given(vacationRepository.findVacationHistory(eq(employeeId), isNull(), isNull(), any(PageRequest.class)))
                    .willReturn(repoPage);

            ArgumentCaptor<PageRequest> pageableCaptor = ArgumentCaptor.forClass(PageRequest.class);

            // When
            PageResponse<VacationHistoryDTO> result =
                    vacationService.findVacationHistory(employeeId, startDate, endDate, page, size);

            // Then
            then(vacationRepository).should()
                    .findVacationHistory(eq(employeeId), isNull(), isNull(), pageableCaptor.capture());

            PageRequest captured = pageableCaptor.getValue();
            assertThat(captured.getPageNumber()).isEqualTo(0);
            assertThat(captured.getPageSize()).isEqualTo(10);

            assertThat(result.getPage()).isEqualTo(1);
            assertThat(result.getSize()).isEqualTo(10);
            assertThat(result.getTotalElements()).isZero();
        }
    }

    // =========================================================
    // findDepartmentVacationCalendar()
    // =========================================================
    @Nested
    @DisplayName("findDepartmentVacationCalendar()")
    class FindDepartmentVacationCalendarTests {

        @Test
        @DisplayName("month가 1~12 범위가 아니면 예외가 발생한다")
        void givenInvalidMonth_whenFindDepartmentVacationCalendar_thenThrow() {
            // Given
            Integer employeeId = 2;
            Integer year = 2026;
            Integer month = 13;

            // When & Then
            assertThatThrownBy(() -> vacationService.findDepartmentVacationCalendar(employeeId, year, month))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("month는 1~12 범위여야 합니다.");
        }

        @Test
        @DisplayName("직원에서 departmentId를 구한 뒤, 해당 월의 승인 휴가를 조회한다")
        void givenEmployeeExists_whenFindDepartmentVacationCalendar_thenReturnRows() {
            // Given
            Integer employeeId = 2;
            Integer year = 2026;
            Integer month = 2;

            Employee employee = mock(Employee.class);
            EmployeeDepartment dept = mock(EmployeeDepartment.class);

            given(dept.getDepartmentId()).willReturn(6);
            given(employee.getEmployeeDepartment()).willReturn(dept);
            given(employeeRepository.findById(employeeId)).willReturn(Optional.of(employee));

            LocalDate monthStart = LocalDate.of(2026, 2, 1);
            LocalDate monthEnd = LocalDate.of(2026, 2, 28);

            List<DepartmentVacationDTO> rows = List.of(mock(DepartmentVacationDTO.class));
            given(departmentVacationRepository.findApprovedDepartmentVacationByMonth(6, monthStart, monthEnd))
                    .willReturn(rows);

            // When
            List<DepartmentVacationDTO> result =
                    vacationService.findDepartmentVacationCalendar(employeeId, year, month);

            // Then
            assertThat(result).isEqualTo(rows);
            then(departmentVacationRepository).should()
                    .findApprovedDepartmentVacationByMonth(6, monthStart, monthEnd);
        }

        @Test
        @DisplayName("직원이 존재하지 않으면 예외가 발생한다")
        void givenEmployeeNotFound_whenFindDepartmentVacationCalendar_thenThrow() {
            // Given
            Integer employeeId = 999;
            given(employeeRepository.findById(employeeId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> vacationService.findDepartmentVacationCalendar(employeeId, 2026, 2))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("존재하지 않는 employeeId");
        }
    }

    // =========================================================
    // findVacationLeaves()
    // =========================================================
    @Nested
    @DisplayName("findVacationLeaves()")
    class FindVacationLeavesTests {

        @Test
        @DisplayName("요약 결과가 null이면 null을 반환한다")
        void givenSummaryNull_whenFindVacationLeaves_thenReturnNull() {
            // Given
            Integer employeeId = 2;
            given(vacationSummaryRepository.findSummaryByEmployeeId(employeeId)).willReturn(null);

            // When
            VacationSummaryDTO result = vacationService.findVacationLeaves(employeeId);

            // Then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("요약 결과가 빈 리스트면 null을 반환한다")
        void givenSummaryEmpty_whenFindVacationLeaves_thenReturnNull() {
            // Given
            Integer employeeId = 2;
            given(vacationSummaryRepository.findSummaryByEmployeeId(employeeId)).willReturn(List.of());

            // When
            VacationSummaryDTO result = vacationService.findVacationLeaves(employeeId);

            // Then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("요약 결과가 여러 건이면 0번째(최신)를 반환한다")
        void givenSummaryMultiple_whenFindVacationLeaves_thenReturnNewest() {
            // Given
            Integer employeeId = 2;
            VacationSummaryDTO newest = mock(VacationSummaryDTO.class);
            VacationSummaryDTO older = mock(VacationSummaryDTO.class);

            given(vacationSummaryRepository.findSummaryByEmployeeId(employeeId))
                    .willReturn(List.of(newest, older));

            // When
            VacationSummaryDTO result = vacationService.findVacationLeaves(employeeId);

            // Then
            assertThat(result).isSameAs(newest);
        }
    }

    // =========================================================
    // createVacationLogFromApproval()
    // =========================================================
    @Nested
    @DisplayName("createVacationLogFromApproval()")
    class CreateVacationLogFromApprovalTests {

        @Test
        @DisplayName("정상 JSON이면 VacationLog를 생성하고 저장한다")
        void givenValidJson_whenCreateVacationLogFromApproval_thenSave() {
            // Given
            Integer employeeId = 2;
            int vacationTypeId = 1;
            String json = vacationApprovalJson(vacationTypeId, "2026-01-10", "2026-01-12", "개인 사유");

            Employee employee = mock(Employee.class);
            VacationType vacationType = mock(VacationType.class);

            given(employeeRepository.findById(employeeId)).willReturn(Optional.of(employee));
            given(vacationTypeRepository.findById(vacationTypeId)).willReturn(Optional.of(vacationType));

            ArgumentCaptor<VacationLog> logCaptor = ArgumentCaptor.forClass(VacationLog.class);

            // When & Then
            assertThatCode(() -> vacationService.createVacationLogFromApproval(employeeId, json))
                    .doesNotThrowAnyException();

            then(employeeRepository).should().findById(employeeId);
            then(vacationTypeRepository).should().findById(vacationTypeId);
            then(vacationRepository).should().save(logCaptor.capture());

            assertThat(logCaptor.getValue()).isNotNull();
        }

        @Test
        @DisplayName("필수 값이 누락되면 예외가 발생하고 저장하지 않는다")
        void givenMissingRequiredFields_whenCreateVacationLogFromApproval_thenThrowAndNotSave() {
            // Given
            Integer employeeId = 2;
            String json = """
                    { "vacationType": 0, "startDate": null, "endDate": null, "reason": "x" }
                    """;

            // When & Then
            assertThatThrownBy(() -> vacationService.createVacationLogFromApproval(employeeId, json))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("휴가 신청 details 처리 중 오류 발생")
                    .hasMessageContaining("details=");

            then(vacationRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("직원이 없으면 예외가 발생하고 저장하지 않는다")
        void givenEmployeeNotFound_whenCreateVacationLogFromApproval_thenThrowAndNotSave() {
            // Given
            Integer employeeId = 999;
            String json = vacationApprovalJson(1, "2026-01-10", "2026-01-12", "사유");

            given(employeeRepository.findById(employeeId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> vacationService.createVacationLogFromApproval(employeeId, json))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("휴가 신청 details 처리 중 오류 발생");

            then(vacationRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("휴가 유형이 없으면 예외가 발생하고 저장하지 않는다")
        void givenVacationTypeNotFound_whenCreateVacationLogFromApproval_thenThrowAndNotSave() {
            // Given
            Integer employeeId = 2;
            int vacationTypeId = 999;
            String json = vacationApprovalJson(vacationTypeId, "2026-01-10", "2026-01-12", "사유");

            given(employeeRepository.findById(employeeId)).willReturn(Optional.of(mock(Employee.class)));
            given(vacationTypeRepository.findById(vacationTypeId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> vacationService.createVacationLogFromApproval(employeeId, json))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("휴가 신청 details 처리 중 오류 발생");

            then(vacationRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("JSON 파싱에 실패하면 예외가 발생하고 저장하지 않는다")
        void givenInvalidJson_whenCreateVacationLogFromApproval_thenThrowAndNotSave() {
            // Given
            Integer employeeId = 2;
            String invalidJson = "{ invalid json";

            // When & Then
            assertThatThrownBy(() -> vacationService.createVacationLogFromApproval(employeeId, invalidJson))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("휴가 신청 details 처리 중 오류 발생");

            then(vacationRepository).should(never()).save(any());
        }
    }

    // =========================================================
    // Helpers
    // =========================================================
    private static String vacationApprovalJson(int vacationTypeId, String startDate, String endDate, String reason) {
        return """
               {
                 "vacationType": %d,
                 "startDate": "%s",
                 "endDate": "%s",
                 "reason": "%s"
               }
               """.formatted(vacationTypeId, startDate, endDate, reason);
    }
}
