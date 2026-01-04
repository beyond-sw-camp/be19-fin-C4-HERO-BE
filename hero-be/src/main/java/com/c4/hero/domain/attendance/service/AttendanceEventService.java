package com.c4.hero.domain.attendance.service;

import com.c4.hero.domain.attendance.dto.PersonalDTO;
import com.c4.hero.domain.attendance.mapper.AttendanceMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * <pre>
 * Class Name: AttendanceEventService
 * Description: 근태 관련 결재(Approval) 이벤트 처리 및 근태 데이터 적재 서비스
 *
 * History
 * 2025/12/29 (이지윤) 최초 작성 및 컨벤션 적용
 * 2025/12/30 (이지윤) 초과 근무 로직 추가
 * 2025/12/31 (이지윤) 근무제 변경 로직 추가
 * </pre>
 *
 * 결재 완료 이벤트에서 전달된 details(JSON) 정보를 기반으로
 * - 근태 정정 요청(tbl_attendance_correction_request)
 * - 초과 근무 기록(tbl_overtime)
 * 을 생성하는 책임을 가집니다.
 */
@Service
@RequiredArgsConstructor
public class AttendanceEventService {

    /** 근태 관련 조회/INSERT를 위한 MyBatis Mapper */
    private final AttendanceMapper attendanceMapper;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    /**
     * 특정 직원의 특정 근태 기록 상세 정보를 조회합니다.
     *
     * @param employeeId   직원 ID
     * @param attendanceId 근태 기록 ID
     * @return 개인 근태 상세 DTO
     * @throws IllegalArgumentException 근태 기록이 존재하지 않을 경우
     */
    public PersonalDTO getPersonalDetail(Integer employeeId, Integer attendanceId) {
        PersonalDTO dto = attendanceMapper.selectPersonalById(employeeId, attendanceId);

        if (dto == null) {
            throw new IllegalArgumentException("근태 기록이 존재하지 않습니다. attendanceId=" + attendanceId);
        }

        return dto;
    }

