package com.c4.hero.domain.retirement.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import com.c4.hero.domain.employee.type.EmployeeStatus;
import com.c4.hero.domain.retirement.dto.*;
import com.c4.hero.domain.retirement.entity.ExitReasonMaster;
import com.c4.hero.domain.retirement.entity.Retirement;
import com.c4.hero.domain.retirement.repository.ExitReasonMasterRepository;
import com.c4.hero.domain.retirement.repository.RetirementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <pre>
 * Class Name: RetirementService
 * Description: 퇴직 관리 관련 비즈니스 로직을 처리하는 서비스 클래스
 *
 * History
 * 2025/12/30 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RetirementService {

    private final ExitReasonMasterRepository exitReasonMasterRepository;
    private final EmployeeRepository employeeRepository;
    private final RetirementRepository retirementRepository;

    /**
     * 활성화된 퇴사 사유 목록을 조회합니다.
     *
     * @return 퇴사 사유 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<ExitReasonDTO> getExitReasons() {
        List<ExitReasonMaster> reasons = exitReasonMasterRepository.findAll();
        return reasons.stream()
                .filter(reason -> reason.getActiveYn() == 1)
                .map(reason -> ExitReasonDTO.builder()
                        .exitReasonId(reason.getExitReasonId())
                        .reasonName(reason.getReasonName())
                        .reasonType(reason.getReasonType())
                        .detailDescription(reason.getDetailDescription())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 퇴직 현황 요약 정보를 계산하여 반환합니다.
     * 잔존률, 정착률, 종합 이직률, 신입 이직률을 포함합니다.
     *
     * @return 퇴직 현황 요약 DTO
     */
    @Transactional(readOnly = true)
    public RetirementSummaryDTO getRetirementSummary() {
        // 관리자(부서 ID 0) 제외하고 조회
        List<Employee> allEmployees = employeeRepository.findAll().stream()
                .filter(e -> e.getEmployeeDepartment() != null && e.getEmployeeDepartment().getDepartmentId() != 0)
                .collect(Collectors.toList());
                
        LocalDate today = LocalDate.now();
        
        // 1. 잔존률 (Retention Rate): 최근 3년 (오늘로부터 정확히 3년 전 ~ 현재)
        LocalDate periodStart = today.minusYears(3);

        // 기간 초 직원 수: 3년 전 시점에 재직 중이었던 직원
        List<Employee> employeesAtStart = allEmployees.stream()
                .filter(e -> !e.getHireDate().isAfter(periodStart) &&
                        (e.getTerminationDate() == null || e.getTerminationDate().isAfter(periodStart)))
                .collect(Collectors.toList());
        long countAtStart = employeesAtStart.size();

        // 기간 말 잔존 직원 수: 기간 초 직원 중에서 현재도 퇴사하지 않은 직원
        long retainedAtEnd = employeesAtStart.stream()
                .filter(e -> e.getStatus() != EmployeeStatus.RETIRED)
                .count();

        double retentionRate = countAtStart > 0 ? (double) retainedAtEnd / countAtStart * 100 : 0;

        // 2. 정착률 (Settlement Rate): 최근 1년 입사자 중 3개월 이상 근무(또는 재직)한 비율
        LocalDate oneYearAgo = today.minusYears(1);
        List<Employee> newHiresInLastYear = allEmployees.stream()
                .filter(e -> !e.getHireDate().isBefore(oneYearAgo)) // 1년 전 날짜 포함 (>=)
                .collect(Collectors.toList());

        long newHiresCount = newHiresInLastYear.size();

        long settledNewHiresCount = newHiresInLastYear.stream()
                .filter(e -> {
                    if (e.getStatus() != EmployeeStatus.RETIRED) {
                        return true; // 현재 재직 중이면 정착으로 간주
                    }
                    // 퇴사한 경우, 근속 기간이 3개월 이상이면 정착으로 간주
                    LocalDate endDate = e.getTerminationDate() != null ? e.getTerminationDate() : today;
                    long daysWorked = ChronoUnit.DAYS.between(e.getHireDate(), endDate);
                    return daysWorked >= 90;
                })
                .count();

        double settlementRate = newHiresCount > 0 ? (double) settledNewHiresCount / newHiresCount * 100 : 0;

        // 3. 종합 이직률 (Total Turnover Rate): 최근 1년 간 퇴사율 (퇴사자 / 1년 전 재직 인원)
        long employeesOneYearAgo = allEmployees.stream()
                .filter(e -> !e.getHireDate().isAfter(oneYearAgo) &&
                        (e.getTerminationDate() == null || e.getTerminationDate().isAfter(oneYearAgo)))
                .count();
        
        long retiredInLastYear = allEmployees.stream()
                .filter(e -> e.getTerminationDate() != null && e.getTerminationDate().isAfter(oneYearAgo))
                .count();
                
        double totalTurnoverRate = employeesOneYearAgo > 0 ? (double) retiredInLastYear / employeesOneYearAgo * 100 : 0;

        // 4. 신입 1년 내 이직률 (New Hire Turnover Rate): 최근 1년 입사자 중 퇴사자 비율
        // (조기 이탈률: 3개월 미만 근무 후 퇴사자 비율로 수정)
        long earlyLeaverCount = newHiresInLastYear.stream()
                .filter(e -> {
                    if (e.getStatus() != EmployeeStatus.RETIRED) {
                        return false;
                    }
                    LocalDate endDate = e.getTerminationDate() != null ? e.getTerminationDate() : today;
                    long daysWorked = ChronoUnit.DAYS.between(e.getHireDate(), endDate);
                    return daysWorked < 90;
                })
                .count();
        
        double newHireTurnoverRate = newHiresCount > 0 ? (double) earlyLeaverCount / newHiresCount * 100 : 0;

        return RetirementSummaryDTO.builder()
                .retentionRate(Math.round(retentionRate * 100.0) / 100.0)
                .settlementRate(Math.round(settlementRate * 100.0) / 100.0)
                .totalTurnoverRate(Math.round(totalTurnoverRate * 100.0) / 100.0)
                .newHireTurnoverRate(Math.round(newHireTurnoverRate * 100.0) / 100.0)
                .build();
    }

    /**
     * 퇴사 사유별 통계 데이터를 집계합니다.
     * (1년 미만 조기 퇴사자 vs 전체 퇴사자)
     *
     * @return 사유별 퇴직 통계 DTO
     */
    @Transactional(readOnly = true)
    public ExitReasonStatsResponseDTO getExitReasonStats() {
        List<Retirement> allRetirements = retirementRepository.findAll().stream()
                .filter(r -> r.getEmployee().getEmployeeDepartment() != null && r.getEmployee().getEmployeeDepartment().getDepartmentId() != 0)
                .collect(Collectors.toList());

        // 1. 1년 미만 조기 퇴사자 통계
        List<ExitReasonStatsResponseDTO.ExitReasonStat> earlyLeavers = allRetirements.stream()
                .filter(r -> r.getWorkingDays() < 365)
                .collect(Collectors.groupingBy(r -> r.getExitReason().getReasonName(), Collectors.counting()))
                .entrySet().stream()
                .map(entry -> ExitReasonStatsResponseDTO.ExitReasonStat.builder()
                        .reasonName(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        // 2. 전체 퇴사자 통계
        List<ExitReasonStatsResponseDTO.ExitReasonStat> totalLeavers = allRetirements.stream()
                .collect(Collectors.groupingBy(r -> r.getExitReason().getReasonName(), Collectors.counting()))
                .entrySet().stream()
                .map(entry -> ExitReasonStatsResponseDTO.ExitReasonStat.builder()
                        .reasonName(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        return ExitReasonStatsResponseDTO.builder()
                .earlyLeavers(earlyLeavers)
                .totalLeavers(totalLeavers)
                .build();
    }

    /**
     * 현재 재직자들의 근속 연수 분포를 계산합니다.
     *
     * @return 근속 연수별 인원 비율 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<TenureDistributionDTO> getTenureDistributionStats() {
        // 관리자 제외
        List<Employee> activeEmployees = employeeRepository.findAll().stream()
                .filter(e -> e.getStatus() != EmployeeStatus.RETIRED)
                .filter(e -> e.getEmployeeDepartment() != null && e.getEmployeeDepartment().getDepartmentId() != 0)
                .collect(Collectors.toList());
                
        long totalActiveEmployees = activeEmployees.size();
        if (totalActiveEmployees == 0) return new ArrayList<>();

        Map<Long, Long> tenureCounts = activeEmployees.stream()
                .collect(Collectors.groupingBy(
                        e -> ChronoUnit.YEARS.between(e.getHireDate(), LocalDate.now()),
                        Collectors.counting()
                ));

        List<TenureDistributionDTO> stats = new ArrayList<>();
        for (long i = 0; i < 10; i++) { // 0~9년차
            long year = i;
            long count = tenureCounts.getOrDefault(year, 0L);
            double rate = (double) count / totalActiveEmployees * 100.0;
            stats.add(TenureDistributionDTO.builder()
                    .tenureRange(year + "년차")
                    .percentage(Math.round(rate * 100.0) / 100.0)
                    .build());
        }

        // 10년차 이상 그룹
        long countOver10 = tenureCounts.entrySet().stream()
                .filter(entry -> entry.getKey() >= 10)
                .mapToLong(Map.Entry::getValue)
                .sum();
        
        double rateOver10 = (double) countOver10 / totalActiveEmployees * 100.0;
        stats.add(TenureDistributionDTO.builder()
                .tenureRange("10년차 이상")
                .percentage(Math.round(rateOver10 * 100.0) / 100.0)
                .build());

        return stats;
    }

    /**
     * 분기별 신입 사원 정착률 및 이직률 통계를 계산합니다.
     * (데이터가 존재하는 모든 분기에 대해)
     *
     * @return 신입 정착률 및 이직률 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<NewHireStatDTO> getNewHireStats() {
        // 관리자 제외
        List<Employee> allEmployees = employeeRepository.findAll().stream()
                .filter(e -> e.getEmployeeDepartment() != null && e.getEmployeeDepartment().getDepartmentId() != 0)
                .collect(Collectors.toList());
                
        LocalDate today = LocalDate.now();

        // 1. 입사일 기준으로 분기별 그룹화
        Map<String, List<Employee>> employeesByQuarter = allEmployees.stream()
                .collect(Collectors.groupingBy(e -> {
                    int year = e.getHireDate().getYear();
                    int quarter = e.getHireDate().get(IsoFields.QUARTER_OF_YEAR);
                    return year + "년 " + quarter + "분기";
                }));

        List<NewHireStatDTO> stats = new ArrayList<>();

        // 2. 각 분기별로 통계 계산
        for (Map.Entry<String, List<Employee>> entry : employeesByQuarter.entrySet()) {
            String quarterLabel = entry.getKey();
            List<Employee> hiredInQuarter = entry.getValue();
            long total = hiredInQuarter.size();

            long settledCount = 0;
            long earlyLeaverCount = 0;

            for (Employee employee : hiredInQuarter) {
                // 정착률 계산: (재직 중 + 3개월 이상 근무 후 퇴사)
                boolean isSettled = false;
                if (employee.getStatus() != EmployeeStatus.RETIRED) {
                    isSettled = true; // 현재 재직 중이면 정착
                } else {
                    LocalDate endDate = employee.getTerminationDate() != null ? employee.getTerminationDate() : today;
                    long daysWorked = ChronoUnit.DAYS.between(employee.getHireDate(), endDate);
                    if (daysWorked >= 90) {
                        isSettled = true; // 3개월 이상 근무 후 퇴사면 정착
                    }
                }

                if (isSettled) {
                    settledCount++;
                }

                // 신입 이직률 계산: 3개월 미만 근무 후 퇴사한 사람 (조기 이탈자)
                boolean isEarlyLeaver = false;
                if (employee.getStatus() == EmployeeStatus.RETIRED) {
                    LocalDate endDate = employee.getTerminationDate() != null ? employee.getTerminationDate() : today;
                    long daysWorked = ChronoUnit.DAYS.between(employee.getHireDate(), endDate);
                    if (daysWorked < 90) {
                        isEarlyLeaver = true;
                    }
                }

                if (isEarlyLeaver) {
                    earlyLeaverCount++;
                }
            }

            double settlementRate = total > 0 ? (double) settledCount / total * 100 : 0;
            double turnoverRate = total > 0 ? (double) earlyLeaverCount / total * 100 : 0;

            stats.add(NewHireStatDTO.builder()
                    .quarter(quarterLabel)
                    .settlementRate(Math.round(settlementRate * 100.0) / 100.0)
                    .turnoverRate(Math.round(turnoverRate * 100.0) / 100.0)
                    .build());
        }

        // 3. 최신 분기 순으로 정렬
        stats.sort(Comparator.comparing(NewHireStatDTO::getQuarter).reversed());

        return stats;
    }

    /**
     * 부서별 이직률 통계를 계산합니다.
     *
     * @return 부서별 이직률 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<DepartmentTurnoverDTO> getDepartmentTurnoverStats() {
        List<Employee> allEmployees = employeeRepository.findAll();
        LocalDate today = LocalDate.now();

        Map<String, List<Employee>> employeesByDept = allEmployees.stream()
                .filter(e -> e.getEmployeeDepartment() != null)
                // 관리자 부서(0)만 제외 (대기 발령 부서(-1)는 포함)
                .filter(e -> e.getEmployeeDepartment().getDepartmentId() != 0)
                .collect(Collectors.groupingBy(e -> e.getEmployeeDepartment().getDepartmentName()));

        return employeesByDept.entrySet().stream()
                .map(entry -> {
                    String deptName = entry.getKey();
                    List<Employee> deptEmployees = entry.getValue();
                    long total = deptEmployees.size();
                    
                    // 퇴사자 수 계산: status가 'R'이거나, terminationDate가 오늘 이전인 경우
                    long retired = deptEmployees.stream()
                            .filter(e -> e.getStatus() == EmployeeStatus.RETIRED || 
                                         (e.getTerminationDate() != null && !e.getTerminationDate().isAfter(today)))
                            .count();
                            
                    long current = total - retired;
                    double turnoverRate = total > 0 ? (double) retired / total * 100 : 0;

                    return DepartmentTurnoverDTO.builder()
                            .departmentName(deptName)
                            .currentCount(current)
                            .retiredCount(retired)
                            .turnoverRate(Math.round(turnoverRate * 100.0) / 100.0)
                            .build();
                })
                .sorted(Comparator.comparing(DepartmentTurnoverDTO::getTurnoverRate).reversed()) // 이직률 내림차순 정렬
                .collect(Collectors.toList());
    }

    /**
     * 퇴사 결재 승인 처리를 수행합니다.
     * 직원 정보에 퇴사일을 업데이트하고, 퇴사 기록을 생성합니다.
     *
     * @param employeeNumber 사번
     * @param terminationDate 퇴사일
     * @param terminationReasonId 퇴사 사유 ID
     * @param terminationReasonDetail 상세 사유
     */
    @Transactional
    public void processRetirementApproval(String employeeNumber, LocalDate terminationDate, Integer terminationReasonId, String terminationReasonDetail) {
        // 1. 직원 조회
        Employee employee = employeeRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

        // 2. 퇴사 사유 조회
        ExitReasonMaster exitReason = exitReasonMasterRepository.findById(terminationReasonId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE)); // 적절한 에러 코드로 변경 필요

        // 3. 직원 정보 업데이트 (퇴사일, 보관 만료일 설정)
        employee.updateTerminationDate(terminationDate);
        employee.updateRetentionExpireAt(terminationDate.plusYears(5));

        // 4. 근속 일수 계산
        long workingDays = ChronoUnit.DAYS.between(employee.getHireDate(), terminationDate);

        // 5. 퇴사 기록 생성 및 저장
        Retirement retirement = Retirement.builder()
                .employee(employee)
                .exitReason(exitReason)
                .exitDate(terminationDate)
                .detailReason(terminationReasonDetail)
                .workingDays((int) workingDays)
                .build();

        retirementRepository.save(retirement);
    }

    /**
     * 관리자가 직원을 강제로 퇴직 처리합니다.
     *
     * @param employeeId 대상 직원 ID
     * @param request 퇴직 정보
     */
    @Transactional
    public void forceTerminateEmployee(Integer employeeId, ForceRetirementRequestDTO request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

        // 이미 퇴사 처리된 직원은 중복 처리 방지
        if (employee.getStatus() == EmployeeStatus.RETIRED) {
            throw new BusinessException(ErrorCode.ALREADY_RETIRED);
        }

        processRetirementApproval(
                employee.getEmployeeNumber(),
                request.getTerminationDate(),
                request.getTerminationReasonId(),
                request.getTerminationReasonDetail()
        );
    }
}
