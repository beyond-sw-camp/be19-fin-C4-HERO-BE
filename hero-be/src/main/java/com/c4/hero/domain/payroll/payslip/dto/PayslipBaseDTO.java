package com.c4.hero.domain.payroll.payslip.dto;

/**
 * 급여명세서(Payslip) 기본 정보 DTO
 * PDF 생성/다운로드 화면에서 사원/급여 기본 데이터를 제공할 때 사용
 * 추후에 급여명세서 안에 들어가는 내용 더 추가 할 예정
 * <pre>
 * Class Name: PayslipBaseDTO
 * Description: 급여명세서 PDF 기본 정보(월/사원/부서/급여 요약) DTO
 *
 * History
 * 2025/12/08 동근 최초 작성
 * 2025/12/14 동근 report 도메인에서 payslip 도메인으로 분리
 * </pre>
 *
 * @author 동근
 * @version 1.1
 */
public record PayslipBaseDTO(
        String salaryMonth,
        String employeeName,
        String departmentName,
        int baseSalary,
        int grossPay,
        int totalDeduction,
        int netPay,
        String pdfUrl
) {}

