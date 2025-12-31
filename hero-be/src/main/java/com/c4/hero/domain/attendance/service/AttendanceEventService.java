package com.c4.hero.domain.attendance.service;

import com.c4.hero.domain.attendance.dto.PersonalDTO;
import com.c4.hero.domain.attendance.mapper.AttendanceMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * <pre>
 * Class Name: AttendanceEventService
 * Description: 근태 관련 결재(Approval) 이벤트 처리 및 근태 정정 요청 생성 서비스
 *
 * History
 * 2025/12/29 (이지윤) 최초 작성 및 컨벤션 적용
 * </pre>
 *
 * 결재 완료 이벤트에서 전달된 details(JSON) 정보를 기반으로
 * 근태 정정 요청(tbl_attendance_correction_request)을 생성하는 책임을 가집니다.
 */
@Service
@RequiredArgsConstructor
public class AttendanceEventService {

    /** 근태 관련 조회/정정 INSERT를 위한 MyBatis Mapper */
    private final AttendanceMapper attendanceMapper;

    /**
     * JSON 파싱용 ObjectMapper.
     *
     * <p>
     * Java 8 날짜/시간 모듈 등 자동 등록을 위해 {@code findAndRegisterModules()}를 호출합니다.
     * </p>
     */
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
     * <p>JSON 구조 예시</p>
     * <pre>
     * {
     *   "attendanceId": 123,
     *   "targetDate": "2025-12-10",
     *   "correctedStart": "09:00",
     *   "correctedEnd": "18:00",
     *   "reason": "지각 처리 정정 요청"
     * }
     * </pre>
     *
     * <p>처리 순서</p>
     * <ol>
     *   <li>JSON 파싱 후 필수 값(attendanceId, targetDate) 검증</li>
     *   <li>해당 근태(attendanceId)가 employeeId의 소유인지 검증</li>
     *   <li>LocalDate/LocalTime 파싱</li>
     *   <li>근태 정정 요청 INSERT</li>
     * </ol>
     *
     * @param employeeId  결재 기안자(또는 요청자) 직원 ID
     * @param detailsJson 결재 문서 details 필드(JSON 문자열)
     * @throws IllegalArgumentException JSON 파싱 오류 또는 필수 값 누락, 소유 검증 실패 등
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
     * "HH:mm" 문자열을 {@link LocalTime} 으로 파싱합니다.
     *
     * <p>규칙</p>
     * <ul>
     *     <li>null 또는 공백 문자열 → {@code null} 반환</li>
     *     <li>"00:00" → 미입력으로 간주하고 {@code null} 반환</li>
     *     <li>그 외 → {@link LocalTime#parse(CharSequence)} 사용</li>
     * </ul>
     *
     * @param hhmm "HH:mm" 형식의 문자열
     * @return 파싱된 {@link LocalTime} 또는 {@code null}
     */
    private LocalTime parseLocalTimeOrNull(String hhmm) {
        if (hhmm == null || hhmm.isBlank()) {
            return null;
        }
        if ("00:00".equals(hhmm)) {
            // "미입력" 정책 유지
            return null;
        }

        return LocalTime.parse(hhmm);
    }
}
