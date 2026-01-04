package com.c4.hero.domain.attendance.service;

import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.attendance.dto.*;
import com.c4.hero.domain.attendance.mapper.AttendanceMapper;
import com.c4.hero.domain.attendance.repository.AttendanceDashboardRepository;
import com.c4.hero.domain.attendance.repository.AttendanceDashboardSummaryRepository;
import com.c4.hero.domain.attendance.repository.AttendanceEmployeeDashboardRepository;
import com.c4.hero.domain.attendance.repository.DeptWorkSystemRepository;
import com.c4.hero.domain.attendance.type.AttendanceHalfType;
import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@DisplayName("AttendanceService 단위 테스트")
class AttendanceServiceTest {

    @Mock
    private AttendanceMapper attendanceMapper;

    @Mock
    private DeptWorkSystemRepository deptWorkSystemRepository;

    @Mock
    private AttendanceDashboardRepository attendanceDashboardRepository;

    @Mock
    private AttendanceDashboardSummaryRepository attendanceDashboardSummaryRepository;

    @Mock
    private AttendanceEmployeeDashboardRepository attendanceEmployeeDashboardRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private AttendanceService attendanceService;

    /* =========================
       Fixtures
       ========================= */

    private AttendanceEmployeeMonthlyStatDTO stat(int month, long workDays, long tardy, long absence) {
        return new AttendanceEmployeeMonthlyStatDTO(month, workDays, tardy, absence);
    }

    private Employee mockEmployee(String employeeNumber, String employeeName) {
        Employee employee = mock(Employee.class);
        when(employee.getEmployeeNumber()).thenReturn(employeeNumber);
        when(employee.getEmployeeName()).thenReturn(employeeName);
        return employee;
    }

    /* =========================
       개인 요약
       ========================= */

    @Nested
    @DisplayName("개인 근태 요약 카드(getPersonalSummary)")
    class GetPersonalSummaryTest {

