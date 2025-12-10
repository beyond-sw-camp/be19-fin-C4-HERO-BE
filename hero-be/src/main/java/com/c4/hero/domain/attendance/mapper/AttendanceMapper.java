package com.c4.hero.domain.attendance.mapper;

import com.c4.hero.domain.attendance.dto.PersonalDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * <pre>
 * Interface Name: AttendanceMapper
 * Description: 근태 관련 데이터 조회를 위한 MyBatis Mapper 인터페이스
 *
 * History
 * 2025/12/09 (이지윤) 최초 작성 및 컨벤션 적용
 * </pre>
 *
 * @author 이지윤
 * @version 1.0
 */

@Mapper
public interface AttendanceMapper {

    /**
     * 개인 근태 기록 목록을 조회합니다.
     *
     * @return 개인 근태 기록 리스트(List<PersonalDTO>)
     */
    List<PersonalDTO> selectPersonalPage(@Param("offset") int offset,
                                         @Param("size") int size);

    int selectPersonalCount();
}