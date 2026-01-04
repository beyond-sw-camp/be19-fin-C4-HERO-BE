package com.c4.hero.domain.attendance.controller;

import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.attendance.dto.AttSummaryDTO;
import com.c4.hero.domain.attendance.dto.AttendanceDashboardDTO;
import com.c4.hero.domain.attendance.dto.AttendanceDashboardSummaryDTO;
import com.c4.hero.domain.attendance.dto.AttendanceEmployeeHalfDashboardDTO;
import com.c4.hero.domain.attendance.dto.ChangeLogDTO;
import com.c4.hero.domain.attendance.dto.CorrectionDTO;
import com.c4.hero.domain.attendance.dto.DeptWorkSystemDTO;
import com.c4.hero.domain.attendance.dto.OvertimeDTO;
import com.c4.hero.domain.attendance.dto.PersonalDTO;
import com.c4.hero.domain.attendance.service.AttendanceEventService;
import com.c4.hero.domain.attendance.service.AttendanceService;
import com.c4.hero.domain.attendance.type.AttendanceHalfType;
import com.c4.hero.domain.auth.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


/**
 * <pre>
 * Class Name: AttendanceController
 * Description: 근태(개인 근태, 초과 근무, 정정 이력, 부서 현황, 대시보드 등) 관련 REST API 컨트롤러
 *
 * History
 * 2025/12/09 (이지윤) 최초 작성
 * 2025/12/18 (이지윤) 개인 근태 요약, JWT 기반 조회 적용
 * 2025/12/24 (이지윤) 부서 근태 현황/대시보드/반기 대시보드 API 추가 및 코딩 컨벤션 정리
 * 2025/12/30 (이지윤) 개인 근태 기록 단건 조회 기능 개발
 * </pre>
 *
 * 개인별/부서별 근태 및 연관된 각종 현황을 조회하는 엔드포인트를 제공합니다.
 * - 개인 탭: 근태 요약, 근태 이력, 초과 근무, 근태 정정, 근무제 변경 이력
 * - 부서 탭: 부서 근태 현황(당일), 근태 점수 대시보드(월별)
 * - 직원 반기 대시보드: 특정 직원의 반기 근태 차트용 데이터
 *
 * JWT 토큰에서 employeeId를 파싱하여, 로그인한 사용자의 데이터를 조회하는 패턴을 기본으로 합니다.
 *
 * @author 이지윤
 * @version 1.2
 */
@Tag( name = "근태 API", description = "개인/부서 근태 조회, 근태 점수 대시보드, 직원 반기 대시보드 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendance")
public class AttendanceController {

    /** 근태 관련 비즈니스 로직 처리 서비스 */
    private final AttendanceService attendanceService;
    private final AttendanceEventService attendanceEventService;