        @Test
        @DisplayName("기간이 null이면 이번 달 1일~오늘로 보정하여 Mapper를 호출한다")
        void getPersonalSummary_NullPeriod_ResolveToThisMonthAndToday() {
            // Given
            Integer employeeId = 1;

            LocalDate today = LocalDate.now();
            LocalDate expectedStart = today.withDayOfMonth(1);
            LocalDate expectedEnd = today;

            AttSummaryDTO summary = mock(AttSummaryDTO.class);
            when(attendanceMapper.selectPersonalSummary(eq(employeeId), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(summary);

            // When
            AttSummaryDTO result = attendanceService.getPersonalSummary(employeeId, null, null);

            // Then
            assertThat(result).isSameAs(summary);

            ArgumentCaptor<LocalDate> startCaptor = ArgumentCaptor.forClass(LocalDate.class);
            ArgumentCaptor<LocalDate> endCaptor = ArgumentCaptor.forClass(LocalDate.class);

            verify(attendanceMapper).selectPersonalSummary(eq(employeeId), startCaptor.capture(), endCaptor.capture());

            assertThat(startCaptor.getValue()).isEqualTo(expectedStart);
            assertThat(endCaptor.getValue()).isEqualTo(expectedEnd);
        }

        @Test
        @DisplayName("employeeId는 인증단에서 보장된다고 가정하며, 서비스는 Mapper에 그대로 전달한다")
        void getPersonalSummary_NullEmployeeId_PassThroughToMapper() {
            // Given
            AttSummaryDTO summary = mock(AttSummaryDTO.class);

            when(attendanceMapper.selectPersonalSummary(
                    isNull(),
                    any(LocalDate.class),
                    any(LocalDate.class)
            )).thenReturn(summary);

            // When
            AttSummaryDTO result = attendanceService.getPersonalSummary(null, null, null);

            // Then
            assertThat(result).isSameAs(summary);
            verify(attendanceMapper).selectPersonalSummary(isNull(), any(LocalDate.class), any(LocalDate.class));
        }

    }

    /* =========================
       개인/초과/정정/변경 리스트(페이지네이션)
       ========================= */

    @Nested
    @DisplayName("개인/초과/정정/변경 목록(페이지네이션)")
    class PersonalListPagingTest {

        @Test
        @DisplayName("개인 근태 목록: count 조회 후 page 조회를 수행한다")
        void getPersonalList_CountThenPageQuery() {
            // Given
            Integer employeeId = 1;
            int page = 2;
            Integer size = 10;

            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 1, 31);

            when(attendanceMapper.selectPersonalCount(employeeId, startDate, endDate)).thenReturn(25);
            when(attendanceMapper.selectPersonalPage(eq(employeeId), anyInt(), anyInt(), eq(startDate), eq(endDate)))
                    .thenReturn(List.of(mock(PersonalDTO.class)));

            // When
            PageResponse<PersonalDTO> result =
                    attendanceService.getPersonalList(employeeId, page, size, startDate, endDate);

            // Then
            assertThat(result).isNotNull();

            ArgumentCaptor<Integer> offsetCaptor = ArgumentCaptor.forClass(Integer.class);
            ArgumentCaptor<Integer> sizeCaptor = ArgumentCaptor.forClass(Integer.class);

            verify(attendanceMapper).selectPersonalCount(employeeId, startDate, endDate);
            verify(attendanceMapper).selectPersonalPage(eq(employeeId), offsetCaptor.capture(), sizeCaptor.capture(), eq(startDate), eq(endDate));

            // PageCalculator가 통상 offset=(page-1)*size 로 계산한다고 가정(프로젝트 대부분 동일)
            assertThat(offsetCaptor.getValue()).isEqualTo((page - 1) * size);
            assertThat(sizeCaptor.getValue()).isEqualTo(size);
        }

        @Test
        @DisplayName("초과 근무 목록: count 조회 후 page 조회를 수행한다")
        void getOvertimeList_CountThenPageQuery() {
            // Given
            Integer employeeId = 1;
            Integer page = 1;
            Integer size = 20;

            when(attendanceMapper.selectOvertimeCount(employeeId, null, null)).thenReturn(0);
            when(attendanceMapper.selectOvertimePage(eq(employeeId), anyInt(), anyInt(), isNull(), isNull()))
                    .thenReturn(List.of());

            // When
            PageResponse<OvertimeDTO> result =
                    attendanceService.getOvertimeList(employeeId, page, size, null, null);

            // Then
            assertThat(result).isNotNull();

            verify(attendanceMapper).selectOvertimeCount(employeeId, null, null);
            verify(attendanceMapper).selectOvertimePage(employeeId, 0, size, null, null);
        }

        @Test
        @DisplayName("근태 정정 목록: count 조회 후 page 조회를 수행한다")
        void getCorrectionList_CountThenPageQuery() {
            // Given
            Integer employeeId = 1;
            Integer page = 3;
            Integer size = 5;

            when(attendanceMapper.selectCorrectionCount(employeeId, null, null)).thenReturn(13);
            when(attendanceMapper.selectCorrectionPage(eq(employeeId), anyInt(), anyInt(), isNull(), isNull()))
                    .thenReturn(List.of(mock(CorrectionDTO.class)));

            // When
            PageResponse<CorrectionDTO> result =
                    attendanceService.getCorrectionList(employeeId, page, size, null, null);

            // Then
            assertThat(result).isNotNull();

            verify(attendanceMapper).selectCorrectionCount(employeeId, null, null);
            verify(attendanceMapper).selectCorrectionPage(employeeId, (page - 1) * size, size, null, null);
        }

        @Test
        @DisplayName("근무제 변경 이력 목록: count 조회 후 page 조회를 수행한다")
        void getChangeLogList_CountThenPageQuery() {
            // Given
            Integer employeeId = 1;
            Integer page = 1;
            Integer size = 10;

            when(attendanceMapper.selectChangeLogCount(employeeId, null, null)).thenReturn(1);
            when(attendanceMapper.selectChangeLogPage(eq(employeeId), anyInt(), anyInt(), isNull(), isNull()))
                    .thenReturn(List.of(mock(ChangeLogDTO.class)));

            // When
            PageResponse<ChangeLogDTO> result =
                    attendanceService.getChangeLogList(employeeId, page, size, null, null);

            // Then
            assertThat(result).isNotNull();

            verify(attendanceMapper).selectChangeLogCount(employeeId, null, null);
            verify(attendanceMapper).selectChangeLogPage(employeeId, 0, size, null, null);
        }
    }

    /* =========================
       부서 근태 현황
       ========================= */

    @Nested
    @DisplayName("부서 근태 현황(getDeptWorkSystemList)")
    class DeptWorkSystemTest {