    /**
     * 결재 완료(details JSON)를 기반으로 근태 정정 요청을 생성합니다.
     *
     * @param employeeId  결재 기안자(또는 요청자) 직원 ID
     * @param detailsJson 결재 문서 details 필드(JSON 문자열)
     */
    public void createCorrectionRequestFromApproval(Integer employeeId, String detailsJson) {
        try {
            JsonNode root = objectMapper.readTree(detailsJson);

            int attendanceId = root.path("attendanceId").asInt(0);
            String targetDateStr = root.path("targetDate").asText("");
            String correctedStartStr = root.path("correctedStart").asText("00:00");
            String correctedEndStr = root.path("correctedEnd").asText("00:00");
            String reason = root.path("reason").asText("");

            if (attendanceId == 0) {
                throw new IllegalArgumentException("attendanceId 누락");
            }
            if (targetDateStr.isBlank()) {
                throw new IllegalArgumentException("targetDate 누락");
            }

            // 1) 본인 소유 attendance인지 검증
            PersonalDTO personal = attendanceMapper.selectPersonalById(employeeId, attendanceId);
            if (personal == null) {
                throw new IllegalArgumentException("근태 기록이 존재하지 않습니다. attendanceId=" + attendanceId);
            }

            // 2) 날짜/시간 파싱
            LocalDate targetDate = LocalDate.parse(targetDateStr);
            LocalTime correctedStart = parseLocalTimeOrNull(correctedStartStr);
            LocalTime correctedEnd = parseLocalTimeOrNull(correctedEndStr);

            // 3) 근태 정정 요청 INSERT
            attendanceMapper.insertCorrectionRequest(
                    employeeId,
                    attendanceId,
                    targetDate,
                    correctedStart,
                    correctedEnd,
                    reason
            );

        } catch (Exception e) {
            throw new IllegalArgumentException("근태 정정 요청 생성 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 결재 완료(details JSON)를 기반으로 초과근무 기록을 생성합니다.
     *
     * <p>전략</p>
     * - overtime는 attendance_id FK 대신 employee_id + date 기반으로 적재/조회
     *
     * @param employeeId  결재 기안자(또는 요청자) 직원 ID
     * @param detailsJson 결재 문서 details 필드(JSON 문자열)
     */
    public void createOvertimeFromApproval(Integer employeeId, String detailsJson) {
        try {
            JsonNode root = objectMapper.readTree(detailsJson);

            // 프론트 폼 키(workDate) 기준, 혹시 몰라 date도 fallback
            String workDateStr = root.path("workDate").asText("");
            if (workDateStr.isBlank()) {
                workDateStr = root.path("date").asText("");
            }

            String startTimeStr = root.path("startTime").asText("00:00");
            String endTimeStr = root.path("endTime").asText("00:00");
            String reason = root.path("reason").asText("");

            if (workDateStr.isBlank()) {
                throw new IllegalArgumentException("workDate 누락");
            }

            LocalDate workDate = LocalDate.parse(workDateStr);
            LocalTime startTime = parseLocalTimeRequired(startTimeStr, "startTime");
            LocalTime endTime = parseLocalTimeRequired(endTimeStr, "endTime");

            long minutes = Duration.between(startTime, endTime).toMinutes();
            if (minutes <= 0) {
                throw new IllegalArgumentException("초과근무 시간 오류: endTime이 startTime보다 이후여야 합니다.");
            }

            float overtimeHours = Math.round((minutes / 60.0) * 10.0) / 10.0f;

            // ✅ 초과근무 INSERT
            // (Mapper에 insertOvertime(...) 추가 필요)
            attendanceMapper.insertOvertime(
                    employeeId,
                    workDate,
                    startTime,
                    endTime,
                    overtimeHours,
                    reason
            );

        } catch (Exception e) {
            throw new IllegalArgumentException("초과 근무 생성 실패: " + e.getMessage(), e);
        }
    }

    /**
     * "HH:mm" 문자열을 {@link LocalTime} 으로 파싱합니다. (근태정정용: 미입력(null) 허용)
     */
    private LocalTime parseLocalTimeOrNull(String hhmm) {
        if (hhmm == null || hhmm.isBlank()) {
            return null;
        }
        if ("00:00".equals(hhmm)) {
            return null;
        }
        return LocalTime.parse(hhmm);
    }

    /**
     * "HH:mm" 문자열을 {@link LocalTime} 으로 파싱합니다. (초과근무용: 필수)
     */
    private LocalTime parseLocalTimeRequired(String hhmm, String fieldName) {
        if (hhmm == null || hhmm.isBlank() || "00:00".equals(hhmm)) {
            throw new IllegalArgumentException(fieldName + " 누락");
        }
        return LocalTime.parse(hhmm);
    }

    /**
     * 결재 완료(details JSON)를 기반으로 초과근무 기록을 생성합니다.
     *
     * @param employeeId  결재 기안자(또는 요청자) 직원 ID
     * @param detailsJson 결재 문서 details 필드(JSON 문자열)
     */
    public void createWorkSystemChangeLogFromApproval(Integer employeeId, String detailsJson) {
        try {
            JsonNode root = objectMapper.readTree(detailsJson);

            int workSystemTemplateId = root.path("workSystemTemplate").asInt(0);
            String applyDateStr = root.path("applyDate").asText("");
            String startTimeStr = root.path("startTime").asText("09:00");
            String endTimeStr = root.path("endTime").asText("18:00");
            String reason = root.path("reason").asText("");

            if (workSystemTemplateId == 0) {
                throw new IllegalArgumentException("workSystemTemplate 누락");
            }
            if (applyDateStr.isBlank()) {
                throw new IllegalArgumentException("applyDate 누락");
            }

            LocalDate applyDate = LocalDate.parse(applyDateStr);
            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);

            // ✅ template_name 채우기(로그 테이블에 문자열이 필요)
            // - ID가 템플릿ID든 타입ID든 대응 가능하도록, SQL에서 존재하는 쪽을 우선 조회하게 만들 예정(아래 XML 참고)
            String templateName = attendanceMapper.selectWorkSystemNameByAnyId(workSystemTemplateId);
            if (templateName == null || templateName.isBlank()) {
                templateName = "근무제 변경";
            }

            attendanceMapper.insertWorkSystemChangeLog(
                    employeeId,
                    applyDate,
                    reason,
                    templateName,
                    startTime,
                    endTime,
                    workSystemTemplateId
            );

        } catch (Exception e) {
            throw new IllegalArgumentException("근무제 변경 이력 생성 실패: " + e.getMessage(), e);
        }
    }

}
