package com.c4.hero.domain.payroll.report.mapper;

import com.c4.hero.domain.payroll.integration.attendance.AttendanceSummaryDto;
import com.c4.hero.domain.payroll.report.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EmployeePayrollReportMapper {
    // 현재 월or선택 월 요약 정보 (실수령, 지급총액, 공제총액)
    MyPaySummaryCoreDTO selectMyPayrollSummary(
            @Param("employeeId") Integer employeeId,
            @Param("salaryMonth") String salaryMonth
    );

    // 지급 항목 리스트
    List<PayItemDTO> selectAllowanceItems(
            @Param("employeeId") Integer employeeId,
            @Param("salaryMonth") String salaryMonth
    );

    // 공제 항목 리스트
    List<PayItemDTO> selectDeductionItems(
            @Param("employeeId") Integer employeeId,
            @Param("salaryMonth") String salaryMonth
    );

    // 근무 요약 (근무일수/시간/초과근무시간)
    AttendanceSummaryDto selectAttendanceSummary(
            @Param("employeeId") Integer employeeId,
            @Param("salaryMonth") String salaryMonth
    );

    // 최근 12개월 급여 이력
    List<PayHistoryRowDTO> selectPayHistory(
            @Param("employeeId") Integer employeeId,
            @Param("fromMonth") String fromMonth,
            @Param("toMonth") String toMonth
    );

    // 명세서 PDF URL 기본 정보 (없으면 null)
    PayslipBaseDTO selectPayslipBase(
            @Param("employeeId") Integer employeeId,
            @Param("salaryMonth") String salaryMonth
    );
}
