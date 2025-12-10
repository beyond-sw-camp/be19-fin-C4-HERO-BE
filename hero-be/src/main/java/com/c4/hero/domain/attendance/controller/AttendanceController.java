package com.c4.hero.domain.attendance.controller;

import com.c4.hero.domain.attendance.dto.PersonalPageResponseDTO;
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
     * 개인 근태 기록 목록을 조회합니다.
     *
     * @return 개인 근태 기록 리스트(List<PersonalDTO>)
     */
    @GetMapping("/personal")
    public PersonalPageResponseDTO setPersonalList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return attendanceService.getPersonalList(page, size);
    }
}
