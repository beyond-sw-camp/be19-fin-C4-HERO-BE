package com.c4.hero.domain.payroll.report.service;


import com.c4.hero.domain.auth.security.LoginOnly;
import com.c4.hero.domain.payroll.report.dto.MyPaySummaryCoreDTO;
import com.c4.hero.domain.payroll.report.dto.MyPaySummaryDTO;
import com.c4.hero.domain.payroll.report.dto.PayHistoryChartPointDTO;
import com.c4.hero.domain.payroll.report.dto.PayHistoryResponseDTO;
import com.c4.hero.domain.payroll.report.dto.PayHistoryRowDTO;
import com.c4.hero.domain.payroll.report.dto.PayItemDTO;
import com.c4.hero.domain.payroll.report.mapper.EmployeePayrollReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;


/**
 * 급여 리포트 조회 서비스
 *
 * <pre>
 * Class Name: ReportService
 * Description: 사원의 급여 리포트 조회 비즈니스 로직을 담당
 *              - 내 급여 요약 조회
 *              - 최근 급여 이력 조회 (차트/테이블)
 *
 *
 * History
 * 2025/12/08 - 동근 최초 작성
 * 2025/12/14 - 동근 payslip 조회 로직 제거 및 report 역할 명확화
 * 2026/01/03 - 동근 권한 인가 정책 추가
 * </pre>
 *
 * @author 동근
 * @version 1.2
 */
@LoginOnly
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
