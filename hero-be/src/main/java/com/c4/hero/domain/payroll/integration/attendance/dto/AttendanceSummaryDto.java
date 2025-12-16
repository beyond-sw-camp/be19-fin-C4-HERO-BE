package com.c4.hero.domain.payroll.integration.attendance.dto;

/**
 * <pre>
 * DTO Name : AttendanceSummaryDto
 * Description : 급여 계산을 위한 근태 요약 정보 DTO
 *
 * 구성 정보
 *  - 근무 일수
 *  - 총 근무 시간
 *  - 초과 근무 시간
 *
 * 사용 목적
 *  - 급여 계산 및 리포트용 근태 요약 데이터 전달
 *  - 근태 도메인의 상세 데이터를 급여 도메인에 전달할 때 최소 정보만 노출
 *
 * History
 *  2025/12/15 - 동근 최초 작성
 * </pre>
 *
 *  @author 동근
 *  @version 1.0
 *
 * @param workDays      해당 월 근무 일수
 * @param workHours    해당 월 총 근무 시간 (시간 단위)
 * @param overtimeHours 초과 근무 시간 (시간 단위)
 */
public record AttendanceSummaryDto(
        int workDays,
        int workHours,
        int overtimeHours
) {}
