package com.c4.hero.domain.vacation.controller;

import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.auth.security.JwtUtil;
import com.c4.hero.domain.vacation.dto.DepartmentVacationDTO;
import com.c4.hero.domain.vacation.dto.VacationHistoryDTO;
import com.c4.hero.domain.vacation.dto.VacationSummaryDTO;
import com.c4.hero.domain.vacation.service.VacationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * <pre>
 * Class Name: VacationController
 * Description: 휴가 이력 조회 등 휴가 관련 REST API 요청을 처리하는 컨트롤러
 *
 * History
 * 2025/12/16 (이지윤) 최초 작성 및 백엔드 코딩 컨벤션 적용
 * 2025/12/23 (수정) Google Calendar 동기화 엔드포인트 추가
 * </pre>
 *
 * @author 이지윤
 * @version 1.1
 */
@RestController
@RequestMapping("/api/vacation")
@RequiredArgsConstructor
public class VacationController {

    /** 휴가 도메인 비즈니스 로직을 처리하는 서비스 */
    private final VacationService vacationService;
    private final JwtUtil jwtUtil;

    private Integer getEmployeeIdFromToken(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        return jwtUtil.getEmployeeId(token);
    }

    /**
     * 개인 휴가 이력을 페이지 단위로 조회합니다.
     */
    @GetMapping("/history")
    public PageResponse<VacationHistoryDTO> getVacationHistory(
            HttpServletRequest request,
            @RequestParam(name = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        Integer employeeId = getEmployeeIdFromToken(request);

        return vacationService.findVacationHistory(
                employeeId,
                startDate,
                endDate,
                page,
                size
        );
    }

    /**
     * 부서 휴가 캘린더(월 단위)를 조회합니다.
     */
    @GetMapping("/department/calendar")
    public List<DepartmentVacationDTO> getDepartmentVacationCalendar(
            HttpServletRequest request,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "month", required = false) Integer month
    ) {
        Integer employeeId = getEmployeeIdFromToken(request);
        return vacationService.findDepartmentVacationCalendar(employeeId, year, month);
    }

    /**
     * 로그인한 사용자의 휴가 요약 정보를 조회합니다.
     */
    @GetMapping("/summary")
    public VacationSummaryDTO getVacationSummary(HttpServletRequest request) {
        Integer employeeId = getEmployeeIdFromToken(request);
        return vacationService.findVacationLeaves(employeeId);
    }

    // ======================================================================
    // Google Calendar Sync (테스트/운영 대비용으로 남겨두는 엔드포인트)
    // ======================================================================

    /**
     * 특정 월의 휴가 로그를 Google Calendar에 동기화합니다.
     *
     * <p>예시</p>
     * POST /api/vacation/google-calendar/sync?year=2025&month=12
     *
     * <p>주의</p>
     * - 현재는 인증만 타고(토큰 필요), 권한(관리자 전용)은 별도 설정 권장
     *
     * @param request JWT 토큰 확인용
     * @param year    동기화 대상 연도 (필수)
     * @param month   동기화 대상 월 1~12 (필수)
     * @return inserted/updated/failed/total
     */
    @PostMapping("/google-calendar/sync")
    public VacationService.SyncResult syncGoogleCalendar(
            HttpServletRequest request,
            @RequestParam(name = "year") int year,
            @RequestParam(name = "month") int month
    ) {
        // 토큰이 유효한지 최소 체크(사용자 식별)
        // 지금은 반환값을 쓰진 않지만, 인증이 안되면 여기서 예외 발생하도록 유지
        getEmployeeIdFromToken(request);

        return vacationService.syncVacationLogsToGoogleCalendar(year, month);
    }
}
