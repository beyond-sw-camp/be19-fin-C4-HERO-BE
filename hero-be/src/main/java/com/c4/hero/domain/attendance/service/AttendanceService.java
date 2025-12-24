package com.c4.hero.domain.attendance.service;

import com.c4.hero.common.pagination.PageCalculator;
import com.c4.hero.common.pagination.PageInfo;
import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.attendance.dto.*;
import com.c4.hero.domain.attendance.mapper.AttendanceMapper;
import com.c4.hero.domain.attendance.repository.AttendanceDashboardRepository;
import com.c4.hero.domain.attendance.repository.AttendanceDashboardSummaryRepository;
import com.c4.hero.domain.attendance.repository.DeptWorkSystemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.time.YearMonth;

/**
 * <pre>
 * Class Name: AttendanceService
 * Description: 근태 기록(개인 근태, 초과 근무, 근태 기록 수정 이력, 근무제 정정 이력 등)
 조회 관련 비즈니스 로직을 처리하는 서비스 클래스
 *
 * History
 * 2025/12/09 (이지윤) 최초 작성
 * </pre>
 *
 * @author 이지윤
 * @version 1.1
 */
@Service
@RequiredArgsConstructor
public class AttendanceService {

    /** 근태 정보(개인/초과 근무/정정 등) 조회를 위한 MyBatis Mapper */
    private final AttendanceMapper attendanceMapper;

    /** 부서 근무제/근태 현황 조회용 JPA 레포지토리 */
    private final DeptWorkSystemRepository deptWorkSystemRepository;

    /** 근태 점수 대시보드 조회용 JPA 레포지토리 */
    private final AttendanceDashboardRepository attendanceDashboardRepository;

    private final AttendanceDashboardSummaryRepository  attendanceDashboardSummaryRepository;
    /**
     * 요약 카드용 기본 기간(이번 달 1일~오늘)을 계산한다
     *
     * <p>
     * 컨트롤러에서 전달받은 문자열(yyyy-MM-dd) 기준의 시작일/종료일을
     * 최종적으로 확정한 형태로 보관합니다.
     * </p>
     *
     * @param startDate 조회 시작일(yyyy-MM-dd)
     * @param endDate   조회 종료일(yyyy-MM-dd)
     */
    private record DateRange(LocalDate startDate, LocalDate endDate) {}

    /**
     * 개인 근태 조회용 기간(startDate, endDate)을 확정합니다.
     *
     * <p>규칙</p>
     * <ul>
     *     <li>startDate가 null 또는 공백이면: 이번 달 1일을 시작일로 사용</li>
     *     <li>endDate가 null 또는 공백이면: 오늘(LocalDate.now())을 종료일로 사용</li>
     *     <li>그 외에는 전달받은 문자열(yyyy-MM-dd)을 그대로 사용</li>
     * </ul>
     *
     * @param startDate 요청으로 전달된 시작일(yyyy-MM-dd), null 또는 공백 가능
     * @param endDate   요청으로 전달된 종료일(yyyy-MM-dd), null 또는 공백 가능
     * @return 최종 확정된 시작일/종료일을 담은 DateRange
     */
    private DateRange resolvePersonalPeriod(LocalDate startDate, LocalDate endDate) {
        LocalDate defaultEnd = LocalDate.now();
        LocalDate defaultStart = defaultEnd.withDayOfMonth(1);

        LocalDate finalStartDate =
                (startDate != null) ? startDate :defaultStart;   // yyyy-MM-dd

        LocalDate finalEndDate =
                (endDate != null) ? endDate : defaultEnd;     // yyyy-MM-dd

        return new DateRange(finalStartDate, finalEndDate);
    }

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
     * 개인 근태 상단 요약 카드 조회
     * - 기본: 오늘이 포함된 이번 달(1일 ~ 오늘) 기준
     * - startDate/endDate가 넘어오면 그 기간 기준으로 재계산
     *
     * @param employeeId 직원 ID (토큰에서 꺼낸 값)
     * @param startDate  조회 시작일(yyyy-MM-dd) - 옵션
     * @param endDate    조회 종료일(yyyy-MM-dd) - 옵션
     * @return PersonalSummaryDTO (근무일/오늘 근무제/지각/결근)
     */
    public AttSummaryDTO getPersonalSummary(
            Integer employeeId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        // 0. 기간 보정 공통 메서드 사용
        DateRange range = resolvePersonalPeriod(startDate, endDate);
        LocalDate finalStartDate = range.startDate();
        LocalDate finalEndDate = range.endDate();

        // 1. Mapper 호출
        return attendanceMapper.selectPersonalSummary(
                employeeId,
                finalStartDate,
                finalEndDate
        );
    }

