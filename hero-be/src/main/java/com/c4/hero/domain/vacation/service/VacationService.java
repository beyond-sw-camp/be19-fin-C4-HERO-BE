package com.c4.hero.domain.vacation.service;

import com.c4.hero.common.pagination.PageCalculator;
import com.c4.hero.common.pagination.PageInfo;
import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.vacation.dto.DepartmentVacationDTO;
import com.c4.hero.domain.vacation.dto.VacationHistoryDTO;
import com.c4.hero.domain.vacation.repository.DepartmentVacationRepository;
import com.c4.hero.domain.vacation.repository.VacationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * <pre>
 * Class Name: VacationService
 * Description: 휴가 이력 조회 등 휴가 도메인 비즈니스 로직을 처리하는 서비스 클래스
 *
 * History
 * 2025/12/16 (이지윤) 최초 작성 및 코딩 컨벤션 적용
 * </pre>
 *
 * 휴가 이력 페이지 조회를 담당하며,
 * JPA Page와 공통 페이지네이션 유틸(PageCalculator, PageResponse)을 연결하는 역할을 수행합니다.
 * 추후 로그인 연동 시 employeeId는 인증 정보에서 가져오는 방식으로 변경 가능합니다.
 *
 * @author 이지윤
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class VacationService {

    /** 휴가 이력 조회를 위한 레포지토리 */
    private final VacationRepository vacationRepository;
    private final DepartmentVacationRepository departmentVacationRepository;


    /**
     * 직원 휴가 이력을 페이지 단위로 조회합니다.
     *
     * @param employeeId 조회할 직원 ID
     * @param startDate  조회 시작일 (null인 경우 시작일 제한 없음)
     * @param endDate    조회 종료일 (null인 경우 종료일 제한 없음)
     * @param page       요청 페이지 번호 (1부터 시작)
     * @param size       페이지당 데이터 개수
     * @return 휴가 이력 DTO 리스트 및 페이지 정보가 포함된 응답
     */
    public PageResponse<VacationHistoryDTO> findVacationHistory(
            Integer employeeId,
            LocalDateTime startDate,
            LocalDateTime endDate,

            int page,
            int size
    ) {
        // 공통 페이지 계산 유틸을 통해 페이지/사이즈를 보정
        // (실제 총 건수는 JPA Page에서 계산되므로 maxTotalCount는 의미상 상한값으로 사용)
        PageInfo pageInfo = PageCalculator.calculate(
                page,
                size,
                Integer.MAX_VALUE
        );

        PageRequest pageable = PageRequest.of(
                pageInfo.getPage() - 1,
                pageInfo.getSize()
        );

        Page<VacationHistoryDTO> pageResult = vacationRepository.findVacationHistory(
                employeeId,
                startDate,
                endDate,
                pageable
        );

        return PageResponse.of(
                pageResult.getContent(),
                pageResult.getNumber() + 1,      // 0-based → 1-based로 변환
                pageResult.getSize(),
                pageResult.getTotalElements()
        );
    }

    /**
     * 부서 휴가 캘린더(월 단위) 조회
     *
     * - year/month가 null이면 서버 기준 현재 월로 조회합니다.
     * - departmentId가 null이면 전체 부서 휴가(필터 미적용)로 조회합니다.
     *
     * @param departmentId 부서 ID (null 가능)
     * @param year         연도 (예: 2025, null 가능)
     * @param month        월 (1~12, null 가능)
     * @return 해당 월 범위에 "겹치는" 휴가 이벤트 목록
     */

    public List<DepartmentVacationDTO> findDepartmentVacationCalendar(
            Integer departmentId,
            Integer year,
            Integer month
    ){
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Shanghai"));


        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month != null) ? month : now.getMonthValue();

        if (targetMonth < 1 || targetMonth > 12) {
            throw new IllegalArgumentException("month는 1~12 범위여야 합니다. month=" + targetMonth);
        }

        LocalDate firstDay = LocalDate.of(targetYear, targetMonth, 1);
        LocalDateTime monthStart = firstDay.atStartOfDay();
        LocalDateTime monthEnd = firstDay.plusMonths(1).atStartOfDay().minusNanos(1);

        return departmentVacationRepository.findApprovedDepartmentVacationByMonth(
                departmentId,
                monthStart,
                monthEnd
        );
    }

}
