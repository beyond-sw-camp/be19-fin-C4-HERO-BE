package com.c4.hero.domain.attendance.service;

import com.c4.hero.domain.attendance.dto.PersonalDTO;
import com.c4.hero.domain.attendance.mapper.AttendanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceMapper attendanceMapper;

    public List<PersonalDTO> getPersonalList(){
        return attendanceMapper.selectPersonal();
    }
}
