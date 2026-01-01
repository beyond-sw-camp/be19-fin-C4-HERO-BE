package com.c4.hero.domain.vacation.controller;

import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.auth.security.JwtUtil;
import com.c4.hero.domain.vacation.dto.DepartmentVacationDTO;
import com.c4.hero.domain.vacation.dto.VacationHistoryDTO;
import com.c4.hero.domain.vacation.dto.VacationSummaryDTO;
import com.c4.hero.domain.vacation.service.VacationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.LocalDate;
import java.util.List;

/**
 * <pre>
 * Class Name: VacationController
 * Description: 휴가 이력 조회 등 휴가 관련 REST API 요청을 처리하는 컨트롤러
 *
 * History
 * 2025/12/16 (이지윤) 최초 작성 및 백엔드 코딩 컨벤션 적용
 * 2025/12/30 (리팩토링) Google Calendar 연동 제거
 * </pre>
 *
 * @author 이지윤
 * @version 1.2
 */
@Tag(
        name = "휴가 API",
        description = "개인 휴가 이력/요약, 부서 휴가 캘린더 API"
)
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
    @Operation(
            summary = "개인 휴가 이력 조회",
            description = "개인 휴가 이력을 페이지 단위로 조회"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "개인 휴가 이력 조회 성공",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "요청 파라미터 오류(startDate/endDate/page/size 등)"),
            @ApiResponse(responseCode = "401", description = "인증 실패(JWT 누락/만료/위조)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
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
    @Operation(
            summary = "부서 휴가 캘린더 조회",
            description = "부서 휴가 캘린더(월 단위)를 조회"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "부서 휴가 캘린더 조회 성공",
                    content = @Content(schema = @Schema(implementation = DepartmentVacationDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "요청 파라미터 오류(year/month 범위 등)"),
            @ApiResponse(responseCode = "401", description = "인증 실패(JWT 누락/만료/위조)"),
            @ApiResponse(responseCode = "403", description = "권한 없음(부서 캘린더 조회 권한 부족)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
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
    @Operation(
            summary = "개인 휴가 요약 조회",
            description = "로그인한 사용자의 휴가 요약 정보를 조회"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "개인 휴가 요약 조회 성공",
                    content = @Content(schema = @Schema(implementation = VacationSummaryDTO.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패(JWT 누락/만료/위조)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/summary")
    public VacationSummaryDTO getVacationSummary(HttpServletRequest request) {
        Integer employeeId = getEmployeeIdFromToken(request);
        return vacationService.findVacationLeaves(employeeId);
    }
}