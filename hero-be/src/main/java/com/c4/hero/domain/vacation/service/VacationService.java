package com.c4.hero.domain.vacation.service;

import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import com.c4.hero.domain.vacation.dto.DepartmentVacationDTO;
import com.c4.hero.domain.vacation.dto.VacationHistoryDTO;
import com.c4.hero.domain.vacation.dto.VacationSummaryDTO;
import com.c4.hero.domain.vacation.entity.VacationLog;
import com.c4.hero.domain.vacation.entity.VacationType;
import com.c4.hero.domain.vacation.repository.DepartmentVacationRepository;
import com.c4.hero.domain.vacation.repository.VacationRepository;
import com.c4.hero.domain.vacation.repository.VacationSummaryRepository;
import com.c4.hero.domain.vacation.repository.VacationTypeRepository;
import com.c4.hero.domain.vacation.type.VacationStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.List;

/**
 * <pre>
 * Class Name: VacationService
 * Description: 휴가 도메인 비즈니스 로직을 처리하는 서비스 클래스
 *
 * History
 * 2025/12/16 (이지윤) 최초 작성 및 코딩 컨벤션 적용
 * 2025/12/30 (리팩토링) Google Calendar 연동 제거
 * </pre>
 *
 * @author 이지윤
 * @version 1.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VacationService {

    /** 휴가 이력 조회를 위한 레포지토리 */
    private final VacationRepository vacationRepository;
    private final DepartmentVacationRepository departmentVacationRepository;
    private final VacationSummaryRepository vacationSummaryRepository;
    private final EmployeeRepository employeeRepository;
    private final VacationTypeRepository vacationTypeRepository;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    /**
     * 직원 휴가 이력을 페이지 단위로 조회합니다.
     */
    public PageResponse<VacationHistoryDTO> findVacationHistory(
            Integer employeeId,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size
    ) {
        int safePage = (page <= 0) ? 1 : page;
        int safeSize = (size <= 0) ? 10 : size;

        PageRequest pageable = PageRequest.of(safePage - 1, safeSize);

        Page<VacationHistoryDTO> pageResult = vacationRepository.findVacationHistory(
                employeeId,
                startDate,
                endDate,
                pageable
        );

        return PageResponse.of(
                pageResult.getContent(),
                pageResult.getNumber() + 1,
                pageResult.getSize(),
                (int) pageResult.getTotalElements()
        );
    }

    /**
     * 부서 휴가 캘린더(월 단위) 조회
     */
    public List<DepartmentVacationDTO> findDepartmentVacationCalendar(
            Integer employeeId,
            Integer year,
            Integer month
    ) {
        LocalDate now = LocalDate.now();

        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month != null) ? month : now.getMonthValue();

        if (targetMonth < 1 || targetMonth > 12) {
            throw new IllegalArgumentException("month는 1~12 범위여야 합니다. month=" + targetMonth);
        }

        Integer departmentId = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 employeeId=" + employeeId))
                .getEmployeeDepartment()
                .getDepartmentId();

        LocalDate firstDay = LocalDate.of(targetYear, targetMonth, 1);
        LocalDate monthStart = firstDay;
        LocalDate monthEnd = firstDay.plusMonths(1).minusDays(1);

        return departmentVacationRepository.findApprovedDepartmentVacationByMonth(
                departmentId,
                monthStart,
                monthEnd
        );
    }

    /**
     * 직원의 휴가 요약 정보를 조회합니다.
     */
    public VacationSummaryDTO findVacationLeaves(Integer employeeId) {
        List<VacationSummaryDTO> results =
                vacationSummaryRepository.findSummaryByEmployeeId(employeeId);

        if (results == null || results.isEmpty()) {
            // 요약 데이터가 아예 없으면 null 리턴 (프론트에서 null 처리)
            return null;
        }

        if (results.size() > 1) {
            log.warn("휴가 요약이 여러 건 조회되었습니다. employeeId={}, count={}",
                    employeeId, results.size());
        }

        // grant_date desc 로 정렬되어 있으니까 0번이 가장 최신
        return results.get(0);
    }

    /**
     * 결재 완료된 휴가 신청서(details JSON)를 기반으로 VacationLog를 생성합니다.
     *
     * @param employeeId  휴가 신청자(기안자) employeeId
     * @param detailsJson ApprovalDocument.details에 저장된 JSON 문자열
     */
    @Transactional
    public void createVacationLogFromApproval(Integer employeeId, String detailsJson) {
        try {
            JsonNode root = objectMapper.readTree(detailsJson);
            int vacationTypeId = root.path("vacationType").asInt(0);
            String startDateStr = root.path("startDate").asText(null);
            String endDateStr = root.path("endDate").asText(null);
            String reason = root.path("reason").asText("");

            if (vacationTypeId <= 0 || startDateStr == null || endDateStr == null) {
                throw new IllegalArgumentException(
                        "휴가 신청 details에 필수 값(vacationType/startDate/endDate)이 누락되었습니다. details=" + detailsJson
                );
            }

            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);

            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 직원입니다. employeeId=" + employeeId));

            VacationType vacationType = vacationTypeRepository.findById(vacationTypeId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 휴가 유형입니다. vacationTypeId=" + vacationTypeId));

            VacationLog log = VacationLog.create(
                    employee,
                    vacationType,
                    startDate,
                    endDate,
                    reason,
                    VacationStatus.APPROVED
            );

            vacationRepository.save(log);

            log.info("[VacationService] VacationLog saved. employeeId={}, typeId={}, start={}, end={}",
                    employeeId, vacationTypeId, startDate, endDate);

        } catch (Exception e) {
            // 상위 리스너에서 잡아서 로그 찍도록 예외 그대로 던지기
            throw new IllegalStateException("휴가 신청 details 처리 중 오류 발생. details=" + detailsJson, e);
        }
    }
}