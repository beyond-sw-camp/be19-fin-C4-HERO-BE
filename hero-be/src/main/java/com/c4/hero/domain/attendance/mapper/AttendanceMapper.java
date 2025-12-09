package com.c4.hero.domain.attendance.mapper;

import com.c4.hero.domain.attendance.dto.PersonalDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AttendanceMapper {
    List<PersonalDTO> selectPersonal();
}
