package com.c4.hero.domain.attendance.service;

import com.c4.hero.common.exception.EntityNotFoundException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.common.pagination.PageCalculator;
import com.c4.hero.common.pagination.PageInfo;
import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.attendance.dto.AttSummaryDTO;
import com.c4.hero.domain.attendance.dto.AttendanceDashboardDTO;
import com.c4.hero.domain.attendance.dto.AttendanceDashboardSummaryDTO;
import com.c4.hero.domain.attendance.dto.AttendanceEmployeeHalfDashboardDTO;
import com.c4.hero.domain.attendance.dto.AttendanceEmployeeHalfSummaryDTO;
import com.c4.hero.domain.attendance.dto.AttendanceEmployeeMonthlyStatDTO;
import com.c4.hero.domain.attendance.dto.ChangeLogDTO;
import com.c4.hero.domain.attendance.dto.CorrectionDTO;
import com.c4.hero.domain.attendance.dto.DeptWorkSystemDTO;
import com.c4.hero.domain.attendance.dto.OvertimeDTO;
import com.c4.hero.domain.attendance.dto.PersonalDTO;
import com.c4.hero.domain.attendance.entity.Attendance;
import com.c4.hero.domain.attendance.mapper.AttendanceMapper;
import com.c4.hero.domain.attendance.repository.AttendanceDashboardRepository;
import com.c4.hero.domain.attendance.repository.AttendanceDashboardSummaryRepository;
import com.c4.hero.domain.attendance.repository.AttendanceEmployeeDashboardRepository;
import com.c4.hero.domain.attendance.repository.DeptWorkSystemRepository;
import com.c4.hero.domain.attendance.type.AttendanceHalfType;
import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <pre>
 * Class Name: AttendanceService
 * Description: 근태 기록(개인 근태, 초과 근무, 근태 정정 이력, 근무제 변경 이력 등)
 *              조회 관련 비즈니스 로직을 처리하는 서비스 클래스
 *
 * History
 * 2025/12/09 (이지윤) 최초 작성
 * 2025/12/24 (이지윤) 대시보드/반기 대시보드/요약 카드 로직 추가 및 컨벤션 정리
 * 2026/01/07 (민철) 근태 이력 수정 로직 추가
 * </pre>
 *
 * 개인/부서 단위의 근태 이력 및 각종 요약/대시보드 데이터를 조회하는 도메인 서비스입니다.
 * - 개인 탭: 근태 요약, 근태 이력, 초과 근무, 근태 정정, 근무제 변경 이력
 * - 부서 탭: 부서 근태 현황(당일), 근태 점수 대시보드(월별), 요약 통계
 * - 반기 대시보드: 직원 1인의 반기(상/하반기) 근태 통계
 * MyBatis Mapper + JPA Repository 기반으로 조회를 수행하며,
 * 공통 페이지네이션/기간 보정 로직을 내부에서 처리합니다.
 *
 * @author 이지윤
 * @version 1.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceService {

    /** 근태 정보(개인/초과 근무/정정 등) 조회를 위한 MyBatis Mapper */
    private final AttendanceMapper attendanceMapper;

    /** 부서 근무제/근태 현황 조회용 JPA 레포지토리 */
    private final DeptWorkSystemRepository deptWorkSystemRepository;

    /** 근태 점수 대시보드 조회용 JPA 레포지토리 */
    private final AttendanceDashboardRepository attendanceDashboardRepository;

    /** 근태 점수 대시보드 상단 요약 통계 조회용 JPA 레포지토리 */
    private final AttendanceDashboardSummaryRepository attendanceDashboardSummaryRepository;

    /** 직원 반기 대시보드(요약/월별 통계) 조회용 JPA 레포지토리 */
    private final AttendanceEmployeeDashboardRepository attendanceEmployeeDashboardRepository;

    /** 직원 기본 정보 조회용 JPA 레포지토리 */
    private final EmployeeRepository employeeRepository;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    /** 근태 이력 수정 (지각, 결근 -> 정상) */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void changeStatus(Integer drafterId, String detailsJson) {
        try {
            JsonNode root = objectMapper.readTree(detailsJson);

            int attendanceId = root.path("attendanceId").asInt(0);
            String targetDateStr = root.path("targetDate").asText("");
            String correctedStartStr = root.path("correctedStart").asText("00:00");
            String correctedEndStr = root.path("correctedEnd").asText("00:00");

            LocalDate targetDate = LocalDate.parse(targetDateStr);
            LocalTime correctedStart = parseLocalTimeOrNull(correctedStartStr);
            LocalTime correctedEnd = parseLocalTimeOrNull(correctedEndStr);


            Attendance attendanceEntity;
            if (attendanceId == 0) {
                attendanceEntity = attendanceEmployeeDashboardRepository
                        .findByEmployee_EmployeeIdAndWorkDate(drafterId, targetDate);

                if (attendanceEntity == null) {
                    throw new EntityNotFoundException(
                            ErrorCode.ENTITY_NOT_FOUND,
                            "해당 날짜의 근태 기록을 찾을 수 없습니다. employeeId=" + drafterId + ", date=" + targetDate
                    );
                }
            } else {
                attendanceEntity = attendanceEmployeeDashboardRepository.findById(attendanceId)
                        .orElseThrow(() -> new EntityNotFoundException(
                                ErrorCode.ENTITY_NOT_FOUND,
                                "근태 기록을 찾을 수 없습니다. attendanceId=" + attendanceId
                        ));
            }

            Integer breakMinMinutes = attendanceMapper.selectBreakMinMinutes(attendanceId);
            log.info("휴게시간{}",breakMinMinutes);
            if (breakMinMinutes == null) {
                breakMinMinutes = 0;
            }

            Integer workDuration = null;
            if (correctedStart != null && correctedEnd != null) {
                long totalMinutes = Duration.between(correctedStart, correctedEnd).toMinutes();
                workDuration = (int) (totalMinutes - breakMinMinutes);

                if (workDuration < 0) {
                    workDuration = 0;
                }

                if (totalMinutes < breakMinMinutes) {
                    workDuration = (int) totalMinutes;
                }
            }

            attendanceEntity.changeStatus( "정상", workDuration);
            attendanceEmployeeDashboardRepository.save(attendanceEntity);
        } catch (JsonProcessingException e) {
            log.error("근태 상세정보 JSON 파싱 실패. drafterId={}, details={}",
                    drafterId, detailsJson, e);
            throw new IllegalArgumentException("근태이력 수정 데이터 형식이 올바르지 않습니다.", e);
        } catch (Exception e) {
            log.error("근태이력 수정 처리 실패. drafterId={}, details={}",
                    drafterId, detailsJson, e);
            throw new IllegalStateException("근태이력 수정 중 오류가 발생했습니다.", e);
        }
    }

    /** 시간 파싱 */
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
     * 근태 조회용 기간(startDate, endDate)을 표현하는 내부 레코드입니다.
     *
     * <p>
     * LocalDate 기준의 시작일/종료일을 한 번에 전달하기 위해 사용합니다.
     * </p>
     *
     * @param startDate 조회 시작일
     * @param endDate   조회 종료일
     */
    private record DateRange(LocalDate startDate, LocalDate endDate) { }

    /**
     * 개인 근태 조회용 기간(startDate, endDate)을 확정합니다.
     *
     * <p>규칙</p>
     * <ul>
     *     <li>startDate가 null이면: 이번 달 1일을 시작일로 사용</li>
     *     <li>endDate가 null이면: 오늘(LocalDate.now())을 종료일로 사용</li>
     *     <li>값이 있으면 전달받은 LocalDate를 그대로 사용</li>
     * </ul>
     *
     * @param startDate 요청으로 전달된 시작일(LocalDate), null 가능
     * @param endDate   요청으로 전달된 종료일(LocalDate), null 가능
     * @return 최종 확정된 시작일/종료일을 담은 DateRange
     */
    private DateRange resolvePersonalPeriod(LocalDate startDate, LocalDate endDate) {
        LocalDate defaultEnd = LocalDate.now();
        LocalDate defaultStart = defaultEnd.withDayOfMonth(1);

        LocalDate finalStartDate = (startDate != null) ? startDate : defaultStart;
        LocalDate finalEndDate = (endDate != null) ? endDate : defaultEnd;

        return new DateRange(finalStartDate, finalEndDate);
    }

    /**
     * "YYYY-MM" 문자열 기준으로 월 단위 대시보드 기간(start~end)을 계산합니다.
     *
     * <p>규칙</p>
     * <ul>
     *     <li>month가 null/공백이면: 현재 월 기준</li>
     *     <li>start: 해당 월 1일</li>
     *     <li>end: 해당 월 말일, 단 현재 월인 경우 오늘(LocalDate.now())까지만 사용</li>
     * </ul>
     *
     * @param month "YYYY-MM" 형식 문자열 (예: "2025-12"), null/공백 가능
     * @return 월 단위 시작일/종료일을 담은 DateRange
     */
    private DateRange resolveDashboardMonth(String month) {
        YearMonth ym = (month != null && !month.isBlank())
                ? YearMonth.parse(month)   // "2025-12"
                : YearMonth.now();

        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        // 현재 월이면 미래 날짜 제외(오늘까지)
        if (ym.equals(YearMonth.now())) {
            LocalDate today = LocalDate.now();
            if (end.isAfter(today)) {
                end = today;
            }
        }

        return new DateRange(start, end);
    }

    /**
     * 연도 + 반기(H1/H2)를 기준으로 반기 기간(start~end)을 계산합니다.
     *
     * <p>규칙</p>
     * <ul>
     *     <li>year가 null이면: 현재 연도 기준</li>
     *     <li>halfType이 null이면: H1(상반기) 기준</li>
     *     <li>H1: 1월 1일 ~ 6월 30일, H2: 7월 1일 ~ 12월 31일</li>
     *     <li>현재 연도의 진행 중인 반기라면 end는 오늘(LocalDate.now())까지만 사용</li>
     * </ul>
     *
     * @param year     조회 연도 (null 가능)
     * @param halfType 반기 타입(H1/H2, null 가능)
     * @return 반기 시작일/종료일을 담은 DateRange
     */
    private DateRange resolveHalfPeriod(Integer year, AttendanceHalfType halfType) {
        int finalYear = (year != null) ? year : LocalDate.now().getYear();
        AttendanceHalfType finalHalf = (halfType != null) ? halfType : AttendanceHalfType.H1;

        LocalDate startDate;
        LocalDate endDate;

        if (finalHalf == AttendanceHalfType.H1) {
            startDate = LocalDate.of(finalYear, 1, 1);
            endDate = LocalDate.of(finalYear, 6, 30);
        } else {
            startDate = LocalDate.of(finalYear, 7, 1);
            endDate = LocalDate.of(finalYear, 12, 31);
        }

        // 올해면 미래 날짜 제외(오늘까지)
        LocalDate today = LocalDate.now();
        if (finalYear == today.getYear() && endDate.isAfter(today)) {
            endDate = today;
        }

        return new DateRange(startDate, endDate);
    }

    /**
     * 반기(H1/H2) 기준으로 누락된 월을 0 데이터로 채워 차트용 월별 리스트를 완성합니다.
     *
     * <p>
     * DB에서 조회된 월별 집계(rows)에 없는 월에 대해서는
     * workDays/tardyCount/absenceCount가 0인 DTO를 생성하여 추가합니다.
     * </p>
     *
     * @param year  기준 연도
     * @param half  반기 타입(H1/H2)
     * @param rows  실제 집계 데이터(월별)
     * @return 1~6월(H1) 또는 7~12월(H2) 기준으로 빠진 월 없이 정렬된 리스트
     */
    private List<AttendanceEmployeeMonthlyStatDTO> fillMissingMonths(
            int year,
            AttendanceHalfType half,
            List<AttendanceEmployeeMonthlyStatDTO> rows
    ) {
        Map<Integer, AttendanceEmployeeMonthlyStatDTO> map = rows.stream()
                .collect(Collectors.toMap(AttendanceEmployeeMonthlyStatDTO::getMonth, r -> r));

        int startMonth = (half == AttendanceHalfType.H1) ? 1 : 7;
        int endMonth = (half == AttendanceHalfType.H1) ? 6 : 12;

        // 올해면 미래 달 제외
        LocalDate today = LocalDate.now();
        if (year == today.getYear()) {
            int currentMonth = today.getMonthValue();
            if (endMonth > currentMonth) {
                endMonth = currentMonth;
            }
        }

        List<AttendanceEmployeeMonthlyStatDTO> result = new ArrayList<>();
        for (int m = startMonth; m <= endMonth; m++) {
            AttendanceEmployeeMonthlyStatDTO r = map.get(m);

            long workDays = (r != null) ? r.getWorkDays() : 0L;
            long tardy = (r != null) ? r.getTardyCount() : 0L;
            long absence = (r != null) ? r.getAbsenceCount() : 0L;

            result.add(new AttendanceEmployeeMonthlyStatDTO(m, workDays, tardy, absence));
        }

        return result;
    }

    /**
     * 개인 근태 상단 요약 카드 데이터를 조회합니다.
     *
     * <p>기본 규칙</p>
     * <ul>
     *     <li>startDate/endDate가 null이면: 이번 달 1일 ~ 오늘 기준으로 조회</li>
     *     <li>그 외에는 전달받은 기간(startDate ~ endDate) 기준으로 조회</li>
     * </ul>
     *
     * @param employeeId 직원 ID (JWT 토큰에서 추출된 값)
     * @param startDate  조회 시작일(옵션)
     * @param endDate    조회 종료일(옵션)
     * @return 개인 근태 요약 정보(근무일/오늘 근무제/지각/결근 등)
     */
    public AttSummaryDTO getPersonalSummary(
            Integer employeeId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        DateRange range = resolvePersonalPeriod(startDate, endDate);
        LocalDate finalStartDate = range.startDate();
        LocalDate finalEndDate = range.endDate();

        return attendanceMapper.selectPersonalSummary(
                employeeId,
                finalStartDate,
                finalEndDate
        );
    }

    /**
     * 개인 근태 기록 페이지를 조회합니다.
     *
     * @param employeeId 직원 ID (JWT 토큰에서 추출된 값)
     * @param page       요청 페이지 번호 (1부터 시작)
     * @param size       페이지당 데이터 개수
     * @param startDate  조회 시작일(옵션, null이면 기간 필터 미적용)
     * @param endDate    조회 종료일(옵션, null이면 기간 필터 미적용)
     * @return 개인 근태 기록 페이지 응답 DTO
     */
    public PageResponse<PersonalDTO> getPersonalList(
            Integer employeeId,
            Integer page,
            Integer size,
            LocalDate startDate,
            LocalDate endDate
    ) {
        // 1. 전체 개수 조회
        int totalCount = attendanceMapper.selectPersonalCount(
                employeeId,
                startDate,
                endDate
        );

        // 2. 페이지네이션 계산
        PageInfo pageInfo = PageCalculator.calculate(page, size, totalCount);

        // 3. 현재 페이지 데이터 조회
        List<PersonalDTO> items = attendanceMapper.selectPersonalPage(
                employeeId,
                pageInfo.getOffset(),
                pageInfo.getSize(),
                startDate,
                endDate
        );

        // 4. 공통 PageResponse으로 응답
        return PageResponse.of(
                items,
                pageInfo.getPage() - 1,
                pageInfo.getSize(),
                totalCount
        );
    }

    /**
     * 초과 근무(연장 근무) 기록 페이지를 조회합니다.
     *
     * @param employeeId 직원 ID (JWT 토큰에서 추출된 값)
     * @param page       요청 페이지 번호 (1부터 시작)
     * @param size       페이지당 데이터 개수
     * @param startDate  조회 시작일(옵션, null이면 기간 필터 미적용)
     * @param endDate    조회 종료일(옵션, null이면 기간 필터 미적용)
     * @return 초과 근무 기록 페이지 응답 DTO
     */
    public PageResponse<OvertimeDTO> getOvertimeList(
            Integer employeeId,
            Integer page,
            Integer size,
            LocalDate startDate,
            LocalDate endDate
    ) {
        // 1. 전체 개수 조회
        int totalCount = attendanceMapper.selectOvertimeCount(
                employeeId,
                startDate,
                endDate
        );

        // 2. 페이지네이션 계산
        PageInfo pageInfo = PageCalculator.calculate(page, size, totalCount);

        // 3. 현재 페이지 데이터 조회
        List<OvertimeDTO> items = attendanceMapper.selectOvertimePage(
                employeeId,
                pageInfo.getOffset(),
                pageInfo.getSize(),
                startDate,
                endDate
        );

        // 4. 공통 PageResponse으로 응답
        return PageResponse.of(
                items,
                pageInfo.getPage() - 1,
                pageInfo.getSize(),
                totalCount
        );
    }

    /**
     * 근태 정정 요청 이력 페이지를 조회합니다.
     *
     * @param employeeId 직원 ID (JWT 토큰에서 추출된 값)
     * @param page       요청 페이지 번호 (1부터 시작)
     * @param size       페이지당 데이터 개수
     * @param startDate  조회 시작일(옵션, null이면 기간 필터 미적용)
     * @param endDate    조회 종료일(옵션, null이면 기간 필터 미적용)
     * @return 근태 정정 이력 페이지 응답 DTO
     */
    public PageResponse<CorrectionDTO> getCorrectionList(
            Integer employeeId,
            Integer page,
            Integer size,
            LocalDate startDate,
            LocalDate endDate
    ) {
        // 1. 전체 개수 조회
        int totalCount = attendanceMapper.selectCorrectionCount(
                employeeId,
                startDate,
                endDate
        );

        // 2. 페이지네이션 계산
        PageInfo pageInfo = PageCalculator.calculate(page, size, totalCount);

        // 3. 현재 페이지 데이터 조회
        List<CorrectionDTO> items = attendanceMapper.selectCorrectionPage(
                employeeId,
                pageInfo.getOffset(),
                pageInfo.getSize(),
                startDate,
                endDate
        );

        // 4. 공통 PageResponse으로 응답
        return PageResponse.of(
                items,
                pageInfo.getPage() - 1,
                pageInfo.getSize(),
                totalCount
        );
    }

    /**
     * 근무제 변경(Change Log) 이력 페이지를 조회합니다.
     *
     * @param employeeId 직원 ID (JWT 토큰에서 추출된 값)
     * @param page       요청 페이지 번호 (1부터 시작)
     * @param size       페이지당 데이터 개수
     * @param startDate  조회 시작일(옵션, null이면 기간 필터 미적용)
     * @param endDate    조회 종료일(옵션, null이면 기간 필터 미적용)
     * @return 근무제 변경 이력 페이지 응답 DTO
     */
    public PageResponse<ChangeLogDTO> getChangeLogList(
            Integer employeeId,
            Integer page,
            Integer size,
            LocalDate startDate,
            LocalDate endDate
    ) {
        // 1. 전체 개수 조회
        int totalCount = attendanceMapper.selectChangeLogCount(
                employeeId,
                startDate,
                endDate
        );

        // 2. 페이지네이션 계산
        PageInfo pageInfo = PageCalculator.calculate(page, size, totalCount);

        // 3. 현재 페이지 데이터 조회
        List<ChangeLogDTO> items = attendanceMapper.selectChangeLogPage(
                employeeId,
                pageInfo.getOffset(),
                pageInfo.getSize(),
                startDate,
                endDate
        );

        // 4. 공통 PageResponse으로 응답
        return PageResponse.of(
                items,
                pageInfo.getPage() - 1,
                pageInfo.getSize(),
                totalCount
        );
    }

    /**
     * 부서 근태 현황 페이지를 조회합니다.
     *
     * <p>특징</p>
     * <ul>
     *     <li>특정 부서({@code departmentId}) + 특정 날짜({@code workDate}) 기준</li>
     *     <li>직원별 근무제/근무시간/상태 등을 조회</li>
     *     <li>JPA 기반 페이지네이션 지원</li>
     * </ul>
     *
     * @param employeeId   로그인한 직원 ID (권한 체크 등에 활용 가능)
     * @param departmentId 조회 대상 부서 ID
     * @param workDate     조회 날짜 (null이면 오늘 기준)
     * @param page         요청 페이지 번호 (1부터 시작)
     * @param size         페이지 크기
     * @return 부서 근태 현황 페이지 응답 DTO
     */
    public PageResponse<DeptWorkSystemDTO> getDeptWorkSystemList(
            Integer employeeId,
            Integer departmentId,
            LocalDate workDate,
            int page,
            int size
    ) {
        // 1. workDate가 null이면 오늘 날짜로 기본값 처리
        LocalDate targetDate = (workDate != null) ? workDate : LocalDate.now();

        // 2. JPA Pageable (0-based 인덱스로 변환)
        int pageIndex = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageIndex, size);

        // 3. Repository 조회
        Page<DeptWorkSystemDTO> pageResult = deptWorkSystemRepository.findDeptWorkSystemRows(
                employeeId,
                departmentId,
                targetDate,
                pageable
        );

        // 4. PageResponse로 변환
        return PageResponse.of(
                pageResult.getContent(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements()
        );
    }

    /**
     * 근태 점수 대시보드(직원별 점수 리스트)를 조회합니다.
     *
     * <p>기본 규칙</p>
     * <ul>
     *     <li>month: "YYYY-MM" 형식 문자열 (예: "2025-12")</li>
     *     <li>departmentId가 null이면 전체 부서 대상</li>
     *     <li>scoreSort: "ASC" 또는 "DESC" (null/공백이면 "DESC")</li>
     *     <li>기간은 month 기준으로 시작일/종료일을 계산하여 조회</li>
     * </ul>
     *
     * @param departmentId 부서 ID (null이면 전체 부서)
     * @param month        조회 월("YYYY-MM"), null이면 현재 월 기준
     * @param scoreSort    점수 정렬 방향("ASC"/"DESC")
     * @param page         요청 페이지 번호 (1부터 시작)
     * @param size         페이지 크기
     * @return 근태 점수 대시보드 페이지 응답 DTO
     */
    public PageResponse<AttendanceDashboardDTO> getAttendanceDashboardList(
            Integer departmentId,
            String month,
            String scoreSort,
            int page,
            int size
    ) {
        // 1. 월 -> 기간 변환
        DateRange range = resolveDashboardMonth(month);
        LocalDate finalStart = range.startDate();
        LocalDate finalEnd = range.endDate();

        // 2. scoreSort 기본값 보정
        String finalSort = (scoreSort == null || scoreSort.isBlank()) ? "DESC" : scoreSort;

        // 3. Pageable 생성 (0-based)
        int pageIndex = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageIndex, size);

        // 4. Repository 호출
        Page<AttendanceDashboardDTO> pageResult =
                attendanceDashboardRepository.findAttendanceDashboard(
                        departmentId,
                        finalStart,
                        finalEnd,
                        finalSort,
                        pageable
                );

        // 5. 공통 PageResponse로 변환
        return PageResponse.of(
                pageResult.getContent(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements()
        );
    }

    /**
     * 근태 점수 대시보드 상단 요약(전체/우수/위험 직원 수)을 조회합니다.
     *
     * <p>규칙</p>
     * <ul>
     *     <li>전체 직원 수: 월과 무관, 부서(departmentId) 기준만 반영</li>
     *     <li>우수/위험 직원 수: month 기준 기간에 대해 점수를 계산하여 집계</li>
     * </ul>
     *
     * @param departmentId 부서 ID (null이면 전체 부서)
     * @param month        조회 월("YYYY-MM"), null이면 현재 월 기준
     * @return 근태 대시보드 요약 DTO
     */
    public AttendanceDashboardSummaryDTO getAttendanceDashboardSummary(
            Integer departmentId,
            String month
    ) {
        // 1. total은 월 무관 (부서만 반영)
        long total = attendanceDashboardSummaryRepository.countTotalEmployees(departmentId);

        // 2. 우수/위험은 월 기준
        DateRange range = resolveDashboardMonth(month);
        LocalDate startDate = range.startDate();
        LocalDate endDate = range.endDate();

        long excellent = attendanceDashboardSummaryRepository.countExcellentEmployees(
                departmentId,
                startDate,
                endDate
        );
        long risky = attendanceDashboardSummaryRepository.countRiskyEmployees(
                departmentId,
                startDate,
                endDate
        );

        return new AttendanceDashboardSummaryDTO(total, excellent, risky);
    }

    /**
     * 직원 1명의 반기(상/하반기) 근태 대시보드 데이터를 조회합니다.
     *
     * <p>반기 대시보드 구성</p>
     * <ul>
     *     <li>Drawer 헤더: 직원 기본 정보(사번, 이름 등)</li>
     *     <li>상단 요약 카드: 해당 반기의 근무일/지각/결근 요약</li>
     *     <li>월별 통계 리스트: 반기 내 각 월의 근무일/지각/결근 수</li>
     * </ul>
     *
     * @param employeeId 직원 ID
     * @param year       조회 연도 (null이면 현재 연도)
     * @param halfType   반기 타입(H1/H2, null이면 기본 H1)
     * @return 직원 반기 근태 대시보드 DTO
     */
    public AttendanceEmployeeHalfDashboardDTO getEmployeeHalfDashboard(
            Integer employeeId,
            Integer year,
            AttendanceHalfType halfType
    ) {
        int finalYear = (year != null) ? year : LocalDate.now().getYear();
        AttendanceHalfType finalHalf = (halfType != null) ? halfType : AttendanceHalfType.H1;

        DateRange range = resolveHalfPeriod(finalYear, finalHalf);
        LocalDate startDate = range.startDate();
        LocalDate endDate = range.endDate();

        // 1. 직원 기본 정보 (Drawer 헤더)
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("직원 정보 없음: " + employeeId));

        // 2. 반기 요약(상단 카드)
        AttendanceEmployeeHalfSummaryDTO summary =
                attendanceEmployeeDashboardRepository.findEmployeeHalfSummary(employeeId, startDate, endDate);

        if (summary == null) {
            summary = new AttendanceEmployeeHalfSummaryDTO(0L, 0L, 0L);
        }

        // 3. 월별 집계(차트) + 누락 월 0 채우기
        List<AttendanceEmployeeMonthlyStatDTO> monthlyRows =
                attendanceEmployeeDashboardRepository.findEmployeeMonthlyStats(employeeId, startDate, endDate);

        List<AttendanceEmployeeMonthlyStatDTO> monthlyStats =
                fillMissingMonths(finalYear, finalHalf, monthlyRows);

        // 4. 최종 응답 DTO
        return new AttendanceEmployeeHalfDashboardDTO(
                employeeId,
                employee.getEmployeeNumber(),
                employee.getEmployeeName(),
                finalYear,
                finalHalf,
                summary,
                monthlyStats
        );
    }
}