        @Test
        @DisplayName("workDate가 주어지면 해당 날짜로 Repository를 호출한다")
        void getDeptWorkSystemList_WithWorkDate_CallsRepository() {
            // Given
            Integer employeeId = 1;
            Integer departmentId = 10;
            LocalDate workDate = LocalDate.of(2025, 12, 1);
            int page = 1;
            int size = 10;

            Page<DeptWorkSystemDTO> pageResult = new PageImpl<>(
                    List.of(mock(DeptWorkSystemDTO.class)),
                    PageRequest.of(0, size),
                    1
            );

            when(deptWorkSystemRepository.findDeptWorkSystemRows(
                    eq(employeeId), eq(departmentId), eq(workDate), any(Pageable.class)
            )).thenReturn(pageResult);

            // When
            PageResponse<DeptWorkSystemDTO> result =
                    attendanceService.getDeptWorkSystemList(employeeId, departmentId, workDate, page, size);

            // Then
            assertThat(result).isNotNull();

            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

            verify(deptWorkSystemRepository).findDeptWorkSystemRows(
                    eq(employeeId), eq(departmentId), eq(workDate), pageableCaptor.capture()
            );

            assertThat(pageableCaptor.getValue()).isEqualTo(PageRequest.of(0, size));
        }
    }

    /* =========================
       월 대시보드 / 요약
       ========================= */

    @Nested
    @DisplayName("근태 점수 대시보드(getAttendanceDashboardList / getAttendanceDashboardSummary)")
    class DashboardTest {

        @Test
        @DisplayName("month가 주어지면 해당 월 start/end로 Repository를 호출한다")
        void getAttendanceDashboardList_WithMonth_UsesMonthRange() {
            // Given
            Integer departmentId = 10;
            String month = "2024-02"; // 현재 월과 무관한 안정 테스트
            String scoreSort = "ASC";
            int page = 2;
            int size = 5;

            YearMonth ym = YearMonth.parse(month);
            LocalDate expectedStart = ym.atDay(1);
            LocalDate expectedEnd = ym.atEndOfMonth();

            Page<AttendanceDashboardDTO> pageResult = new PageImpl<>(
                    List.of(mock(AttendanceDashboardDTO.class)),
                    PageRequest.of(page - 1, size),
                    1
            );

            when(attendanceDashboardRepository.findAttendanceDashboard(
                    eq(departmentId),
                    any(LocalDate.class),
                    any(LocalDate.class),
                    anyString(),
                    any(Pageable.class)
            )).thenReturn(pageResult);

            // When
            PageResponse<AttendanceDashboardDTO> result =
                    attendanceService.getAttendanceDashboardList(departmentId, month, scoreSort, page, size);

            // Then
            assertThat(result).isNotNull();

            ArgumentCaptor<LocalDate> startCaptor = ArgumentCaptor.forClass(LocalDate.class);
            ArgumentCaptor<LocalDate> endCaptor = ArgumentCaptor.forClass(LocalDate.class);
            ArgumentCaptor<String> sortCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

            verify(attendanceDashboardRepository).findAttendanceDashboard(
                    eq(departmentId),
                    startCaptor.capture(),
                    endCaptor.capture(),
                    sortCaptor.capture(),
                    pageableCaptor.capture()
            );

            assertThat(startCaptor.getValue()).isEqualTo(expectedStart);
            assertThat(endCaptor.getValue()).isEqualTo(expectedEnd);
            assertThat(sortCaptor.getValue()).isEqualTo("ASC");
            assertThat(pageableCaptor.getValue()).isEqualTo(PageRequest.of(page - 1, size));
        }

        @Test
        @DisplayName("scoreSort가 null/blank이면 DESC로 보정한다")
        void getAttendanceDashboardList_BlankSort_DefaultDesc() {
            // Given
            Integer departmentId = null;
            String month = "2024-03";
            String scoreSort = "   "; // ✅ blank
            int page = 1;
            int size = 10;

            when(attendanceDashboardRepository.findAttendanceDashboard(
                    isNull(), any(LocalDate.class), any(LocalDate.class), anyString(), any(Pageable.class)
            )).thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, size), 0));

            // When
            attendanceService.getAttendanceDashboardList(departmentId, month, scoreSort, page, size);

