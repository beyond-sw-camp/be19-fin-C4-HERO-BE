package com.c4.hero.domain.attendance.controller;

import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.attendance.dto.AttendanceDashboardDTO;
import com.c4.hero.domain.attendance.dto.ChangeLogDTO;
import com.c4.hero.domain.attendance.dto.CorrectionDTO;
import com.c4.hero.domain.attendance.dto.DeptWorkSystemDTO;
import com.c4.hero.domain.attendance.dto.OvertimeDTO;
import com.c4.hero.domain.attendance.dto.PersonalDTO;
import com.c4.hero.domain.attendance.dto.AttSummaryDTO;
import com.c4.hero.domain.attendance.service.AttendanceService;
import com.c4.hero.domain.auth.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * <pre>
 * Class Name: AttendanceController
 * Description: 근태 기록(개인 근태 포함) 관련 요청을 처리하는 REST 컨트롤러
 *
 * History
 * 2025/12/09 (이지윤) 최초 작성
 * </pre>
 *
 * @author 이지윤
 * @version 1.1
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendance")
public class AttendanceController {

    /** 근태 관련 비즈니스 로직 처리 서비스 */
    private final AttendanceService attendanceService;
    private final JwtUtil jwtUtil;

    /**
     * HTTP 요청 헤더에서 JWT 토큰을 추출하고, 토큰으로부터 employeeId를 반환합니다.
     *
     * <p>
     * 인증이 완료된 요청을 전제로 하며,
     * 내부적으로 {@code jwtUtil.resolveToken(request)} 를 통해 토큰을 추출하고,
     * {@code jwtUtil.getEmployeeId(token)} 으로 사번(직원 ID)을 파싱합니다.
     * </p>
     *
     * @param request 현재 HTTP 요청
     * @return JWT에 포함된 직원 ID
     */
    private Integer getEmployeeIdFromToken(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);

        return jwtUtil.getEmployeeId(token);
    }

    /**
     * 개인 근태 요약 정보를 조회합니다.
     *
     * <p>특징</p>
     * <ul>
     *     <li>JWT 토큰에서 직원 ID를 추출하여, 로그인한 본인의 근태 요약을 조회</li>
     *     <li>{@code startDate}, {@code endDate}는 선택적으로 전달 가능 (yyyy-MM-dd 형식)</li>
     *     <li>날짜가 전달되지 않으면 서비스 계층에서 기본 기간(예: 이번 달 기준)을 적용</li>
     * </ul>
     *
     * @param request   HTTP 요청 (JWT 토큰 추출용)
     * @param startDate 조회 시작일(yyyy-MM-dd), null/미전달 시 기본값 사용
     * @param endDate   조회 종료일(yyyy-MM-dd), null/미전달 시 기본값 사용
     * @return 개인 근태 요약 DTO
     */
    @GetMapping("/personal/summary")
    public AttSummaryDTO getPersonalSummary(
            HttpServletRequest request,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        Integer employeeId = getEmployeeIdFromToken(request);

        return attendanceService.getPersonalSummary(employeeId, startDate, endDate);
    }

    /**
     * 개인 근태 기록 목록(페이지)을 조회합니다.
     *
     * @param request 로그인한 정보를 받아오는 부분
     * @param page      조회할 페이지 번호 (1부터 시작)
     * @param size      한 페이지당 조회할 데이터 개수
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 개인 근태 기록 페이지 응답 DTO
     */
    @GetMapping("/personal")
    public PageResponse<PersonalDTO> getPersonalList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        Integer employeeId = getEmployeeIdFromToken(request);

        return attendanceService.getPersonalList(employeeId, page, size, startDate, endDate);
    }


    @GetMapping("/overtime")
    public PageResponse<OvertimeDTO> getOvertimeList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ){
        Integer employeeId = getEmployeeIdFromToken(request);

        return attendanceService.getOvertimeList(employeeId, page, size, startDate, endDate);
    }

    @GetMapping("/correction")
    public PageResponse<CorrectionDTO> getCorrectionList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ){
        Integer employeeId = getEmployeeIdFromToken(request);

        return attendanceService.getCorrectionList(employeeId, page, size, startDate, endDate);
    }

    @GetMapping("/changelog")
    public PageResponse<ChangeLogDTO> getChangeLogList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ){
        Integer employeeId = getEmployeeIdFromToken(request);

        return attendanceService.getChangeLogList(employeeId, page, size, startDate, endDate);
    }

    /**
     * 부서 근태 현황 조회
     *
     * @param departmentId 부서 ID
     * @param workDate     조회 날짜 (yyyy-MM-dd)
     * @param page         페이지 번호 (1부터 시작, 기본값 1)
     * @param size         페이지 크기 (기본값 10)
     * @return PageResponse<DeptWorkSystemRowDTO>
     */

    @GetMapping("/deptworksystem")
    public PageResponse<DeptWorkSystemDTO> getDeptWorkSystemList(
            HttpServletRequest request,
            @RequestParam Integer departmentId,
            @RequestParam
            @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
            LocalDate workDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Integer employeeId = getEmployeeIdFromToken(request);

        return attendanceService.getDeptWorkSystemList(
                employeeId, departmentId, workDate, page, size
        );
    }

    @GetMapping("/dashboard")
    public PageResponse<AttendanceDashboardDTO> getAttendanceDashboardList(
            @RequestParam(name = "departmentId", required = false) Integer departmentId,
            @RequestParam(name = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return attendanceService.getAttendanceDashboardList(
                departmentId,
                startDate,
                endDate,
                page,
                size
        );
    }
}