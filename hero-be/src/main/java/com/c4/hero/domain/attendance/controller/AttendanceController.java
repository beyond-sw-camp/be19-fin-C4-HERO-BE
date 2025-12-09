package com.c4.hero.domain.attendance.controller;

import com.c4.hero.domain.attendance.dto.PersonalDTO;
import com.c4.hero.domain.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/personal")
    public List<PersonalDTO> setPersonalList(){
        return attendanceService.getPersonalList();
    }
}
