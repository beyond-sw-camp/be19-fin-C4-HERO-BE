package com.c4.hero.domain.attendance.service;

import com.c4.hero.domain.attendance.dto.PersonalDTO;
import com.c4.hero.domain.attendance.dto.PersonalPageResponseDTO;
import com.c4.hero.domain.attendance.mapper.AttendanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <pre>
 * Class Name: AttendanceService
 * Description: 근태 기록 조회 등 근태 관련 비즈니스 로직을 처리하는 서비스 클래스
 *
 * History
 * 2025/12/09 (이지윤) 최초 작성 및 컨벤션 적용
 * </pre>
 *
 * @author 이지윤
 * @version 1.0
 */

@Service
@RequiredArgsConstructor
public class AttendanceService {

    /** 근태 정보 조회를 위한 Mapper */
    private final AttendanceMapper attendanceMapper;

    /**
     * 개인 근태 기록 목록을 조회합니다.
     *
     * @return 개인 근태 기록 리스트(List<PersonalDTO>)
     */
    public PersonalPageResponseDTO getPersonalList(int page, int size) {

        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);

        int offset = (safePage - 1) * safeSize;

        int totalCount = attendanceMapper.selectPersonalCount();
        List<PersonalDTO> items =
                attendanceMapper.selectPersonalPage(offset, safeSize);

        int totalPages = (int)Math.ceil((double)totalCount / safeSize);

        if (totalPages > 0 && safePage > totalPages) {
            safePage = totalPages;
        }

        return new PersonalPageResponseDTO(
                items,
                safePage,
                safeSize,
                totalCount,
                totalPages
        );
    }
}
