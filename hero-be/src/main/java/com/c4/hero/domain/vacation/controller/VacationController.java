package com.c4.hero.domain.vacation.controller;

import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.vacation.dto.VacationHistoryDTO;
import com.c4.hero.domain.vacation.service.VacationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

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

    /** 휴가 이력 조회 비즈니스 로직을 처리하는 서비스 */
    private final VacationService vacationService;

    /**
     * 휴가 이력(페이지)을 조회합니다.
     *
     * <p>특징</p>
     * <ul>
     *     <li>사번({@code employeeId})로 특정 직원의 휴가 이력 조회 (로그인 도입 후 생략 예정)</li>
     *     <li>시작일/종료일({@code startDate}, {@code endDate})로 기간 필터링 가능</li>
     *     <li>페이지/사이즈 기반 페이지네이션 지원</li>
     * </ul>
     *
     * @param employeeId 조회할 직원 ID (null인 경우 전체 또는 추후 로그인 기반으로 대체 예정)
     * @param startDate  조회 시작일 (yyyy-MM-dd), null이면 기간 시작 제한 없음
     * @param endDate    조회 종료일 (yyyy-MM-dd), null이면 기간 종료 제한 없음
     * @param page       요청 페이지 번호 (1부터 시작)
     * @param size       페이지당 데이터 개수
     * @return 휴가 이력 페이지 응답
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
        return vacationService.findVacationHistory(
                employeeId,
                startDate,
                endDate,
                page,
                size
        );
    }
}