    /** JWT 토큰 파싱 및 인증 정보를 처리하는 유틸리티 */
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
     *     <li>{@code startDate}, {@code endDate}는 yyyy-MM-dd 형식의 LocalDate로 전달</li>
     *     <li>날짜가 전달되지 않으면 서비스 계층에서 기본 기간(예: 이번 달 기준)을 적용</li>
     * </ul>
     *
     * @param request   HTTP 요청 (JWT 토큰 추출용)
     * @param startDate 조회 시작일(yyyy-MM-dd), null/미전달 시 기본값 사용
     * @param endDate   조회 종료일(yyyy-MM-dd), null/미전달 시 기본값 사용
     * @return 개인 근태 요약 DTO
     */
    @Operation(summary = "개인 근태 요약 조회",
               description = "근태 관리 상단 탭"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "개인 근태 기록 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "요청 파라미터 오류(page/size/date 범위 등)"),
            @ApiResponse(responseCode = "401", description = "인증 실패(JWT 누락/만료/위조)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
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
     * <p>JWT 기반으로 로그인한 사용자의 근태 이력을 조회하며,</p>
     * <p>기간 필터(startDate ~ endDate)와 페이지네이션을 지원합니다.</p>
     *
     * @param request   로그인 정보(JWT)를 포함한 HTTP 요청
     * @param page      조회할 페이지 번호 (1부터 시작, 기본값 1)
     * @param size      한 페이지당 조회할 데이터 개수 (기본값 10)
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 개인 근태 기록 페이지 응답 DTO
     */
    @Operation( summary = "개인 근태 기록 목록 조회",
            description = "로그인한 사용자의 근태 이력을 조회"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "개인 근태 기록 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 파라미터 오류(page/size/date 범위 등)"),
            @ApiResponse(responseCode = "401", description = "인증 실패(JWT 누락/만료/위조)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })

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

    @Operation(
            summary = "개인 근태 기록 단건 조회",
            description = "attendanceId로 특정 근태 기록 1건을 조회합니다. (JWT 기반 본인 데이터만 조회)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "근태 기록 단건 조회 성공",
                    content = @Content(schema = @Schema(implementation = PersonalDTO.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패(JWT 누락/만료/위조)"),
            @ApiResponse(responseCode = "404", description = "근태 기록 없음(본인 데이터가 아니거나 존재하지 않음)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/{attendanceId}")
    public PersonalDTO getAttendanceDetail(
            HttpServletRequest request,
            @PathVariable Integer attendanceId
    ) {
        Integer employeeId = getEmployeeIdFromToken(request);
        return attendanceEventService.getPersonalDetail(employeeId, attendanceId);
    }


    /**
     * 개인 초과 근무(연장 근무) 이력(페이지)을 조회합니다.
     *
     * @param request   로그인 정보(JWT)를 포함한 HTTP 요청
     * @param page      조회할 페이지 번호 (1부터 시작, 기본값 1)
     * @param size      한 페이지당 조회할 데이터 개수 (기본값 10)
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 초과 근무 이력 페이지 응답 DTO
     */
    @Operation( summary = "개인 초과 근무 이력 조회",
            description = "개인 초과 근무(연장 근무) 이력(페이지)을 조회"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "초과 근무 이력 조회 성공",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 파라미터 오류(page/size/date 범위 등)"),
            @ApiResponse(responseCode = "401", description = "인증 실패(JWT 누락/만료/위조)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })

    @GetMapping("/overtime")
    public PageResponse<OvertimeDTO> getOvertimeList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        Integer employeeId = getEmployeeIdFromToken(request);

        return attendanceService.getOvertimeList(employeeId, page, size, startDate, endDate);
    }

    /**
     * 개인 근태 정정 요청 이력(페이지)을 조회합니다.
     *
     * @param request   로그인 정보(JWT)를 포함한 HTTP 요청
     * @param page      조회할 페이지 번호 (1부터 시작, 기본값 1)
     * @param size      한 페이지당 조회할 데이터 개수 (기본값 10)
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 근태 정정 이력 페이지 응답 DTO
     */
    @Operation(
            summary = "개인 근태 정정 요청 이력 조회",
            description = "개인 근태 정정 요청 이력(페이지)을 조회"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "근태 정정 요청 이력 조회 성공",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 파라미터 오류(page/size/date 범위 등)"),
            @ApiResponse(responseCode = "401", description = "인증 실패(JWT 누락/만료/위조)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })

    @GetMapping("/correction")
    public PageResponse<CorrectionDTO> getCorrectionList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        Integer employeeId = getEmployeeIdFromToken(request);

        return attendanceService.getCorrectionList(employeeId, page, size, startDate, endDate);
    }

    /**
     * 개인 근무제 변경(Work System Change) 이력(페이지)을 조회합니다.
     *
     * @param request   로그인 정보(JWT)를 포함한 HTTP 요청
     * @param page      조회할 페이지 번호 (1부터 시작, 기본값 1)
     * @param size      한 페이지당 조회할 데이터 개수 (기본값 10)
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 근무제 변경 이력 페이지 응답 DTO
     */
    @Operation(
            summary = "개인 근무제 변경 이력 조회",
            description = "개인 근무제 변경이력(페이지)을 조회"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "근무제 변경 이력 조회 성공",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 파라미터 오류(page/size/date 범위 등)"),
            @ApiResponse(responseCode = "401", description = "인증 실패(JWT 누락/만료/위조)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })

    @GetMapping("/changelog")
    public PageResponse<ChangeLogDTO> getChangeLogList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        Integer employeeId = getEmployeeIdFromToken(request);

        return attendanceService.getChangeLogList(employeeId, page, size, startDate, endDate);
    }

    /**
     * 부서 근태 현황(당일 기준)을 조회합니다.
     *
     * <p>특징</p>
     * <ul>
     *     <li>지정한 날짜({@code workDate})와 부서({@code departmentId}) 기준</li>
     *     <li>부서 소속 직원별 근무제, 근무시간, 상태 등을 조회</li>
     *     <li>페이지네이션을 지원</li>
     * </ul>
     *
     * @param request      로그인 정보(JWT)를 포함한 HTTP 요청 (필요 시 부서 권한 체크 등 확장 가능)
     * @param departmentId 조회 대상 부서 ID
     * @param workDate     조회 기준일(yyyy-MM-dd)
     * @param page         페이지 번호 (1부터 시작, 기본값 1)
     * @param size         페이지 크기 (기본값 10)
     * @return 부서 근태 현황 페이지 응답 DTO
     */
    @Operation(
            summary = "부서 근태 현황 조회",
            description = "부서 근태 현황(당일 기준)을 조회"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "부서 근태 현황 조회 성공",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 파라미터 오류(departmentId/workDate/page/size 등)"),
            @ApiResponse(responseCode = "401", description = "인증 실패(JWT 누락/만료/위조)"),
            @ApiResponse(responseCode = "403", description = "권한 없음(부서 조회 권한 부족)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })

    @GetMapping("/deptworksystem")
    public PageResponse<DeptWorkSystemDTO> getDeptWorkSystemList(
            HttpServletRequest request,
            @RequestParam Integer departmentId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate workDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Integer employeeId = getEmployeeIdFromToken(request);

        return attendanceService.getDeptWorkSystemList(
                employeeId,
                departmentId,
                workDate,
                page,
                size
        );
    }

    /**
     * 근태 점수 대시보드(직원별 점수 리스트)를 조회합니다.
     *
     * <p>특징</p>
     * <ul>
     *     <li>{@code month}는 "YYYY-MM" 형식 문자열 (예: "2025-12")</li>
     *     <li>departmentId가 null이면 전체 부서, 값이 있으면 해당 부서만 대상</li>
     *     <li>scoreSort는 "ASC" / "DESC" (기본값 DESC)</li>
     * </ul>
     *
     * @param departmentId 부서 ID (null이면 전체 부서)
     * @param month        조회 월("YYYY-MM"), null인 경우 서비스에서 기본값 처리
     * @param scoreSort    점수 정렬 방향("ASC"/"DESC"), 기본값 "DESC"
     * @param page         페이지 번호 (1부터 시작, 기본값 1)
     * @param size         페이지 크기 (기본값 10)
     * @return 근태 점수 대시보드 페이지 응답 DTO
     */
    @Operation(
            summary = "근태 점수 대시보드 조회",
            description = "근태 점수 대시보드(직원별 점수 리스트)를 조회"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "근태 점수 대시보드 조회 성공",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 파라미터 오류(month 형식/scoreSort/page/size 등)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })

    @GetMapping("/dashboard")
    public PageResponse<AttendanceDashboardDTO> getAttendanceDashboardList(
            @RequestParam(name = "departmentId", required = false) Integer departmentId,
            @RequestParam(name = "month", required = false) String month,
            @RequestParam(name = "scoreSort", required = false, defaultValue = "DESC") String scoreSort,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return attendanceService.getAttendanceDashboardList(
                departmentId,
                month,
                scoreSort,
                page,
                size
        );
    }

    /**
     * 근태 점수 대시보드 상단 요약(전체/우수/위험 직원 수)을 조회합니다.
     *
     * <p>
     * {@code month}는 "YYYY-MM" 형식 문자열이며,
     * {@code departmentId}가 null이면 전체 부서 기준으로 집계합니다.
     * </p>
     *
     * @param departmentId 부서 ID (null이면 전체 부서)
     * @param month        조회 월("YYYY-MM"), null인 경우 서비스에서 기본값 처리
     * @return 근태 대시보드 요약 DTO
     */
    @Operation(
            summary = "근태 점수 대시보드 요약 조회",
            description = "근태 점수 대시보드 상단 요약(전체/우수/위험 직원 수)을 조회"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "근태 점수 대시보드 요약 조회 성공",
                    content = @Content(schema = @Schema(implementation = AttendanceDashboardSummaryDTO.class))),
            @ApiResponse(responseCode = "400", description = "요청 파라미터 오류(month 형식 등)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })

    @GetMapping("/dashboard/summary")
    public AttendanceDashboardSummaryDTO getAttendanceDashboardSummary(
            @RequestParam(name = "departmentId", required = false) Integer departmentId,
            @RequestParam(name = "month", required = false) String month
    ) {
        return attendanceService.getAttendanceDashboardSummary(departmentId, month);
    }

    /**
     * 직원 1명의 반기(상/하반기) 근태 대시보드(차트 Drawer)를 조회합니다.
     *
     * <p>요청 예</p>
     * <pre>
     * GET /api/attendance/dashboard/employee?employeeId=1&year=2025&half=H1
     * </pre>
     *
     * <p>파라미터</p>
     * <ul>
     *     <li>employeeId: 직원 PK (필수)</li>
     *     <li>year: 조회 연도 (옵션, null이면 서비스에서 현재 연도 사용)</li>
     *     <li>half: 상/하반기 구분(H1/H2), null이면 기본 H1 또는 서비스 기본값</li>
     * </ul>
     *
     * @param employeeId 직원 ID (필수)
     * @param year       조회 연도 (옵션)
     * @param half       반기 타입(H1/H2, 옵션)
     * @return 직원 반기 근태 대시보드 DTO
     */
    @Operation(
            summary = "직원 반기 근태 대시보드 조회",
            description = "직원 1명의 반기(상/하반기) 근태 대시보드(차트 Drawer)를 조회"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "직원 반기 근태 대시보드 조회 성공",
                    content = @Content(schema = @Schema(implementation = AttendanceEmployeeHalfDashboardDTO.class))),
            @ApiResponse(responseCode = "400", description = "요청 파라미터 오류(employeeId/year/half 등)"),
            @ApiResponse(responseCode = "404", description = "직원 정보 없음(employeeId에 해당하는 직원 없음)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })

    @GetMapping("/dashboard/employee")
    public AttendanceEmployeeHalfDashboardDTO getEmployeeHalfDashboard(
            @RequestParam Integer employeeId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) AttendanceHalfType half
    ) {
        return attendanceService.getEmployeeHalfDashboard(employeeId, year, half);
    }
}