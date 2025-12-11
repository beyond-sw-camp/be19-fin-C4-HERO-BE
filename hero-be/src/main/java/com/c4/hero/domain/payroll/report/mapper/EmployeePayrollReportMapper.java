package com.c4.hero.domain.payroll.report.mapper;

import com.c4.hero.domain.payroll.integration.attendance.AttendanceSummaryDto;
import com.c4.hero.domain.payroll.report.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 사원 급여 리포트 조회용 MyBatis Mapper
 * <pre>
 * Class Name: EmployeePayrollReportMapper
 * Description: 내 급여 요약, 이력, 명세서 등 급여 리포트 관련 조회 Mapper
 *
 * History
 * 2025/12/08 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Mapper
public interface EmployeePayrollReportMapper {

    /**
     *  현재 월or선택 월 요약 정보 (실수령, 지급총액, 공제총액)
     * @param employeeId 사원 ID
     * @param salaryMonth 급여월(YYYY-MM)
     * @return 월별 급여 요약 Core DTO
     */
    MyPaySummaryCoreDTO selectMyPayrollSummary(
            @Param("employeeId") Integer employeeId,
            @Param("salaryMonth") String salaryMonth
    );


    /**
     * 특정 월의 수당 항목 리스트 조회
     * @param employeeId 사원 ID
     * @param salaryMonth 급여월(YYYY-MM)
     * @return 수당 항목 리스트
     */
    List<PayItemDTO> selectAllowanceItems(
            @Param("employeeId") Integer employeeId,
            @Param("salaryMonth") String salaryMonth
    );

    /**
     * 특정 월의 공제 항목 리스트 조회
     * @param employeeId 사원 ID
     * @param salaryMonth 급여월(YYYY-MM)
     * @return 공제 항목 리스트
     */
    List<PayItemDTO> selectDeductionItems(
            @Param("employeeId") Integer employeeId,
            @Param("salaryMonth") String salaryMonth
    );


    /**
     * 특정 월의 근태 요약 정보 조회 (근무일수/시간/초과근무시간)
     * @param employeeId  사원 ID
     * @param salaryMonth 급여월(YYYY-MM)
     * @return 근태 요약 DTO
     */
    AttendanceSummaryDto selectAttendanceSummary(
            @Param("employeeId") Integer employeeId,
            @Param("salaryMonth") String salaryMonth
    );

    /**
     * 최근 12개월 급여 이력
     * @param employeeId 사원 ID
     * @param fromMonth 시작 월(YYYY-MM)
     * @param toMonth 종료 월(YYYY-MM)
     * @return 급여 이력 테이블 행 리스트
     */
    List<PayHistoryRowDTO> selectPayHistory(
            @Param("employeeId") Integer employeeId,
            @Param("fromMonth") String fromMonth,
            @Param("toMonth") String toMonth
    );

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
