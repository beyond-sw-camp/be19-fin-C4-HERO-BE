package com.c4.hero.domain.attendance.service;

import com.c4.hero.domain.attendance.dto.PersonalDTO;
import com.c4.hero.domain.attendance.mapper.AttendanceMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class AttendanceEventServiceTest {

    @Mock
    private AttendanceMapper attendanceMapper;

    @InjectMocks
    private AttendanceEventService attendanceEventService;

    @Nested
    @DisplayName("getPersonalDetail()")
    class GetPersonalDetailTests {

        @Test
        @DisplayName("조회 결과가 존재하면 DTO를 그대로 반환한다")
        void getPersonalDetail_whenExists_thenReturnDto() {
            // given
            Integer employeeId = 2;
            Integer attendanceId = 10;

            PersonalDTO dto = mock(PersonalDTO.class);
            given(attendanceMapper.selectPersonalById(employeeId, attendanceId)).willReturn(dto);

            // when
            PersonalDTO result = attendanceEventService.getPersonalDetail(employeeId, attendanceId);

            // then
            assertThat(result).isSameAs(dto);
            then(attendanceMapper).should().selectPersonalById(employeeId, attendanceId);
        }

        @Test
        @DisplayName("조회 결과가 없으면 IllegalArgumentException을 던진다")
        void getPersonalDetail_whenNotExists_thenThrow() {
            // given
            Integer employeeId = 2;
            Integer attendanceId = 999;

            given(attendanceMapper.selectPersonalById(employeeId, attendanceId)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> attendanceEventService.getPersonalDetail(employeeId, attendanceId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("근태 기록이 존재하지 않습니다.")
                    .hasMessageContaining("attendanceId=" + attendanceId);
        }
    }

    @Nested
    @DisplayName("createCorrectionRequestFromApproval()")
    class CreateCorrectionRequestFromApprovalTests {

        @Test
        @DisplayName("정상 JSON이면 소유 검증 후 insertCorrectionRequest를 호출한다 (시간 '00:00'은 null 처리)")
        void createCorrectionRequestFromApproval_success_timeNull() {
            // given
            Integer employeeId = 2;
            int attendanceId = 10;
            String json = correctionJson(attendanceId, "2026-01-01", "00:00", "00:00", "지하철 연착");

            // 소유 검증 통과
            given(attendanceMapper.selectPersonalById(employeeId, attendanceId)).willReturn(mock(PersonalDTO.class));

            ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
            ArgumentCaptor<LocalTime> startCaptor = ArgumentCaptor.forClass(LocalTime.class);
            ArgumentCaptor<LocalTime> endCaptor = ArgumentCaptor.forClass(LocalTime.class);

            // when
            attendanceEventService.createCorrectionRequestFromApproval(employeeId, json);

            // then
            then(attendanceMapper).should().insertCorrectionRequest(
                    eq(employeeId),
                    eq(attendanceId),
                    dateCaptor.capture(),
                    startCaptor.capture(),
                    endCaptor.capture(),
                    eq("지하철 연착")
            );

            assertThat(dateCaptor.getValue()).isEqualTo(LocalDate.of(2026, 1, 1));
            assertThat(startCaptor.getValue()).isNull();
            assertThat(endCaptor.getValue()).isNull();
        }

        @Test
        @DisplayName("정상 JSON이면 시간 파싱이 되고 insertCorrectionRequest가 호출된다")
        void createCorrectionRequestFromApproval_success_withTimes() {
            // given
            Integer employeeId = 2;
            int attendanceId = 10;
            String json = correctionJson(attendanceId, "2026-01-01", "09:10", "18:05", "지각 정정");

            given(attendanceMapper.selectPersonalById(employeeId, attendanceId)).willReturn(mock(PersonalDTO.class));

            // when
            attendanceEventService.createCorrectionRequestFromApproval(employeeId, json);

            // then
            then(attendanceMapper).should().insertCorrectionRequest(
                    eq(employeeId),
                    eq(attendanceId),
                    eq(LocalDate.of(2026, 1, 1)),
                    eq(LocalTime.of(9, 10)),
                    eq(LocalTime.of(18, 5)),
                    eq("지각 정정")
            );
        }

        @Test
        @DisplayName("attendanceId가 0이면 실패(감싸서 IllegalArgumentException)")
        void createCorrectionRequestFromApproval_whenAttendanceIdMissing_thenFail() {
            // given
            Integer employeeId = 2;
            String json = correctionJson(0, "2026-01-01", "09:00", "18:00", "사유");

            // when & then (메서드가 try/catch로 감싸 재던짐)
            assertThatThrownBy(() -> attendanceEventService.createCorrectionRequestFromApproval(employeeId, json))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("근태 정정 요청 생성 실패")
                    .hasMessageContaining("attendanceId 누락");

            then(attendanceMapper).should(never()).insertCorrectionRequest(any(), anyInt(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("targetDate가 공백이면 실패(감싸서 IllegalArgumentException)")
        void createCorrectionRequestFromApproval_whenTargetDateMissing_thenFail() {
            // given
            Integer employeeId = 2;
            String json = """
                    {
                      "attendanceId": 10,
                      "targetDate": "",
                      "correctedStart": "09:00",
                      "correctedEnd": "18:00",
                      "reason": "사유"
                    }
                    """;

            // when & then
            assertThatThrownBy(() -> attendanceEventService.createCorrectionRequestFromApproval(employeeId, json))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("근태 정정 요청 생성 실패")
                    .hasMessageContaining("targetDate 누락");
        }

        @Test
        @DisplayName("소유 검증(selectPersonalById) 실패하면 생성 실패")
        void createCorrectionRequestFromApproval_whenPersonalNotFound_thenFail() {
            // given
            Integer employeeId = 2;
            int attendanceId = 10;
            String json = correctionJson(attendanceId, "2026-01-01", "09:00", "18:00", "사유");

            given(attendanceMapper.selectPersonalById(employeeId, attendanceId)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> attendanceEventService.createCorrectionRequestFromApproval(employeeId, json))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("근태 정정 요청 생성 실패")
                    .hasMessageContaining("근태 기록이 존재하지 않습니다.");

            then(attendanceMapper).should(never()).insertCorrectionRequest(any(), anyInt(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("JSON이 깨져 있으면 생성 실패(감싸서 IllegalArgumentException)")
        void createCorrectionRequestFromApproval_whenJsonInvalid_thenFail() {
            // given
            Integer employeeId = 2;
            String invalidJson = "{ invalid json";

            // when & then
            assertThatThrownBy(() -> attendanceEventService.createCorrectionRequestFromApproval(employeeId, invalidJson))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("근태 정정 요청 생성 실패");
        }
    }

    @Nested
    @DisplayName("createOvertimeFromApproval()")
    class CreateOvertimeFromApprovalTests {

        @Test
        @DisplayName("workDate가 있으면 overtimeHours 계산 후 insertOvertime 호출 (반올림 1자리)")
        void createOvertimeFromApproval_success_workDate() {
            // given
            Integer employeeId = 2;
            // 18:00~19:15 = 75분 => 1.25h => 1.3h
            String json = overtimeJson("2026-01-02", null, "18:00", "19:15", "업무 마감");

            // when
            attendanceEventService.createOvertimeFromApproval(employeeId, json);

            // then
            then(attendanceMapper).should().insertOvertime(
                    eq(employeeId),
                    eq(LocalDate.of(2026, 1, 2)),
                    eq(LocalTime.of(18, 0)),
                    eq(LocalTime.of(19, 15)),
                    eq(1.3f),
                    eq("업무 마감")
            );
        }

        @Test
        @DisplayName("workDate가 없고 date만 있으면 date로 fallback하여 처리한다")
        void createOvertimeFromApproval_success_dateFallback() {
            // given
            Integer employeeId = 2;
            // 19:00~20:30 = 90분 => 1.5h
            String json = overtimeJson("", "2026-01-03", "19:00", "20:30", "배포");

            // when
            attendanceEventService.createOvertimeFromApproval(employeeId, json);

            // then
            then(attendanceMapper).should().insertOvertime(
                    eq(employeeId),
                    eq(LocalDate.of(2026, 1, 3)),
                    eq(LocalTime.of(19, 0)),
                    eq(LocalTime.of(20, 30)),
                    eq(1.5f),
                    eq("배포")
            );
        }

        @Test
        @DisplayName("workDate/date 둘 다 없으면 실패(감싸서 IllegalArgumentException)")
        void createOvertimeFromApproval_whenWorkDateMissing_thenFail() {
            // given
            Integer employeeId = 2;
            String json = overtimeJson("", "", "18:00", "19:00", "사유");

            // when & then
            assertThatThrownBy(() -> attendanceEventService.createOvertimeFromApproval(employeeId, json))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("초과 근무 생성 실패")
                    .hasMessageContaining("workDate 누락");

            then(attendanceMapper).should(never()).insertOvertime(anyInt(), any(), any(), any(), anyFloat(), any());
        }

        @Test
        @DisplayName("startTime이 '00:00'이면 필수값 누락으로 실패(감싸서 IllegalArgumentException)")
        void createOvertimeFromApproval_whenStartTimeMissing_thenFail() {
            // given
            Integer employeeId = 2;
            String json = overtimeJson("2026-01-02", null, "00:00", "19:00", "사유");

            // when & then
            assertThatThrownBy(() -> attendanceEventService.createOvertimeFromApproval(employeeId, json))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("초과 근무 생성 실패")
                    .hasMessageContaining("startTime 누락");
        }

        @Test
        @DisplayName("endTime이 startTime보다 이전/동일이면 실패(감싸서 IllegalArgumentException)")
        void createOvertimeFromApproval_whenEndBeforeStart_thenFail() {
            // given
            Integer employeeId = 2;
            String json = overtimeJson("2026-01-02", null, "19:00", "18:00", "사유");

            // when & then
            assertThatThrownBy(() -> attendanceEventService.createOvertimeFromApproval(employeeId, json))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("초과 근무 생성 실패")
                    .hasMessageContaining("초과근무 시간 오류");
        }
    }

    @Nested
    @DisplayName("createWorkSystemChangeLogFromApproval()")
    class CreateWorkSystemChangeLogFromApprovalTests {

        @Test
        @DisplayName("정상 JSON이면 templateName 조회 후 insertWorkSystemChangeLog 호출")
        void createWorkSystemChangeLogFromApproval_success() {
            // given
            Integer employeeId = 2;
            String json = workSystemJson(3, "2026-01-10", "10:00", "19:00", "개인 사정");

            given(attendanceMapper.selectWorkSystemNameByAnyId(3)).willReturn("시차출근(10-19)");

            // when
            attendanceEventService.createWorkSystemChangeLogFromApproval(employeeId, json);

            // then
            then(attendanceMapper).should().insertWorkSystemChangeLog(
                    eq(employeeId),
                    eq(LocalDate.of(2026, 1, 10)),
                    eq("개인 사정"),
                    eq("시차출근(10-19)"),
                    eq(LocalTime.of(10, 0)),
                    eq(LocalTime.of(19, 0)),
                    eq(3)
            );
        }

        @Test
        @DisplayName("templateName이 null/blank면 '근무제 변경'으로 대체한다")
        void createWorkSystemChangeLogFromApproval_whenTemplateNameNull_thenFallback() {
            // given
            Integer employeeId = 2;
            String json = workSystemJson(3, "2026-01-10", "09:00", "18:00", "사유");

            given(attendanceMapper.selectWorkSystemNameByAnyId(3)).willReturn(null);

            // when
            attendanceEventService.createWorkSystemChangeLogFromApproval(employeeId, json);

            // then
            then(attendanceMapper).should().insertWorkSystemChangeLog(
                    eq(employeeId),
                    eq(LocalDate.of(2026, 1, 10)),
                    eq("사유"),
                    eq("근무제 변경"),
                    eq(LocalTime.of(9, 0)),
                    eq(LocalTime.of(18, 0)),
                    eq(3)
            );
        }

        @Test
        @DisplayName("workSystemTemplateId가 0이면 실패(감싸서 IllegalArgumentException)")
        void createWorkSystemChangeLogFromApproval_whenTemplateIdMissing_thenFail() {
            // given
            Integer employeeId = 2;
            String json = workSystemJson(0, "2026-01-10", "09:00", "18:00", "사유");

            // when & then
            assertThatThrownBy(() -> attendanceEventService.createWorkSystemChangeLogFromApproval(employeeId, json))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("근무제 변경 이력 생성 실패")
                    .hasMessageContaining("workSystemTemplate 누락");
        }

        @Test
        @DisplayName("applyDate가 공백이면 실패(감싸서 IllegalArgumentException)")
        void createWorkSystemChangeLogFromApproval_whenApplyDateMissing_thenFail() {
            // given
            Integer employeeId = 2;
            String json = workSystemJson(3, "", "09:00", "18:00", "사유");

            // when & then
            assertThatThrownBy(() -> attendanceEventService.createWorkSystemChangeLogFromApproval(employeeId, json))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("근무제 변경 이력 생성 실패")
                    .hasMessageContaining("applyDate 누락");
        }
    }

    // =========================================================
    // Helpers: JSON builders
    // =========================================================
    private static String correctionJson(
            int attendanceId,
            String targetDate,
            String correctedStart,
            String correctedEnd,
            String reason
    ) {
        return """
               {
                 "attendanceId": %d,
                 "targetDate": "%s",
                 "correctedStart": "%s",
                 "correctedEnd": "%s",
                 "reason": "%s"
               }
               """.formatted(attendanceId, targetDate, correctedStart, correctedEnd, reason);
    }

    private static String overtimeJson(
            String workDate,
            String dateFallback,
            String startTime,
            String endTime,
            String reason
    ) {
        // workDate가 비어있으면 dateFallback로 fallback하는 로직을 테스트하기 위해 둘 다 넣을 수 있게 구성
        return """
               {
                 "workDate": "%s",
                 "date": "%s",
                 "startTime": "%s",
                 "endTime": "%s",
                 "reason": "%s"
               }
               """.formatted(
                workDate == null ? "" : workDate,
                dateFallback == null ? "" : dateFallback,
                startTime,
                endTime,
                reason
        );
    }

    private static String workSystemJson(
            int workSystemTemplateId,
            String applyDate,
            String startTime,
            String endTime,
            String reason
    ) {
        return """
               {
                 "workSystemTemplate": %d,
                 "applyDate": "%s",
                 "startTime": "%s",
                 "endTime": "%s",
                 "reason": "%s"
               }
               """.formatted(workSystemTemplateId, applyDate, startTime, endTime, reason);
    }
}
