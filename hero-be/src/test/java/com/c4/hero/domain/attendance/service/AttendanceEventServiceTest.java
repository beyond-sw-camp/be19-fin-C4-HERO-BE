package com.c4.hero.domain.attendance.service;

import com.c4.hero.domain.attendance.dto.PersonalDTO;
import com.c4.hero.domain.attendance.mapper.AttendanceMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AttendanceEventServiceTest {

    @InjectMocks
    private AttendanceEventService attendanceEventService;

    @Mock
    private AttendanceMapper attendanceMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("개인 근태 상세 조회 성공 테스트")
    void getPersonalDetailSuccessTest() {
        // given
        Integer employeeId = 1;
        Integer attendanceId = 100;
        PersonalDTO mockDto = new PersonalDTO();
        mockDto.setAttendanceId(attendanceId);

        given(attendanceMapper.selectPersonalById(employeeId, attendanceId)).willReturn(mockDto);

        // when
        PersonalDTO result = attendanceEventService.getPersonalDetail(employeeId, attendanceId);

        // then
        assertNotNull(result);
        assertEquals(attendanceId, result.getAttendanceId());
    }

    @Test
    @DisplayName("개인 근태 상세 조회 실패 테스트 - 존재하지 않는 기록")
    void getPersonalDetailFailTest() {
        // given
        Integer employeeId = 1;
        Integer attendanceId = 999;

        given(attendanceMapper.selectPersonalById(employeeId, attendanceId)).willReturn(null);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> attendanceEventService.getPersonalDetail(employeeId, attendanceId));
    }

    @Test
    @DisplayName("근태 정정 요청 생성 성공 테스트")
    void createCorrectionRequestFromApprovalSuccessTest() throws Exception {
        // given
        Integer employeeId = 1;
        String detailsJson = "{\"attendanceId\": 100, \"targetDate\": \"2024-01-01\", \"correctedStart\": \"09:00\", \"correctedEnd\": \"18:00\", \"reason\": \"오류 수정\"}";

        PersonalDTO mockDto = new PersonalDTO();
        mockDto.setAttendanceId(100);

        given(attendanceMapper.selectPersonalById(employeeId, 100)).willReturn(mockDto);

        // when
        attendanceEventService.createCorrectionRequestFromApproval(employeeId, detailsJson);

        // then
        verify(attendanceMapper, times(1)).insertCorrectionRequest(
                eq(employeeId),
                eq(100),
                eq(LocalDate.of(2024, 1, 1)),
                eq(LocalTime.of(9, 0)),
                eq(LocalTime.of(18, 0)),
                eq("오류 수정")
        );
    }

    @Test
    @DisplayName("초과 근무 생성 성공 테스트")
    void createOvertimeFromApprovalSuccessTest() {
        // given
        Integer employeeId = 1;
        String detailsJson = "{\"workDate\": \"2024-01-01\", \"startTime\": \"18:00\", \"endTime\": \"20:00\", \"reason\": \"야근\"}";

        // when
        attendanceEventService.createOvertimeFromApproval(employeeId, detailsJson);

        // then
        verify(attendanceMapper, times(1)).insertOvertime(
                eq(employeeId),
                eq(LocalDate.of(2024, 1, 1)),
                eq(LocalTime.of(18, 0)),
                eq(LocalTime.of(20, 0)),
                eq(2.0f),
                eq("야근")
        );
    }

    @Test
    @DisplayName("근무제 변경 이력 생성 성공 테스트")
    void createWorkSystemChangeLogFromApprovalSuccessTest() {
        // given
        Integer employeeId = 1;
        String detailsJson = "{\"workSystemTemplate\": 1, \"applyDate\": \"2024-01-01\", \"startTime\": \"09:00\", \"endTime\": \"18:00\", \"reason\": \"변경\"}";

        given(attendanceMapper.selectWorkSystemNameByAnyId(1)).willReturn("유연 근무제");

        // when
        attendanceEventService.createWorkSystemChangeLogFromApproval(employeeId, detailsJson);

        // then
        verify(attendanceMapper, times(1)).insertWorkSystemChangeLog(
                eq(employeeId),
                eq(LocalDate.of(2024, 1, 1)),
                eq("변경"),
                eq("유연 근무제"),
                eq(LocalTime.of(9, 0)),
                eq(LocalTime.of(18, 0)),
                eq(1)
        );
    }
}
