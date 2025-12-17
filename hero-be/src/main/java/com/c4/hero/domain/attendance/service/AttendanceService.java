package com.c4.hero.domain.attendance.service;

import com.c4.hero.common.pagination.PageCalculator;
import com.c4.hero.common.pagination.PageInfo;
import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.attendance.dto.*;
import com.c4.hero.domain.attendance.mapper.AttendanceMapper;
import com.c4.hero.domain.attendance.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * <pre>
 * Class Name: AttendanceService
 * Description: 근태 기록(개인 근태, 초과 근무, 근태 기록 수정 이력, 근무제 정정 이력 등)
   조회 관련 비즈니스 로직을 처리하는 서비스 클래스
 *
 * History
 * 2025/12/09 (이지윤) 최초 작성
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
    private final AttendanceRepository attendanceRepository;

    /**
     * 개인 근태 기록 페이지를 조회합니다.
     *
     * @param page      요청 페이지 번호 (1부터 시작)
     * @param size      페이지당 데이터 개수
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 개인 근태 기록 페이지 응답 DTO
     */
    public PageResponse<PersonalDTO> getPersonalList(
            int page,
            int size,
            String startDate,
            String endDate
    ) {
        //1. 전체 개수 조회 (날짜 필터 반영)
        int totalCount = attendanceMapper.selectPersonalCount(startDate, endDate);

        // 2. 페이지네이션 계산
        PageInfo pageInfo = PageCalculator.calculate(page, size, totalCount);

        // 3. 현재 페이지 데이터 조회
        List<PersonalDTO> items = attendanceMapper.selectPersonalPage(
                pageInfo.getOffset(),
                pageInfo.getSize(),
                startDate,
                endDate
        );

        // 4. 공통 PageResponse으로 응답
        return PageResponse.of(
                items,
                pageInfo.getPage() - 1,
                pageInfo.getSize(),
                totalCount
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
    public PageResponse<OvertimeDTO> getOvertimeList(
            int page,
            int size,
            String startDate,
            String endDate
    ) {
        //1. 전체 개수 조회 (날짜 필터 반영)
        int totalCount = attendanceMapper.selectOvertimeCount(startDate, endDate);

        // 2. 페이지네이션 계산
        PageInfo pageInfo = PageCalculator.calculate(page, size, totalCount);

        // 3. 현재 페이지 데이터 조회
        List<OvertimeDTO> items = attendanceMapper.selectOvertimePage(
                pageInfo.getOffset(),
                pageInfo.getSize(),
                startDate,
                endDate
        );

        // 4. 공통 PageResponse으로 응답
        return PageResponse.of(
                items,
                pageInfo.getPage() - 1,
                pageInfo.getSize(),
                totalCount
        );
    }

    /**
     * 근태 기록 수정 페이지를 조회합니다.
     *
     * @param page      요청 페이지 번호 (1부터 시작)
     * @param size      페이지당 데이터 개수
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 근태 기록 수정 페이지 응답 DTO
     */
    public PageResponse<CorrectionDTO> getCorrectionList(
            int page,
            int size,
            String startDate,
            String endDate
    ){
        //1. 전체 개수 조회 (날짜 필터 반영)
        int totalCount = attendanceMapper.selectCorrectionCount(startDate, endDate);

        // 2. 페이지네이션 계산
        PageInfo pageInfo = PageCalculator.calculate(page, size, totalCount);

        // 3. 현재 페이지 데이터 조회
        List<CorrectionDTO> items = attendanceMapper.selectCorrectionPage(
                pageInfo.getOffset(),
                pageInfo.getSize(),
                startDate,
                endDate
        );

        // 4. 공통 PageResponse으로 응답
        return PageResponse.of(
                items,
                pageInfo.getPage() - 1,
                pageInfo.getSize(),
                totalCount
        );
    }

    /**
     * 근무제 정정 페이지를 조회합니다.
     *
     * @param page      요청 페이지 번호 (1부터 시작)
     * @param size      페이지당 데이터 개수
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 근무제 정정 기록 페이지 응답 DTO
     */
    public PageResponse<ChangeLogDTO> getChangeLogList(
            int page,
            int size,
            String startDate,
            String endDate
    ) {
        //1. 전체 개수 조회 (날짜 필터 반영)
        int totalCount = attendanceMapper.selectChangeLogCount(startDate, endDate);

        // 2. 페이지네이션 계산
        PageInfo pageInfo = PageCalculator.calculate(page, size, totalCount);

        // 3. 현재 페이지 데이터 조회
        List<ChangeLogDTO> items = attendanceMapper.selectChangeLogPage(
                pageInfo.getOffset(),
                pageInfo.getSize(),
                startDate,
                endDate
        );

        // 4. 공통 PageResponse으로 응답
        return PageResponse.of(
                items,
                pageInfo.getPage() - 1,
                pageInfo.getSize(),
                totalCount
        );
    }

    /**
     * 부서 근태 현황 페이지 조회
     *
     * @param departmentId 부서 ID
     * @param workDate     조회 날짜
     * @param page         요청 페이지 번호 (1부터 시작)
     * @param size         페이지 크기
     * @return PageResponse<DeptWorkSystemRowDTO>
     */
    public PageResponse<DeptWorkSystemRowDTO> getDeptWorkSystemList(
        Integer departmentId,
        LocalDate workDate,
        int page,
        int size
    ){
        // 1. 전체 개수 조회는 Repository의 countQuery가 담당
        //    → Page<T>를 통해 totalElements 확보 가능

        // 2. 페이지 계산 (우리 프로젝트 기준: 1-based)
        PageInfo pageInfo = PageCalculator.calculate(
                page,
                size,
                Integer.MAX_VALUE // JPA Page에서는 실제 count는 repository가 처리
        );

        // 3. JPA Pageable (0-based 변환은 여기서만)
        PageRequest pageable = PageRequest.of(pageInfo.getPage() - 1, pageInfo.getSize());

        // 4. Repository 조회
        Page<DeptWorkSystemRowDTO> pageResult = attendanceRepository.findDeptWorkSystemRows(
                departmentId,
                workDate,
                pageable
        );

        //5. PageResponse로 변환(응답 전담)
        return PageResponse.of(
                pageResult.getContent(),
                pageResult.getNumber() + 1,
                pageResult.getSize(),
                pageResult.getTotalElements()
        );
    }
}
