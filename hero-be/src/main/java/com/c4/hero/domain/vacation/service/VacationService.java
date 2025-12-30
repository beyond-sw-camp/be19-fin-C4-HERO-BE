//package com.c4.hero.domain.vacation.service;
//
//import com.c4.hero.common.response.PageResponse;
//import com.c4.hero.domain.employee.repository.EmployeeRepository;
//import com.c4.hero.domain.vacation.dto.DepartmentVacationDTO;
//import com.c4.hero.domain.vacation.dto.VacationHistoryDTO;
//import com.c4.hero.domain.vacation.dto.VacationSummaryDTO;
//import com.c4.hero.domain.vacation.entity.VacationLog;
//import com.c4.hero.domain.vacation.repository.DepartmentVacationRepository;
//import com.c4.hero.domain.vacation.repository.VacationRepository;
//import com.c4.hero.domain.vacation.repository.VacationSummaryRepository;
//import com.google.api.client.googleapis.json.GoogleJsonResponseException;
//import com.google.api.client.util.DateTime;
//import com.google.api.services.calendar.Calendar;
//import com.google.api.services.calendar.model.Event;
//import com.google.api.services.calendar.model.EventDateTime;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.ZoneId;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * <pre>
// * Class Name: VacationService
// * Description: 휴가 도메인 비즈니스 로직을 처리하는 서비스 클래스
// *
// * History
// * 2025/12/16 (이지윤) 최초 작성 및 코딩 컨벤션 적용
// * 2025/12/23 (수정) Google Calendar 동기화 기능 통합
// * </pre>
// *
// * @author 이지윤
// * @version 1.1
// */
//@Service
//@RequiredArgsConstructor
//public class VacationService {
//
//    /** 휴가 이력 조회를 위한 레포지토리 */
//    private final VacationRepository vacationRepository;
//    private final DepartmentVacationRepository departmentVacationRepository;
//    private final VacationSummaryRepository vacationSummaryRepository;
//    private final EmployeeRepository employeeRepository;
//
//    /** Google Calendar API Client (Config에서 Bean으로 등록된 것 주입) */
////    private final Calendar googleCalendarClient;
//
//    @Value("${google.calendar.id}")
//    private String calendarId;
//
//    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
//
//    /**
//     * 직원 휴가 이력을 페이지 단위로 조회합니다.
//     */
//    public PageResponse<VacationHistoryDTO> findVacationHistory(
//            Integer employeeId,
//            LocalDate startDate,
//            LocalDate endDate,
//            int page,
//            int size
//    ) {
//        int safePage = (page <= 0) ? 1 : page;
//        int safeSize = (size <= 0) ? 10 : size;
//
//        PageRequest pageable = PageRequest.of(safePage - 1, safeSize);
//
//        Page<VacationHistoryDTO> pageResult = vacationRepository.findVacationHistory(
//                employeeId,
//                startDate,
//                endDate,
//                pageable
//        );
//
//        return PageResponse.of(
//                pageResult.getContent(),
//                pageResult.getNumber() + 1,
//                pageResult.getSize(),
//                (int) pageResult.getTotalElements()
//        );
//    }
//
//    /**
//     * 부서 휴가 캘린더(월 단위) 조회
//     */
//    public List<DepartmentVacationDTO> findDepartmentVacationCalendar(
//            Integer employeeId,
//            Integer year,
//            Integer month
//    ) {
//        LocalDate now = LocalDate.now(KST);
//
//        int targetYear = (year != null) ? year : now.getYear();
//        int targetMonth = (month != null) ? month : now.getMonthValue();
//
//        if (targetMonth < 1 || targetMonth > 12) {
//            throw new IllegalArgumentException("month는 1~12 범위여야 합니다. month=" + targetMonth);
//        }
//
//        Integer departmentId = employeeRepository.findById(employeeId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 employeeId=" + employeeId))
//                .getEmployeeDepartment()
//                .getDepartmentId();
//
//        LocalDate firstDay = LocalDate.of(targetYear, targetMonth, 1);
//        LocalDate monthStart = firstDay;
//        LocalDate monthEnd = firstDay.plusMonths(1).minusDays(1);
//
//        return departmentVacationRepository.findApprovedDepartmentVacationByMonth(
//                departmentId,
//                monthStart,
//                monthEnd
//        );
//    }
//
//    /**
//     * 직원의 휴가 요약 정보를 조회합니다.
//     */
//    public VacationSummaryDTO findVacationLeaves(Integer employeeId) {
//        return vacationSummaryRepository.findSummaryByEmployeeId(employeeId);
//    }
//
//    // ========================================================================
//    // Google Calendar Sync (기존 GoogleCalendarSyncService 로직 통합)
//    // ========================================================================
//
//    /**
//     * 특정 월의 tbl_vacation_log 데이터를 Google Calendar에 동기화합니다.
//     *
//     * <p>
//     * - google_event_id가 없으면 insert 후 google_event_id 저장
//     * - google_event_id가 있으면 update
//     * - update 시 404(캘린더에서 이벤트 삭제됨)면 insert로 복구 후 google_event_id 갱신
//     * </p>
//     *
//     * @param year  연도
//     * @param month 월(1~12)
//     * @return 동기화 결과
//     */
//    public SyncResult syncVacationLogsToGoogleCalendar(int year, int month) {
//        if (month < 1 || month > 12) {
//            throw new IllegalArgumentException("month는 1~12 범위여야 합니다. month=" + month);
//        }
//
//        LocalDate firstDay = LocalDate.of(year, month, 1);
//        LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);
//
//        // 월 범위와 "겹치는" 휴가만 조회 (start<=monthEnd AND end>=monthStart)
//        // 아래 파생 메서드는 VacationRepository에 추가되어 있어야 합니다.
//        List<VacationLog> logs = vacationRepository
//                .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(lastDay, firstDay);
//
//        int inserted = 0;
//        int updated = 0;
//        int failed = 0;
//
//        for (VacationLog log : logs) {
//            try {
//                // 원하면 APPROVED만 동기화하도록 여기서 필터링하세요.
//                // 예) if (log.getApprovalStatus() != VacationStatus.APPROVED) continue;
//
//                String eventId = log.getGoogleEventId();
//                Event event = buildEventFromLog(log);
//
//                if (eventId == null || eventId.isBlank()) {
//                    Event created = googleCalendarClient.events()
//                            .insert(calendarId, event)
//                            .execute();
//
//                    log.setGoogleEventId(created.getId());
//                    inserted++;
//                    continue;
//                }
//
//                try {
//                    googleCalendarClient.events()
//                            .update(calendarId, eventId, event)
//                            .execute();
//                    updated++;
//                } catch (GoogleJsonResponseException e) {
//                    if (e.getStatusCode() == 404) {
//                        Event created = googleCalendarClient.events()
//                                .insert(calendarId, event)
//                                .execute();
//
//                        log.setGoogleEventId(created.getId());
//                        inserted++;
//                    } else {
//                        throw e;
//                    }
//                }
//
//            } catch (Exception ex) {
//                failed++;
//                System.err.println("[GoogleCalendarSync] failed. vacationLogId=" + log.getVacationLogId()
//                        + ", msg=" + ex.getMessage());
//            }
//        }
//
//        return new SyncResult(inserted, updated, failed, logs.size());
//    }
//
//    /**
//     * VacationLog -> Google Calendar Event 변환
//     *
//     * - 휴가는 UX상 "하루 종일" 이벤트로 생성하는 것이 자연스럽습니다.
//     * - Google Calendar all-day 이벤트의 end.date는 "종료 다음날"(exclusive) 규칙입니다.
//     */
//    private Event buildEventFromLog(VacationLog log) {
//        LocalDate start = log.getStartDate();      // LocalDate
//        LocalDate endInclusive = log.getEndDate(); // LocalDate
//        LocalDate endExclusive = endInclusive.plusDays(1);
//
//        Event event = new Event();
//        event.setSummary(buildSummary(log));
//        event.setDescription(buildDescription(log));
//
//        event.setStart(new EventDateTime().setDate(new DateTime(start.toString())));
//        event.setEnd(new EventDateTime().setDate(new DateTime(endExclusive.toString())));
//
//        // 추적용 메타데이터(선택)
//        Map<String, String> priv = new HashMap<>();
//        priv.put("vacationLogId", String.valueOf(log.getVacationLogId()));
//        Event.ExtendedProperties ep = new Event.ExtendedProperties();
//        ep.setPrivate(priv);
//        event.setExtendedProperties(ep);
//
//        return event;
//    }
//
//    private String buildSummary(VacationLog log) {
//        // 가능하면 “사원명 - 휴가종류” 형태로(프론트 표시와 동일)
//        // 엔티티 연관관계에 따라 아래 부분은 프로젝트 구조에 맞게 조정하세요.
//        String employeeName = (log.getEmployee() != null) ? log.getEmployee().getEmployeeName() : "직원";
//        String typeName = (log.getVacationType() != null) ? log.getVacationType().getVacationTypeName() : "휴가";
//        return employeeName + " - " + typeName;
//    }
//
//    private String buildDescription(VacationLog log) {
//        String reason = (log.getReason() != null) ? log.getReason() : "";
//        return "HERO VacationLogId=" + log.getVacationLogId() + "\n" + reason;
//    }
//
//    public record SyncResult(int inserted, int updated, int failed, int total) {
//    }
//}
