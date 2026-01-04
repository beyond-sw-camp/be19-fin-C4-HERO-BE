package com.c4.hero.domain.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: AttendanceDashboardSummaryDTO
 * Description: 근태 점수 대시보드 상단 요약 카드를 위한 통계 정보를 담는 DTO
 *
 * History
 * 2025/12/24 (이지윤) 최초 작성 및 코딩 컨벤션 적용
 * </pre>
 *
 * 근태 점수 대시보드 화면 상단의 요약 카드(전체 직원 수, 우수/위험 직원 수 등)에
 * 표시할 집계 값을 전달하기 위한 DTO입니다.
 *
 * 예)
 * - 전체 직원 수
 * - 우수 직원 수 (95점 이상)
 * - 위험 직원 수 (85점 이하)
 * 등
 *
 * @author 이지윤
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AttendanceDashboardSummaryDTO {

    /** 전체 직원 수 */
    private Long totalEmployees;

    /** 우수 직원 수 (95점 이상) */
    private Long excellentEmployees;

    /** 위험 직원 수 (85점 이하) */
    private Long riskyEmployees;
}
