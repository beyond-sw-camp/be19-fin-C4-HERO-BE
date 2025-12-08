package com.c4.hero.domain.payroll.report.service;

import com.c4.hero.domain.payroll.report.dto.*;
import com.c4.hero.domain.payroll.report.mapper.EmployeePayrollReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;


/**
 * 급여 리포트 서비스 (사원의 내 급여 요약, 명세서 상세, 급여 이력(차트/테이블) 조회를 담당)
 * <pre>
 * Class Name: ReportService
 * Description: 내 급여, 명세서, 급여 이력 조회 비즈니스 로직 처리 서비스
 *
 * History
 * 2025/12/08 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final EmployeePayrollReportMapper mapper;


    /**
     *  내 급여 요약 조회
     *      Core DTO(MyPaySummaryCoreDto)로 DB 요약 정보 조회
     *      수당/공제 항목은 별도 쿼리로 조회해서 MyPaySummaryDto로 합쳐서 반환
     * @param employeeId 사원 ID
     * @param month 조회할 급여월(YYYY-MM)
     * @return 내 급여 요약 DTO
     */
    public MyPaySummaryDTO getMyPayroll(Integer employeeId, String month) {
        String targetMonth = (month != null && !month.isBlank())
                ? month
                : YearMonth.now().toString();

        // 1. 급여 요약 Core 정보 조회 (12필드 DTO)
        MyPaySummaryCoreDTO core = mapper.selectMyPayrollSummary(employeeId, targetMonth);
        if (core == null) {
            throw new IllegalArgumentException("해당 월 급여 데이터가 없습니다.");
        }

        // 2. 수당/공제 항목 리스트 조회
        List<PayItemDTO> allowances = mapper.selectAllowanceItems(employeeId, targetMonth);
        List<PayItemDTO> deductions = mapper.selectDeductionItems(employeeId, targetMonth);

        // 3. Core DTO + 수당/공제 리스트로 최종 DTO 조립
        return new MyPaySummaryDTO(
                core.salaryMonth(),
                core.baseSalary(),
                core.netPay(),
                core.grossPay(),
                core.totalDeduction(),
                core.workDays(),
                core.workHours(),
                core.overtimeHours(),
                core.payDayLabel(),
                core.bankName(),
                core.bankAccountNumber(),
                core.accountHolder(),
                allowances,
                deductions
        );
    }

    /**
     * 명세서 상세 조회(PayslipBaseDto로 기본 정보 조회)
     * PayslipBaseDTO로 기본 정보를 조회하고, 수당/공제 항목을 조합해서 PayslipDetailDTO로 반환
     * @param employeeId 사원 ID
     * @param month 조회할 급여월(YYYY-MM)
     * @return 명세서 상세 DTO
     */
    public PayslipDetailDTO getPayslipDetail(Integer employeeId, String month) {
        String targetMonth = (month != null && !month.isBlank())
                ? month
                : YearMonth.now().toString();

        PayslipBaseDTO base = mapper.selectPayslipBase(employeeId, targetMonth);
        if (base == null) {
            throw new IllegalArgumentException("해당 월 명세서가 없습니다.");
        }
        // 수당/공제 항목 조회
        List<PayItemDTO> allowances = mapper.selectAllowanceItems(employeeId, targetMonth);
        List<PayItemDTO> deductions = mapper.selectDeductionItems(employeeId, targetMonth);

        // 상세 DTO 조립
        return new PayslipDetailDTO(
                base.salaryMonth(),
                base.employeeName(),
                base.departmentName(),
                base.baseSalary(),
                allowances,
                deductions,
                base.grossPay(),
                base.totalDeduction(),
                base.netPay(),
                base.pdfUrl()
        );
    }

    /**
     *  최근 12개월 급여 이력 + 차트 데이터
     *
     * @param employeeId 사원 ID
     * @return 급여 이력 응답 DTO
     */
    public PayHistoryResponseDTO getPayHistory(Integer employeeId) {

        YearMonth now = YearMonth.now();          // 현재 월
        YearMonth from = now.minusMonths(11);     // 최근 12개월

        // 급여 이력 행 데이터 조회
        List<PayHistoryRowDTO> rows = mapper.selectPayHistory(
                employeeId,
                from.toString(),   // YYYY-MM
                now.toString()
        );

        // chart 데이터 변환
        List<PayHistoryChartPointDTO> chart = rows.stream()
                .map(r -> new PayHistoryChartPointDTO(r.salaryMonth(), r.netPay()))
                .toList();

        // 평균 / 최대 / 최소
        int avg = rows.isEmpty() ? 0 :
                (int) rows.stream().mapToInt(PayHistoryRowDTO::netPay).average().orElse(0);

        int max = rows.stream().mapToInt(PayHistoryRowDTO::netPay).max().orElse(0);
        int min = rows.stream().mapToInt(PayHistoryRowDTO::netPay).min().orElse(0);


//     전월 대비 변화율 계산
        int momRate = 0;

        if (rows.size() >= 2) {
            PayHistoryRowDTO prev = rows.get(rows.size() - 2);
            PayHistoryRowDTO latest = rows.get(rows.size() - 1);

            int prevNet = prev.netPay();
            int latestNet = latest.netPay();

            if (prevNet != 0) {
                double rate = ((latestNet - prevNet) * 100.0) / prevNet;
                momRate = (int) Math.round(rate);
            }
        }

//        올해 누적 실수령액 계산
        int ytdNetPay = 0;

        if (!rows.isEmpty()) {
            String currentYear = rows.get(rows.size() - 1).salaryMonth().substring(0, 4); // "2025"

            ytdNetPay = rows.stream()
                    .filter(r -> r.salaryMonth().startsWith(currentYear + "-"))
                    .mapToInt(PayHistoryRowDTO::netPay)
                    .sum();
        }

        //최종 응답 조립
        return new PayHistoryResponseDTO(
                avg,
                max,
                min,
                momRate,
                ytdNetPay,
                chart,
                rows
        );
    }
}
