package com.c4.hero.domain.attendance.service;

import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.attendance.dto.AttendanceDashboardDTO;
import com.c4.hero.domain.attendance.dto.AttendanceDashboardSummaryDTO;
import com.c4.hero.domain.attendance.dto.PersonalDTO;
import com.c4.hero.domain.attendance.mapper.AttendanceMapper;
import com.c4.hero.domain.attendance.repository.AttendanceDashboardRepository;
import com.c4.hero.domain.attendance.repository.AttendanceDashboardSummaryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("근태 서비스 테스트")
class AttendanceServiceTest {

    @InjectMocks
    private AttendanceService attendanceService;

    @Mock
    private AttendanceMapper attendanceMapper;
    @Mock
    private AttendanceDashboardRepository attendanceDashboardRepository;
    @Mock
    private AttendanceDashboardSummaryRepository attendanceDashboardSummaryRepository;

    @Nested
    @DisplayName("개인 근태 조회")
    class PersonalAttendanceTest {

        @Test
        @DisplayName("개인 근태 기록 페이지 조회 시 성공한다")
        void getPersonalList_Success() {
            // given
            Integer employeeId = 1;
            Integer page = 1;
            Integer size = 10;
            LocalDate startDate = LocalDate.of(2024, 1, 1);
            LocalDate endDate = LocalDate.of(2024, 1, 31);

            given(attendanceMapper.selectPersonalCount(employeeId, startDate, endDate)).willReturn(1);
            given(attendanceMapper.selectPersonalPage(eq(employeeId), anyInt(), eq(size), eq(startDate), eq(endDate)))
                    .willReturn(Collections.singletonList(new PersonalDTO()));

            // when
            PageResponse<PersonalDTO> result = attendanceService.getPersonalList(employeeId, page, size, startDate, endDate);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("부서 근태 대시보드")
    class DepartmentDashboardTest {

        @Test
        @DisplayName("근태 점수 대시보드 목록 조회 시 성공한다")
        void getAttendanceDashboardList_Success() {
            // given
            Integer departmentId = 1;
            String month = "2024-01";
            Pageable pageable = PageRequest.of(0, 10);
            Page<AttendanceDashboardDTO> mockPage = new PageImpl<>(Collections.singletonList(new AttendanceDashboardDTO()));

            given(attendanceDashboardRepository.findAttendanceDashboard(eq(departmentId), any(), any(), any(), any(Pageable.class)))
                    .willReturn(mockPage);

            // when
            PageResponse<AttendanceDashboardDTO> result = attendanceService.getAttendanceDashboardList(departmentId, month, "DESC", 1, 10);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("근태 점수 대시보드 요약 조회 시 성공한다")
        void getAttendanceDashboardSummary_Success() {
            // given
            Integer departmentId = 1;
            String month = "2024-01";

            given(attendanceDashboardSummaryRepository.countTotalEmployees(eq(departmentId))).willReturn(10L);
            given(attendanceDashboardSummaryRepository.countExcellentEmployees(eq(departmentId), any(), any())).willReturn(2L);
            given(attendanceDashboardSummaryRepository.countRiskyEmployees(eq(departmentId), any(), any())).willReturn(1L);

            // when
            AttendanceDashboardSummaryDTO result = attendanceService.getAttendanceDashboardSummary(departmentId, month);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTotalEmployees()).isEqualTo(10);
            assertThat(result.getExcellentEmployees()).isEqualTo(2);
            assertThat(result.getRiskyEmployees()).isEqualTo(1);
        }
    }
}
