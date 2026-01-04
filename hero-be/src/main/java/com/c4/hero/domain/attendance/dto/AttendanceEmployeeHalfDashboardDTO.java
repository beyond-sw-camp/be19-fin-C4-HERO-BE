package com.c4.hero.domain.attendance.dto;

import com.c4.hero.domain.attendance.type.AttendanceHalfType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * <pre>
 * Class Name : AttendanceEmployeeHalfDashboardDTO
 * Description : 직원 상세 근태 대시보드(차트 Drawer용) 응답 DTO
 *
 * History
 * 2025/12/24 이지윤 최초 작성
 * </pre>
 *
 * 응답 형태:
 * - employeeId/employeeName/employeeNumber (Drawer 헤더용)
 * - year + half (상/하반기)
 * - summary (상단 3개 카드)
 * - monthlyStats (6개월치 차트 데이터)
 *
 * @author 이지윤
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AttendanceEmployeeHalfDashboardDTO {

    /** 직원 PK */
    private Integer employeeId;

    /** 사번 */
    private String employeeNumber;

    /** 직원 이름 */
    private String employeeName;

    /** 조회 연도 */
    private Integer year;

    /** 조회 반기(H1/H2) */
    private AttendanceHalfType half;

    /** 상단 요약 카드 */
    private AttendanceEmployeeHalfSummaryDTO summary;

    /** 월별 집계(보통 6개월치) */
    private List<AttendanceEmployeeMonthlyStatDTO> monthlyStats;
}
