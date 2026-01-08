package com.c4.hero.domain.attendance.service;

import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.attendance.dto.*;
import com.c4.hero.domain.attendance.entity.Attendance;
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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock private AttendanceMapper attendanceMapper;
    @Mock private DeptWorkSystemRepository deptWorkSystemRepository;
    @Mock private AttendanceDashboardRepository attendanceDashboardRepository;
    @Mock private AttendanceDashboardSummaryRepository attendanceDashboardSummaryRepository;
    @Mock private AttendanceEmployeeDashboardRepository attendanceEmployeeDashboardRepository;
    @Mock private EmployeeRepository employeeRepository;

    @InjectMocks
    private AttendanceService attendanceService;

    // =========================================================
    // changeStatus
    // =========================================================
    @Nested
    @DisplayName("changeStatus()")
    class ChangeStatusTests {

        @Test
        @DisplayName("attendanceId가 있으면 findById로 조회 → '정상' 변경 → save")
        void changeStatus_whenAttendanceIdProvided_thenUpdateAndSave() {
            // given
            int drafterId = 2;
            int attendanceId = 10;
            String json = detailsJson(attendanceId, "2026-01-01", "09:00", "18:00");

            Attendance attendance = mock(Attendance.class);

            given(attendanceEmployeeDashboardRepository.findById(attendanceId))
                    .willReturn(Optional.of(attendance));
            given(attendanceMapper.selectBreakMinMinutes(attendanceId)).willReturn(60);

            // when
            attendanceService.changeStatus(drafterId, json);

            // then (540 - 60 = 480)
            verify(attendance).changeStatus("정상", 480);
            verify(attendanceEmployeeDashboardRepository).save(attendance);
            then(attendanceMapper).should().selectBreakMinMinutes(attendanceId);
        }

        @Test
        @DisplayName("attendanceId=0이면 (employeeId, targetDate)로 조회 → '정상' 변경 → save (breakMin 조회는 0으로 호출)")
        void changeStatus_whenAttendanceIdZero_thenFindByEmployeeAndDateUpdateAndSave() {
            // given
            int drafterId = 2;
            int attendanceId = 0;
            LocalDate targetDate = LocalDate.of(2026, 1, 1);
            String json = detailsJson(attendanceId, targetDate.toString(), "10:00", "12:00");

            Attendance attendance = mock(Attendance.class);

            given(attendanceEmployeeDashboardRepository.findByEmployee_EmployeeIdAndWorkDate(drafterId, targetDate))
                    .willReturn(attendance);
            given(attendanceMapper.selectBreakMinMinutes(attendanceId)).willReturn(30);

            // when
            attendanceService.changeStatus(drafterId, json);

            // then (120 - 30 = 90)
            verify(attendance).changeStatus("정상", 90);
            verify(attendanceEmployeeDashboardRepository).save(attendance);
            then(attendanceMapper).should().selectBreakMinMinutes(0);
        }

        @Test
        @DisplayName("시간이 '00:00'이면 null 처리되어 workDuration=null로 저장")
        void changeStatus_whenTimeIsZero_thenWorkDurationNull() {
            // given
            int drafterId = 2;
            int attendanceId = 10;
            String json = detailsJson(attendanceId, "2026-01-01", "00:00", "00:00");

            Attendance attendance = mock(Attendance.class);
            given(attendanceEmployeeDashboardRepository.findById(attendanceId))
                    .willReturn(Optional.of(attendance));

            // when
            attendanceService.changeStatus(drafterId, json);

            // then
            verify(attendance).changeStatus("정상", null);
            verify(attendanceEmployeeDashboardRepository).save(attendance);
        }

        @Test
        @DisplayName("breakMinMinutes가 null이면 0으로 보정")
        void changeStatus_whenBreakMinIsNull_thenTreatAsZero() {
            // given
            int drafterId = 2;
            int attendanceId = 10;
            String json = detailsJson(attendanceId, "2026-01-01", "09:00", "10:00"); // 60분

            Attendance attendance = mock(Attendance.class);
            given(attendanceEmployeeDashboardRepository.findById(attendanceId))
                    .willReturn(Optional.of(attendance));
            given(attendanceMapper.selectBreakMinMinutes(attendanceId)).willReturn(null);

            // when
            attendanceService.changeStatus(drafterId, json);

            // then (60 - 0 = 60)
            verify(attendance).changeStatus("정상", 60);
            verify(attendanceEmployeeDashboardRepository).save(attendance);
        }

        @Test
        @DisplayName("휴게시간이 총 근무시간보다 크면 workDuration=totalMinutes로 보정(현재 구현 로직 기준)")
        void changeStatus_whenBreakGreaterThanTotal_thenClampToTotal() {
            // given
            int drafterId = 2;
            int attendanceId = 10;
            String json = detailsJson(attendanceId, "2026-01-01", "09:00", "10:00"); // total=60

            Attendance attendance = mock(Attendance.class);
            given(attendanceEmployeeDashboardRepository.findById(attendanceId))
                    .willReturn(Optional.of(attendance));
            given(attendanceMapper.selectBreakMinMinutes(attendanceId)).willReturn(90);

            // when
            attendanceService.changeStatus(drafterId, json);

            // then
            // total 60 - break 90 => 음수 -> 0 보정
            // total < break 이므로 workDuration = total(60)
            verify(attendance).changeStatus("정상", 60);
            verify(attendanceEmployeeDashboardRepository).save(attendance);
        }

        @Test
        @DisplayName("근태 조회 실패(EntityNotFound 등 포함) 시 IllegalStateException으로 래핑되어 던져진다(현재 구현)")
        void changeStatus_whenNotFound_thenThrowIllegalState() {
            // given
            int drafterId = 2;
            int attendanceId = 0;
            LocalDate targetDate = LocalDate.of(2026, 1, 1);
            String json = detailsJson(attendanceId, targetDate.toString(), "09:00", "18:00");

            given(attendanceEmployeeDashboardRepository.findByEmployee_EmployeeIdAndWorkDate(drafterId, targetDate))
                    .willReturn(null);

            // when & then
            assertThatThrownBy(() -> attendanceService.changeStatus(drafterId, json))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("근태이력 수정 중 오류가 발생했습니다.");

            then(attendanceEmployeeDashboardRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("JSON 파싱 실패 시 IllegalArgumentException을 던진다")
        void changeStatus_whenJsonInvalid_thenThrowIllegalArgument() {
            // given
            int drafterId = 2;
            String invalidJson = "{ invalid json ";

            // when & then
            assertThatThrownBy(() -> attendanceService.changeStatus(drafterId, invalidJson))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("근태이력 수정 데이터 형식이 올바르지 않습니다.");
        }
    }

    // =========================================================
    // getPersonalSummary
    // =========================================================
    @Nested
    @DisplayName("getPersonalSummary()")
    class GetPersonalSummaryTests {

        @Test
        @DisplayName("start/end를 명시하면 해당 기간으로 mapper를 호출한다(현재월/오늘 default 로직 회피)")
        void getPersonalSummary_whenDatesProvided_thenCallMapperWithDates() {
            // given
            Integer employeeId = 2;
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 7);

            AttSummaryDTO dto = mock(AttSummaryDTO.class);
            given(attendanceMapper.selectPersonalSummary(employeeId, start, end)).willReturn(dto);

            // when
            AttSummaryDTO result = attendanceService.getPersonalSummary(employeeId, start, end);

            // then
            assertThat(result).isSameAs(dto);
            then(attendanceMapper).should().selectPersonalSummary(employeeId, start, end);
        }
    }

    // =========================================================
    // getPersonalList / getOvertimeList / getCorrectionList / getChangeLogList
    // =========================================================
    @Nested
    @DisplayName("페이지네이션(MyBatis 기반) 조회")
    class PagingListTests {

        @Test
        @DisplayName("getPersonalList: totalCount → PageCalculator → selectPersonalPage 호출 후 PageResponse 반환")
        void getPersonalList_basic() {
            // given
            int employeeId = 2;
            int page = 1;
            int size = 10;
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 31);

            given(attendanceMapper.selectPersonalCount(employeeId, start, end)).willReturn(15);

            // PageCalculator 계산값에 맞춰 stub
            var expectedPageInfo = com.c4.hero.common.pagination.PageCalculator.calculate(page, size, 15);

            List<PersonalDTO> items = List.of(mock(PersonalDTO.class), mock(PersonalDTO.class));
            given(attendanceMapper.selectPersonalPage(employeeId,
                    expectedPageInfo.getOffset(),
                    expectedPageInfo.getSize(),
                    start,
                    end
            )).willReturn(items);

            // when
            PageResponse<PersonalDTO> result = attendanceService.getPersonalList(employeeId, page, size, start, end);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEqualTo(items);
            assertThat(result.getTotalElements()).isEqualTo(15);
            assertThat(result.getSize()).isEqualTo(expectedPageInfo.getSize());
            assertThat(result.getPage()).isEqualTo(expectedPageInfo.getPage() - 1);

            then(attendanceMapper).should().selectPersonalCount(employeeId, start, end);
            then(attendanceMapper).should().selectPersonalPage(employeeId,
                    expectedPageInfo.getOffset(),
                    expectedPageInfo.getSize(),
                    start,
                    end
            );
        }

        @Test
        @DisplayName("getOvertimeList: count/page 호출 후 PageResponse 반환")
        void getOvertimeList_basic() {
            // given
            int employeeId = 2;
            int page = 2;
            int size = 5;
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 31);

            given(attendanceMapper.selectOvertimeCount(employeeId, start, end)).willReturn(9);
            var expectedPageInfo = com.c4.hero.common.pagination.PageCalculator.calculate(page, size, 9);

            List<OvertimeDTO> items = List.of(mock(OvertimeDTO.class));
            given(attendanceMapper.selectOvertimePage(employeeId,
                    expectedPageInfo.getOffset(),
                    expectedPageInfo.getSize(),
                    start,
                    end
            )).willReturn(items);

            // when
            PageResponse<OvertimeDTO> result = attendanceService.getOvertimeList(employeeId, page, size, start, end);

            // then
            assertThat(result.getContent()).isEqualTo(items);
            assertThat(result.getTotalElements()).isEqualTo(9);

            then(attendanceMapper).should().selectOvertimeCount(employeeId, start, end);
            then(attendanceMapper).should().selectOvertimePage(employeeId,
                    expectedPageInfo.getOffset(),
                    expectedPageInfo.getSize(),
                    start,
                    end
            );
        }

        @Test
        @DisplayName("getCorrectionList: count/page 호출 후 PageResponse 반환")
        void getCorrectionList_basic() {
            // given
            int employeeId = 2;
            int page = 1;
            int size = 10;
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 31);

            given(attendanceMapper.selectCorrectionCount(employeeId, start, end)).willReturn(0);
            var expectedPageInfo = com.c4.hero.common.pagination.PageCalculator.calculate(page, size, 0);

            List<CorrectionDTO> items = List.of();
            given(attendanceMapper.selectCorrectionPage(employeeId,
                    expectedPageInfo.getOffset(),
                    expectedPageInfo.getSize(),
                    start,
                    end
            )).willReturn(items);

            // when
            PageResponse<CorrectionDTO> result = attendanceService.getCorrectionList(employeeId, page, size, start, end);

            // then
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }

        @Test
        @DisplayName("getChangeLogList: count/page 호출 후 PageResponse 반환")
        void getChangeLogList_basic() {
            // given
            int employeeId = 2;
            int page = 1;
            int size = 10;
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 31);

            given(attendanceMapper.selectChangeLogCount(employeeId, start, end)).willReturn(3);
            var expectedPageInfo = com.c4.hero.common.pagination.PageCalculator.calculate(page, size, 3);

            List<ChangeLogDTO> items = List.of(mock(ChangeLogDTO.class));
            given(attendanceMapper.selectChangeLogPage(employeeId,
                    expectedPageInfo.getOffset(),
                    expectedPageInfo.getSize(),
                    start,
                    end
            )).willReturn(items);

            // when
            PageResponse<ChangeLogDTO> result = attendanceService.getChangeLogList(employeeId, page, size, start, end);

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(3);
        }
    }

    // =========================================================
    // getDeptWorkSystemList
    // =========================================================
    @Nested
    @DisplayName("getDeptWorkSystemList()")
    class DeptWorkSystemTests {

        @Test
        @DisplayName("workDate 명시 시 해당 날짜 + 0-based pageable로 repository 호출")
        void getDeptWorkSystemList_whenDateProvided_thenCallsRepository() {
            // given
            Integer employeeId = 2;
            Integer departmentId = 6;
            LocalDate workDate = LocalDate.of(2026, 1, 7);
            int page = 1;
            int size = 10;

            List<DeptWorkSystemDTO> content = List.of(mock(DeptWorkSystemDTO.class));
            Page<DeptWorkSystemDTO> pageResult = new PageImpl<>(content, PageRequest.of(0, size), 1);

            given(deptWorkSystemRepository.findDeptWorkSystemRows(eq(employeeId), eq(departmentId), eq(workDate), any(Pageable.class)))
                    .willReturn(pageResult);

            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

            // when
            PageResponse<DeptWorkSystemDTO> result =
                    attendanceService.getDeptWorkSystemList(employeeId, departmentId, workDate, page, size);

            // then
            assertThat(result.getContent()).isEqualTo(content);
            assertThat(result.getTotalElements()).isEqualTo(1);

            then(deptWorkSystemRepository).should()
                    .findDeptWorkSystemRows(eq(employeeId), eq(departmentId), eq(workDate), pageableCaptor.capture());

            Pageable captured = pageableCaptor.getValue();
            assertThat(captured.getPageNumber()).isEqualTo(0);
            assertThat(captured.getPageSize()).isEqualTo(size);
        }

        @Test
        @DisplayName("page가 0 이하로 들어오면 0페이지로 보정된다")
        void getDeptWorkSystemList_whenPageIsZero_thenClampToZero() {
            // given
            Integer employeeId = 2;
            Integer departmentId = 6;
            LocalDate workDate = LocalDate.of(2026, 1, 7);
            int page = 0;
            int size = 10;

            Page<DeptWorkSystemDTO> pageResult = new PageImpl<>(List.of(), PageRequest.of(0, size), 0);

            given(deptWorkSystemRepository.findDeptWorkSystemRows(eq(employeeId), eq(departmentId), eq(workDate), any(Pageable.class)))
                    .willReturn(pageResult);

            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

            // when
            attendanceService.getDeptWorkSystemList(employeeId, departmentId, workDate, page, size);

            // then
            then(deptWorkSystemRepository).should()
                    .findDeptWorkSystemRows(eq(employeeId), eq(departmentId), eq(workDate), pageableCaptor.capture());

            assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(0);
        }
    }

    // =========================================================
    // getAttendanceDashboardList / Summary
    // =========================================================
    @Nested
    @DisplayName("근태 점수 대시보드")
    class AttendanceDashboardTests {

        @Test
        @DisplayName("getAttendanceDashboardList: month(YYYY-MM) → start/end 계산 후 repository 호출, sort 기본값 DESC")
        void getAttendanceDashboardList_whenSortNull_thenDefaultDescAndCallRepo() {
            // given
            Integer departmentId = null;
            String month = "2020-02"; // 현재월 회피(클램핑 로직 회피)
            String scoreSort = null;
            int page = 1;
            int size = 10;

            LocalDate expectedStart = LocalDate.of(2020, 2, 1);
            LocalDate expectedEnd = LocalDate.of(2020, 2, 29);

            List<AttendanceDashboardDTO> content = List.of(mock(AttendanceDashboardDTO.class));
            Page<AttendanceDashboardDTO> repoPage = new PageImpl<>(content, PageRequest.of(0, size), 1);

            given(attendanceDashboardRepository.findAttendanceDashboard(
                    isNull(),
                    eq(expectedStart),
                    eq(expectedEnd),
                    eq("DESC"),
                    any(Pageable.class)
            )).willReturn(repoPage);

            // when
            PageResponse<AttendanceDashboardDTO> result =
                    attendanceService.getAttendanceDashboardList(departmentId, month, scoreSort, page, size);

            // then
            assertThat(result.getContent()).isEqualTo(content);
            assertThat(result.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("getAttendanceDashboardSummary: total은 month 무관, excellent/risky는 month 기간으로 집계")
        void getAttendanceDashboardSummary_basic() {
            // given
            Integer departmentId = 6;
            String month = "2020-02";

            LocalDate expectedStart = LocalDate.of(2020, 2, 1);
            LocalDate expectedEnd = LocalDate.of(2020, 2, 29);

            given(attendanceDashboardSummaryRepository.countTotalEmployees(departmentId)).willReturn(100L);
            given(attendanceDashboardSummaryRepository.countExcellentEmployees(departmentId, expectedStart, expectedEnd)).willReturn(12L);
            given(attendanceDashboardSummaryRepository.countRiskyEmployees(departmentId, expectedStart, expectedEnd)).willReturn(3L);

            // when
            AttendanceDashboardSummaryDTO result = attendanceService.getAttendanceDashboardSummary(departmentId, month);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTotalEmployees()).isEqualTo(100L);
            assertThat(result.getExcellentEmployees()).isEqualTo(12L);
            assertThat(result.getRiskyEmployees()).isEqualTo(3L);
        }
    }

    // =========================================================
    // getEmployeeHalfDashboard
    // =========================================================
    @Nested
    @DisplayName("getEmployeeHalfDashboard()")
    class EmployeeHalfDashboardTests {

        @Test
        @DisplayName("요약이 null이면 0으로 대체하고, 누락 월은 0으로 채워 H1(1~6월) 리스트를 만든다(과거 연도)")
        void getEmployeeHalfDashboard_whenSummaryNull_thenFillMonths() {
            // given
            int employeeId = 20;
            int year = 2020; // 현재 연도 회피(미래월 클램핑 회피)
            AttendanceHalfType halfType = AttendanceHalfType.H1;

            Employee employee = mock(Employee.class);
            given(employee.getEmployeeNumber()).willReturn("D6-MGR");
            given(employee.getEmployeeName()).willReturn("최백엔");
            given(employeeRepository.findById(employeeId)).willReturn(Optional.of(employee));

            LocalDate expectedStart = LocalDate.of(2020, 1, 1);
            LocalDate expectedEnd = LocalDate.of(2020, 6, 30);

            given(attendanceEmployeeDashboardRepository.findEmployeeHalfSummary(employeeId, expectedStart, expectedEnd))
                    .willReturn(null);

            List<AttendanceEmployeeMonthlyStatDTO> monthlyRows = List.of(
                    new AttendanceEmployeeMonthlyStatDTO(1, 20L, 1L, 0L),
                    new AttendanceEmployeeMonthlyStatDTO(3, 18L, 0L, 1L)
            );

            given(attendanceEmployeeDashboardRepository.findEmployeeMonthlyStats(employeeId, expectedStart, expectedEnd))
                    .willReturn(monthlyRows);

            // when
            AttendanceEmployeeHalfDashboardDTO result =
                    attendanceService.getEmployeeHalfDashboard(employeeId, year, halfType);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getEmployeeId()).isEqualTo(employeeId);
            assertThat(result.getEmployeeNumber()).isEqualTo("D6-MGR");
            assertThat(result.getEmployeeName()).isEqualTo("최백엔");
            assertThat(result.getYear()).isEqualTo(2020);
            assertThat(result.getHalf()).isEqualTo(AttendanceHalfType.H1);

            // summary null -> 0 대체
            assertThat(result.getSummary()).isEqualTo(new AttendanceEmployeeHalfSummaryDTO(0L, 0L, 0L));

            // H1 => 1~6월 6개
            assertThat(result.getMonthlyStats()).hasSize(6);

            // 2월은 누락 -> 0
            AttendanceEmployeeMonthlyStatDTO feb = result.getMonthlyStats().get(1);
            assertThat(feb.getMonth()).isEqualTo(2);
            assertThat(feb.getWorkDays()).isZero();
            assertThat(feb.getTardyCount()).isZero();
            assertThat(feb.getAbsenceCount()).isZero();
        }

        @Test
        @DisplayName("직원 정보가 없으면 IllegalArgumentException을 던진다")
        void getEmployeeHalfDashboard_whenEmployeeNotFound_thenThrow() {
            // given
            int employeeId = 999;
            given(employeeRepository.findById(employeeId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> attendanceService.getEmployeeHalfDashboard(employeeId, 2020, AttendanceHalfType.H1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("직원 정보 없음");
        }
    }

    // =========================================================
    // Helpers
    // =========================================================
    private static String detailsJson(int attendanceId, String targetDate, String correctedStart, String correctedEnd) {
        return """
               {
                 "attendanceId": %d,
                 "targetDate": "%s",
                 "correctedStart": "%s",
                 "correctedEnd": "%s"
               }
               """.formatted(attendanceId, targetDate, correctedStart, correctedEnd);
    }
}
