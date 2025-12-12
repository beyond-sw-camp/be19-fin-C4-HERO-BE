package com.c4.hero.domain.attendance.controller;

import com.c4.hero.domain.attendance.dto.*;
import com.c4.hero.domain.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendance")
public class AttendanceController {

    /** 근태 관련 비즈니스 로직 처리 서비스 */
    private final AttendanceService attendanceService;

    /**
     * 개인 근태 기록 목록(페이지)을 조회합니다.
     *
     * @param page      조회할 페이지 번호 (1부터 시작)
     * @param size      한 페이지당 조회할 데이터 개수
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 개인 근태 기록 페이지 응답 DTO
     */
    @GetMapping("/personal")
    public PageResponseDTO<PersonalDTO> getPersonalList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        return attendanceService.getPersonalList(page, size, startDate, endDate);
    }

    @GetMapping("/overtime")
    public PageResponseDTO<OvertimeDTO> getOvertimeList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ){
        return attendanceService.getOvertimeList(page, size, startDate, endDate);
    }

    @GetMapping("/correction")
    public PageResponseDTO<CorrectionDTO> getCorrectionList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ){
        return attendanceService.getCorrectionList(page, size, startDate, endDate);
    }

    @GetMapping("/changeLog")
    public PageResponseDTO<ChangeLogDTO> getChangeLogList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ){
        return attendanceService.getChangeLogList(page, size, startDate, endDate);
    }
}
