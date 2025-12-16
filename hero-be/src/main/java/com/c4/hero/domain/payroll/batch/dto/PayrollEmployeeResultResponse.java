package com.c4.hero.domain.payroll.batch.dto;

/**
 * <pre>
 * DTO Name : PayrollEmployeeResultResponse
 * Description : 급여 배치 내 사원별 급여 계산 결과 응답 DTO
 *
 *
 * History
 *  2025/12/15 - 동근 최초 작성
 * </pre>
 *
 *  @author 동근
 *  @version 1.0
 *
 * @param payrollId      급여 ID
 * @param employeeId     사원 ID
 * @param employeeName   사원 이름
 * @param departmentName 부서명 (없을 경우 null 또는 '-')
 * @param salaryMonth    급여월 (YYYY-MM)
 * @param status         급여 상태 (READY / CALCULATED / FAILED / CONFIRMED)
 * @param baseSalary     기본급
 * @param overtimePay    초과근무 수당
 * @param allowanceTotal 수당 합계
 * @param deductionTotal 공제 합계
 * @param totalPay       실지급액
 */
public record PayrollEmployeeResultResponse(
        Integer payrollId,
        Integer employeeId,
        String employeeName,
        String departmentName,
        String salaryMonth,
        String status,
        Integer baseSalary,
        Integer overtimePay,
        Integer allowanceTotal,
        Integer deductionTotal,
        Integer totalPay
) {}