            // Then
            ArgumentCaptor<String> sortCaptor = ArgumentCaptor.forClass(String.class);

            verify(attendanceDashboardRepository).findAttendanceDashboard(
                    isNull(), any(LocalDate.class), any(LocalDate.class), sortCaptor.capture(), any(Pageable.class)
            );

            assertThat(sortCaptor.getValue()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("요약 조회: total은 month와 무관하게 countTotalEmployees로 조회한다")
        void getAttendanceDashboardSummary_TotalIndependentOfMonth() {
            // Given
            Integer departmentId = 10;
            String month = "2024-04";

            when(attendanceDashboardSummaryRepository.countTotalEmployees(departmentId)).thenReturn(100L);
            when(attendanceDashboardSummaryRepository.countExcellentEmployees(eq(departmentId), any(), any()))
                    .thenReturn(12L);
            when(attendanceDashboardSummaryRepository.countRiskyEmployees(eq(departmentId), any(), any()))
                    .thenReturn(5L);

            YearMonth ym = YearMonth.parse(month);
            LocalDate expectedStart = ym.atDay(1);
            LocalDate expectedEnd = ym.atEndOfMonth();

            // When
            AttendanceDashboardSummaryDTO result =
                    attendanceService.getAttendanceDashboardSummary(departmentId, month);

            // Then
            assertThat(result).isNotNull();

            verify(attendanceDashboardSummaryRepository).countTotalEmployees(departmentId);

            ArgumentCaptor<LocalDate> startCaptor = ArgumentCaptor.forClass(LocalDate.class);
            ArgumentCaptor<LocalDate> endCaptor = ArgumentCaptor.forClass(LocalDate.class);

            verify(attendanceDashboardSummaryRepository).countExcellentEmployees(eq(departmentId), startCaptor.capture(), endCaptor.capture());
            assertThat(startCaptor.getValue()).isEqualTo(expectedStart);
            assertThat(endCaptor.getValue()).isEqualTo(expectedEnd);

            verify(attendanceDashboardSummaryRepository).countRiskyEmployees(eq(departmentId), any(LocalDate.class), any(LocalDate.class));
        }
    }

    /* =========================
       반기 대시보드
       ========================= */

    @Nested
    @DisplayName("직원 반기 대시보드(getEmployeeHalfDashboard)")
    class EmployeeHalfDashboardTest {

        @Test
        @DisplayName("직원 정보가 없으면 예외가 발생한다")
        void getEmployeeHalfDashboard_EmployeeNotFound_ThrowException() {
            // Given
            Integer employeeId = 1;
            Integer year = 2024;
            AttendanceHalfType halfType = AttendanceHalfType.H1;

            when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> attendanceService.getEmployeeHalfDashboard(employeeId, year, halfType))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("직원 정보 없음");

            verify(employeeRepository).findById(employeeId);
            verifyNoInteractions(attendanceEmployeeDashboardRepository);
        }

