package com.c4.hero.domain.payroll.batch.dto;

/**
 * <pre>
 * DTO Name : PayrollBatchTargetEmployeeResponse
 * Description : 급여 배치 대상 사원 조회 응답 DTO
 *
 * History
 *  2025/12/15 - 동근 최초 작성
 * </pre>
 *
 *  @author 동근
 *  @version 1.0
 *
 * @param employeeId     사원 ID
 * @param employeeName   사원 이름
 * @param departmentName 부서명 (없을 경우 null)
 */
public record PayrollBatchTargetEmployeeResponseDTO(
        Integer employeeId,
        String employeeName,
        String departmentName
) {}
