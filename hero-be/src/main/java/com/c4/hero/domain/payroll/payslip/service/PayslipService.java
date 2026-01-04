package com.c4.hero.domain.payroll.payslip.service;

import com.c4.hero.domain.auth.security.LoginOnly;
import com.c4.hero.domain.payroll.payslip.dto.PayslipBaseDTO;
import com.c4.hero.domain.payroll.payslip.dto.PayslipDetailDTO;
import com.c4.hero.domain.payroll.payslip.mapper.PayslipQueryMapper;
import com.c4.hero.domain.payroll.report.dto.PayItemDTO;
import com.c4.hero.domain.payroll.report.mapper.EmployeePayrollReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

/**
 * 급여 명세서 조회 서비스
 *
 * <pre>
 * Class Name: PayslipService
 * Description: 사원의 급여 명세서(Payslip) 조회 비즈니스 로직을 담당
 *              - 급여월 기준 명세서 기본 정보 조회
 *              - 수당/공제 항목을 조합하여 명세서 상세 DTO 구성
 *
 * Design Note
 *  - 명세서 기본 정보: PayslipQueryMapper (payslip 도메인 전용)
 *  - 수당/공제 항목: EmployeePayrollReportMapper 재사용 (items)
 *
 * History
 * 2025/12/14 - 동근 report 도메인에서 payslip 도메인으로 분리
 * 2026/01/03 - 동근 권한 인가 정책 추가
 * </pre>
 *
 * @author 동근
 * @version 1.1
 */
@LoginOnly
@Service
@RequiredArgsConstructor
public class PayslipService {

    /** 급여 명세서 기본 정보 조회용 Mapper */
    private final PayslipQueryMapper payslipQueryMapper;

    /** 급여 항목(수당/공제) 조회 재사용 Mapper */
    private final EmployeePayrollReportMapper reportMapper;


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

        PayslipBaseDTO base = payslipQueryMapper.selectPayslipBase(employeeId, targetMonth);
        if (base == null) {
            throw new IllegalArgumentException("해당 월 명세서가 없습니다.");
        }

        List<PayItemDTO> allowances = reportMapper.selectAllowanceItems(employeeId, targetMonth);
        List<PayItemDTO> deductions = reportMapper.selectDeductionItems(employeeId, targetMonth);

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
}