        @Test
        @DisplayName("summary가 null이면 0으로 보정하고, H1 누락 월은 0으로 채운다(1~6월)")
        void getEmployeeHalfDashboard_SummaryNull_FillMissingMonths_H1() {
            // Given
            Integer employeeId = 1;
            Integer year = 2024;
            AttendanceHalfType halfType = AttendanceHalfType.H1;

            LocalDate expectedStart = LocalDate.of(2024, 1, 1);
            LocalDate expectedEnd = LocalDate.of(2024, 6, 30);

            Employee employee = mock(Employee.class);
            when(employee.getEmployeeNumber()).thenReturn("E-0001");
            when(employee.getEmployeeName()).thenReturn("홍길동");
            when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

            when(attendanceEmployeeDashboardRepository.findEmployeeHalfSummary(employeeId, expectedStart, expectedEnd))
                    .thenReturn(null);

            when(attendanceEmployeeDashboardRepository.findEmployeeMonthlyStats(employeeId, expectedStart, expectedEnd))
                    .thenReturn(List.of(
                            new AttendanceEmployeeMonthlyStatDTO(1, 20L, 1L, 0L),
                            new AttendanceEmployeeMonthlyStatDTO(3, 22L, 0L, 1L)
                    ));

            // When
            AttendanceEmployeeHalfDashboardDTO result =
                    attendanceService.getEmployeeHalfDashboard(employeeId, year, halfType);

            // Then
            assertThat(result).isNotNull();

            // ✅ DTO 필드명 기준: employeeNumber/employeeName/year/half
            assertThat(result.getEmployeeNumber()).isEqualTo("E-0001");
            assertThat(result.getEmployeeName()).isEqualTo("홍길동");
            assertThat(result.getYear()).isEqualTo(2024);
            assertThat(result.getHalf()).isEqualTo(AttendanceHalfType.H1);

            // ✅ Summary DTO 필드명 기준: totalWorkDays/totalTardyCount/totalAbsenceCount
            AttendanceEmployeeHalfSummaryDTO summary = result.getSummary();
            assertThat(summary).isNotNull();
            assertThat(summary.getTotalWorkDays()).isEqualTo(0L);
            assertThat(summary.getTotalTardyCount()).isEqualTo(0L);
            assertThat(summary.getTotalAbsenceCount()).isEqualTo(0L);

            List<AttendanceEmployeeMonthlyStatDTO> monthlyStats = result.getMonthlyStats();
            assertThat(monthlyStats).hasSize(6);

            assertThat(monthlyStats)
                    .extracting(
                            AttendanceEmployeeMonthlyStatDTO::getMonth,
                            AttendanceEmployeeMonthlyStatDTO::getWorkDays,
                            AttendanceEmployeeMonthlyStatDTO::getTardyCount,
                            AttendanceEmployeeMonthlyStatDTO::getAbsenceCount
                    )
                    .containsExactly(
                            tuple(1, 20L, 1L, 0L),
                            tuple(2, 0L, 0L, 0L),
                            tuple(3, 22L, 0L, 1L),
                            tuple(4, 0L, 0L, 0L),
                            tuple(5, 0L, 0L, 0L),
                            tuple(6, 0L, 0L, 0L)
                    );

            verify(employeeRepository).findById(employeeId);
            verify(attendanceEmployeeDashboardRepository).findEmployeeHalfSummary(employeeId, expectedStart, expectedEnd);
            verify(attendanceEmployeeDashboardRepository).findEmployeeMonthlyStats(employeeId, expectedStart, expectedEnd);
        }

        @Test
        @DisplayName("월별 집계가 비어 있어도 H2 범위(7~12월)만큼 0 데이터로 채운다")
        void getEmployeeHalfDashboard_EmptyMonthlyRows_FillZeros_H2() {
            // Given
            Integer employeeId = 1;
            Integer year = 2024;
            AttendanceHalfType halfType = AttendanceHalfType.H2;

            LocalDate expectedStart = LocalDate.of(2024, 7, 1);
            LocalDate expectedEnd = LocalDate.of(2024, 12, 31);

            Employee employee = mock(Employee.class);
            when(employee.getEmployeeNumber()).thenReturn("E-0002");
            when(employee.getEmployeeName()).thenReturn("김철수");
            when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

            AttendanceEmployeeHalfSummaryDTO summary =
                    new AttendanceEmployeeHalfSummaryDTO(10L, 2L, 1L);

            when(attendanceEmployeeDashboardRepository.findEmployeeHalfSummary(employeeId, expectedStart, expectedEnd))
                    .thenReturn(summary);

            when(attendanceEmployeeDashboardRepository.findEmployeeMonthlyStats(employeeId, expectedStart, expectedEnd))
                    .thenReturn(List.of());

            // When
            AttendanceEmployeeHalfDashboardDTO result =
                    attendanceService.getEmployeeHalfDashboard(employeeId, year, halfType);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getHalf()).isEqualTo(AttendanceHalfType.H2);
            assertThat(result.getSummary()).isSameAs(summary);

            assertThat(result.getMonthlyStats())
                    .extracting(
                            AttendanceEmployeeMonthlyStatDTO::getMonth,
                            AttendanceEmployeeMonthlyStatDTO::getWorkDays,
                            AttendanceEmployeeMonthlyStatDTO::getTardyCount,
                            AttendanceEmployeeMonthlyStatDTO::getAbsenceCount
                    )
                    .containsExactly(
                            tuple(7, 0L, 0L, 0L),
                            tuple(8, 0L, 0L, 0L),
                            tuple(9, 0L, 0L, 0L),
                            tuple(10, 0L, 0L, 0L),
                            tuple(11, 0L, 0L, 0L),
                            tuple(12, 0L, 0L, 0L)
                    );
        }
    }

}
