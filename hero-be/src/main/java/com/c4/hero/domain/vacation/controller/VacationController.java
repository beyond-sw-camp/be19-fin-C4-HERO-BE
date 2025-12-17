package com.c4.hero.domain.vacation.controller;

import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.vacation.dto.DepartmentVacationDTO;
import com.c4.hero.domain.vacation.dto.VacationHistoryDTO;
import com.c4.hero.domain.vacation.service.VacationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * <pre>
 * Class Name: VacationController
 * Description: 휴가 이력 조회 등 휴가 관련 REST API 요청을 처리하는 컨트롤러
 *
 * History
 * 2025/12/16 (이지윤) 최초 작성 및 백엔드 코딩 컨벤션 적용
 * </pre>
 *
 * 휴가 이력 그래프/리스트 화면에서 사용할
 * 휴가 기록 페이지네이션 조회 기능을 제공합니다.
 * (로그인 연동 전까지는 employeeId 파라미터를 통해 사원 식별)
 *
 * @author 이지윤
 * @version 1.0
 */
@RestController
@RequestMapping("/api/vacation")
@RequiredArgsConstructor
public class VacationController {

    /** 휴가 도메인 비즈니스 로직을 처리하는 서비스 */
    private final VacationService vacationService;

    /**
     * 개인 휴가 이력을 페이지 단위로 조회합니다.
     *
     * <p>특징</p>
     * <ul>
     *     <li>employeeId 기준으로 특정 직원의 휴가 이력 조회 (로그인 전까지는 파라미터로 전달)</li>
     *     <li>시작일/종료일({@code startDate}, {@code endDate})는 yyyy-MM-dd 형태로 수신</li>
     *     <li>서비스 계층에서는 {@link LocalDateTime}을 사용하므로 컨트롤러에서 변환</li>
     *     <li>페이지/사이즈 기반 페이지네이션 지원</li>
     * </ul>
     *
     * @param employeeId 조회할 직원 ID (null 허용, 추후 인증 정보로 대체 가능)
     * @param startDate  조회 시작일(yyyy-MM-dd), null인 경우 시작일 제한 없음
     * @param endDate    조회 종료일(yyyy-MM-dd), null인 경우 종료일 제한 없음
     * @param page       요청 페이지 번호 (1부터 시작)
     * @param size       페이지당 데이터 개수
     * @return 휴가 이력 DTO 리스트 및 페이지 정보가 포함된 응답
     */
    @GetMapping("/history")
    public PageResponse<VacationHistoryDTO> getVacationHistory(
            @RequestParam(name = "employeeId", required = false) Integer employeeId,
            @RequestParam(name = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        // LocalDate → LocalDateTime 변환 (하루의 시작/끝으로 확장)
        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;

        return vacationService.findVacationHistory(
                employeeId,
                startDateTime,
                endDateTime,
                page,
                size
        );
    }

    /**
     * 부서 휴가 캘린더(월 단위)를 조회합니다.
     *
     * <p>예시 요청</p>
     * <pre>
     * GET /api/vacation/department/calendar?departmentId=1&year=2025&month=12
     * </pre>
     *
     * <p>특징</p>
     * <ul>
     *     <li>year/month가 없으면 서버 기준 현재 연/월 기준으로 조회</li>
     *     <li>departmentId가 없으면 전체 부서를 대상으로 휴가 정보 조회 (필터 미적용)</li>
     *     <li>일자별/직원별 휴가 현황을 캘린더 형태로 표현하기 위한 데이터 제공</li>
     * </ul>
     *
     * @param departmentId 조회 대상 부서 ID (null인 경우 전체 부서)
     * @param year         조회 연도 (null인 경우 현재 연도)
     * @param month        조회 월 (1~12, null인 경우 현재 월)
     * @return 부서 휴가 캘린더 구성을 위한 휴가 정보 리스트
     */
    @GetMapping("/department/calendar")
    public List<DepartmentVacationDTO> getDepartmentVacationCalendar(
            @RequestParam(name = "departmentId", required = false) Integer departmentId,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "month", required = false) Integer month
    ) {
        return vacationService.findDepartmentVacationCalendar(departmentId, year, month);
    }
}
