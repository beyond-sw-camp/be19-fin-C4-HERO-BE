package com.c4.hero.domain.attendance.service;

import com.c4.hero.domain.attendance.dto.CorrectionDTO;
import com.c4.hero.domain.attendance.dto.OvertimeDTO;
import com.c4.hero.domain.attendance.dto.PageResponseDTO;
import com.c4.hero.domain.attendance.dto.PersonalDTO;
import com.c4.hero.domain.attendance.mapper.AttendanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <pre>
 * Class Name: AttendanceService
 * Description: 근태 기록(개인 근태, 초과 근무 등) 조회 관련 비즈니스 로직을 처리하는 서비스 클래스
 *
 * History
 * 2025/12/09 (이지윤) 최초 작성 및 컨벤션 적용
 * 2025/12/10 (이지윤) 초과 근무 조회 로직 및 공통 페이징 응답 DTO 적용
 * </pre>
 *
 * @author 이지윤
 * @version 2.0
 */
@Service
@RequiredArgsConstructor
public class AttendanceService {

    /** 근태 정보 조회를 위한 Mapper */
    private final AttendanceMapper attendanceMapper;

    /**
     * 개인 근태 기록 페이지를 조회합니다.
     *
     * @param page      요청 페이지 번호 (1부터 시작)
     * @param size      페이지당 데이터 개수
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 개인 근태 기록 페이지 응답 DTO
     */
    public PageResponseDTO<PersonalDTO> getPersonalList(
            int page,
            int size,
            String startDate,
            String endDate
    ) {
        // 0. 페이지/사이즈 유효 범위 보정
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);

        // 1. 전체 개수 조회 (날짜 필터 반영)
        int totalCount = attendanceMapper.selectPersonalCount(startDate, endDate);
        int totalPages = (int) Math.ceil((double) totalCount / safeSize);

        // 2. 페이지 번호 보정
        if (totalPages == 0) {
            // 데이터가 하나도 없는 경우: 페이지를 1로 고정
            safePage = 1;
        } else if (safePage > totalPages) {
            // 요청한 페이지가 너무 클 경우: 마지막 페이지로 보정
            safePage = totalPages;
        }

        // 3. OFFSET 계산 (보정된 safePage 기준)
        int offset = (safePage - 1) * safeSize;

        // 4. 현재 페이지 데이터 조회
        List<PersonalDTO> items = attendanceMapper.selectPersonalPage(
                offset,
                safeSize,
                startDate,
                endDate
        );

        // 5. 페이지 응답 DTO 구성
        return new PageResponseDTO<>(
                items,
                safePage,
                safeSize,
                totalCount,
                totalPages
        );
    }

    /**
     * 초과 근무(연장 근무) 기록 페이지를 조회합니다.
     *
     * @param page      요청 페이지 번호 (1부터 시작)
     * @param size      페이지당 데이터 개수
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 초과 근무 기록 페이지 응답 DTO
     */
    public PageResponseDTO<OvertimeDTO> getOvertimeList(
            int page,
            int size,
            String startDate,
            String endDate
    ) {
        // 0. 페이지/사이즈 유효 범위 보정
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);

        // 1. 전체 개수 조회 (날짜 필터 반영)
        int totalCount = attendanceMapper.selectOvertimeCount(startDate, endDate);
        int totalPages = (int) Math.ceil((double) totalCount / safeSize);

        // 2. 페이지 번호 보정
        if (totalPages == 0) {
            safePage = 1;
        } else if (safePage > totalPages) {
            safePage = totalPages;
        }

        // 3. OFFSET 계산 (보정된 safePage 기준)
        int offset = (safePage - 1) * safeSize;

        // 4. 현재 페이지 데이터 조회
        List<OvertimeDTO> items = attendanceMapper.selectOvertimePage(
                offset,
                safeSize,
                startDate,
                endDate
        );

        // 5. 페이지 응답 DTO 구성
        return new PageResponseDTO<>(
                items,
                safePage,
                safeSize,
                totalCount,
                totalPages
        );
    }

    public PageResponseDTO<CorrectionDTO> getCorrectionList(
            int page,
            int size,
            String startDate,
            String endDate
    ){
        // 0. 페이지/사이즈 유효 범위 보정
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);

        // 1. 전체 개수 조회 (날짜 필터 반영)
        int totalCount = attendanceMapper.selectCorrectionCount(startDate, endDate);
        int totalPages = (int) Math.ceil((double) totalCount / safeSize);

        // 2. 페이지 번호 보정
        if (totalPages == 0) {
            safePage = 1;
        } else if (safePage > totalPages) {
            safePage = totalPages;
        }

        // 3. OFFSET 계산 (보정된 safePage 기준)
        int offset = (safePage - 1) * safeSize;

        // 4. 현재 페이지 데이터 조회
        List<CorrectionDTO> items = attendanceMapper.selectCorrectionPage(
                offset,
                safeSize,
                startDate,
                endDate
        );

        // 5. 페이지 응답 DTO 구성
        return new PageResponseDTO<>(
                items,
                safePage,
                safeSize,
                totalCount,
                totalPages
        );
    }
}