    /**
     * 개인 근태 기록 페이지를 조회합니다.
     *
     * @param employeeId 로그인한 사람의 정보 확인
     * @param page      요청 페이지 번호 (1부터 시작)
     * @param size      페이지당 데이터 개수
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 개인 근태 기록 페이지 응답 DTO
     */
    public PageResponse<PersonalDTO> getPersonalList(
            Integer employeeId,
            Integer page,
            Integer size,
            LocalDate startDate,
            LocalDate endDate
    ) {
        // 1. 최종 사용할 기간 결정
        //    - 값이 있으면 그대로 사용
        //    - 값이 없으면 null 로 넘겨서 쿼리에서 기간 조건을 아예 빼도록 처리
        int totalCount = attendanceMapper.selectPersonalCount(
                employeeId,
                startDate,
                endDate
        );

        // 2. 페이지네이션 계산
        PageInfo pageInfo = PageCalculator.calculate(page, size, totalCount);

        // 3. 현재 페이지 데이터 조회 (★ 여기도 finalStartDate / finalEndDate 사용)
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
     * @param employeeId 로그인한 사람의 정보 확인
     * @param page      요청 페이지 번호 (1부터 시작)
     * @param size      페이지당 데이터 개수
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 초과 근무 기록 페이지 응답 DTO
     */
    public PageResponse<OvertimeDTO> getOvertimeList(
            Integer employeeId,
            Integer page,
            Integer size,
            LocalDate startDate,
            LocalDate endDate
    ) {
        // 1. 전체 개수 조회 (null이면 Mapper 쪽에서 기간 조건 빼도록 구현)
        int totalCount = attendanceMapper.selectPersonalCount(
                employeeId,
                startDate,
                endDate
        );

        // 2. 페이지네이션 계산
        PageInfo pageInfo = PageCalculator.calculate(page, size, totalCount);

        // 3. 현재 페이지 데이터 조회 (★ 여기도 finalStartDate / finalEndDate 사용)
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
     * 초과 근무(연장 근무) 기록 페이지를 조회합니다.
     *
     * @param employeeId 로그인한 사람의 정보 확인
     * @param page      요청 페이지 번호 (1부터 시작)
     * @param size      페이지당 데이터 개수
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 초과 근무 기록 페이지 응답 DTO
     */
    public PageResponse<CorrectionDTO> getCorrectionList(
            Integer employeeId,
            Integer page,
            Integer size,
            LocalDate startDate,
            LocalDate endDate
    ) {
        // 1. 전체 개수 조회 (null이면 Mapper 쪽에서 기간 조건 빼도록 구현)
        int totalCount = attendanceMapper.selectPersonalCount(
                employeeId,
                startDate,
                endDate
        );

        // 2. 페이지네이션 계산
        PageInfo pageInfo = PageCalculator.calculate(page, size, totalCount);

        // 3. 현재 페이지 데이터 조회 (★ 여기도 finalStartDate / finalEndDate 사용)
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
     * 근무제 정정 페이지를 조회합니다.
     *
     * @param employeeId 로그인한 사람의 정보 확인
     * @param page      요청 페이지 번호 (1부터 시작)
     * @param size      페이지당 데이터 개수
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 근무제 정정 기록 페이지 응답 DTO
     */
    public PageResponse<ChangeLogDTO> getChangeLogList(
            Integer employeeId,
            Integer page,
            Integer size,
            LocalDate startDate,
            LocalDate endDate
    ) {
        // 1. 전체 개수 조회 (null이면 Mapper 쪽에서 기간 조건 빼도록 구현)
        int totalCount = attendanceMapper.selectPersonalCount(
                employeeId,
                startDate,
                endDate
        );

        // 3. 페이지네이션 계산
        PageInfo pageInfo = PageCalculator.calculate(page, size, totalCount);

        // 4. 현재 페이지 데이터 조회 (★ 여기도 finalStartDate / finalEndDate 사용)
        List<ChangeLogDTO> items = attendanceMapper.selectChangeLogPage(
                employeeId,
                pageInfo.getOffset(),
                pageInfo.getSize(),
                startDate,
                endDate
        );

        // 5. 공통 PageResponse으로 응답
        return PageResponse.of(
                items,
                pageInfo.getPage() - 1,
                pageInfo.getSize(),
                totalCount
        );
    }

    /**
     * 부서 근태 현황 페이지 조회
     *
     * @param employeeId   로그인한 직원 ID
     * @param departmentId 부서 ID
     * @param workDate     조회 날짜
     * @param page         요청 페이지 번호 (1부터 시작)
     * @param size         페이지 크기
     * @return PageResponse<DeptWorkSystemRowDTO>
     */
    public PageResponse<DeptWorkSystemDTO> getDeptWorkSystemList(
            Integer employeeId,
            Integer departmentId,
            LocalDate workDate,
            int page,
            int size
    ){
        // 1. workDate가 null이면 오늘 날짜로 기본값 처리
        LocalDate targetDate = (workDate != null) ? workDate : LocalDate.now();

        // 2. JPA Pageabel (0-based 인덱스로 변환)
        int pageIndex = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageIndex, size);

        // 3. Repository 조회
        Page<DeptWorkSystemDTO> pageResult = deptWorkSystemRepository.findDeptWorkSystemRows(
                employeeId,
                departmentId,
                targetDate,
                pageable
        );

        //4. PageResponse로 변환(응답 전담)
        return PageResponse.of(
                pageResult.getContent(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements()
        );
    }

    /**
     * 근태 점수 대시보드 페이지 조회
     *
     * - 직원별 지각/결근 횟수와 점수를 조회
     * - departmentId가 null이면 전체 부서 대상
     * - startDate / endDate가 null이면 오늘 날짜로 보정
     *
     * @param departmentId 부서 ID (null이면 전체)
     * @param month        필터링 달
     * @param scoreSort    오름차순/내림차순
     * @param page         요청 페이지 번호 (1부터 시작)
     * @param size         페이지 크기
     * @return 근태 점수 대시보드 페이지 응답
     */
    public PageResponse<AttendanceDashboardDTO>  getAttendanceDashboardList(
            Integer departmentId,
            String month,
            String scoreSort,
            int page,
            int size
    ){
        // 1. 월 -> 기간 변환
        DateRange range = resolveDashboardMonth(month);
        LocalDate finalStart = range.startDate();
        LocalDate finalEnd = range.endDate();

        // 2. scoreSort 기본값 보정
        String finalSort = (scoreSort == null || scoreSort.isBlank() ? "DESC" : scoreSort);

        // 3) Pageable 생성
        int pageIndex = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageIndex, size);

        // 3. Repository 호출
        Page<AttendanceDashboardDTO> pageResult =
                attendanceDashboardRepository.findAttendanceDashboard(
                        departmentId,
                        finalStart,
                        finalEnd,
                        finalSort,
                        pageable
                );

        // 4. 공통 PageResponse로 변환
        return PageResponse.of(
                pageResult.getContent(),
                pageResult.getNumber() ,
                pageResult.getSize(),
                pageResult.getTotalElements()
        );
    }

    public AttendanceDashboardSummaryDTO getAttendanceDashboardSummary(
            Integer departmentId,
            String month   // ✅ "YYYY-MM"
    ) {
        // 1) total은 월 무관 (부서만 반영)
        long total = attendanceDashboardSummaryRepository.countTotalEmployees(departmentId);

        // 2) 우수/위험은 월 기준
        DateRange range = resolveDashboardMonth(month);
        LocalDate startDate = range.startDate();
        LocalDate endDate = range.endDate();

        long excellent = attendanceDashboardSummaryRepository.countExcellentEmployees(
                departmentId, startDate, endDate
        );
        long risky = attendanceDashboardSummaryRepository.countRiskyEmployees(
                departmentId, startDate, endDate
        );

        return new AttendanceDashboardSummaryDTO(total, excellent, risky);
    }
}