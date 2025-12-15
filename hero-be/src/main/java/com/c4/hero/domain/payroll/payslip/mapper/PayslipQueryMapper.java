package com.c4.hero.domain.payroll.payslip.mapper;

import com.c4.hero.domain.payroll.payslip.dto.PayslipBaseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 급여 명세서 조회용 MyBatis Mapper
 *
 * <pre>
 * Class Name: PayslipQueryMapper
 * Description: 사원의 급여명세서(Payslip) 기본 정보를 조회하기 위한 SQL Mapper
 *              - 급여월 기준 명세서 기본 정보 조회
 *              - PDF 다운로드를 위한 URL 포함
 *
 * History
 * 2025/12/14 동근 report 도메인에서 payslip 도메인으로 분리
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Mapper
public interface PayslipQueryMapper {

    /**
     * 명세서 PDF URL 기본 정보
     * @param employeeId 사원 ID
     * @param salaryMonth 급여월(YYYY-MM)
     * @return 명세서 기본 정보 DTO
     */
    PayslipBaseDTO selectPayslipBase(
            @Param("employeeId") Integer employeeId,
            @Param("salaryMonth") String salaryMonth
    );
}
