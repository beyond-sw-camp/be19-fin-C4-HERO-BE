package com.c4.hero.domain.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <pre>
 * Class Name: PersonalSummaryDTO
 * Description: 개인 근태 요약(이번 달 기준 및 오늘 근무제 정보)을 담는 DTO
 *
 * History
 * 2025/12/18 (이지윤) 최초 작성 및 코딩 컨벤션 적용
 * </pre>
 *
 * 개인 근태 이력 화면 상단 요약 카드(이번 달 근무일, 지각/결근, 오늘 근무제 등)에
 * 표시할 데이터를 한 번에 전달하기 위한 DTO입니다.
 *
 * @author 이지윤
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AttSummaryDTO {

    /** 이번 달 근무일(정상 출근일 수) */
    private Integer workDays;

    /** 오늘 근무제 이름 */
    private String todayWorkSystemName;

    /** 이번 달 지각 횟수 */
    private Integer lateCount;

    /** 이번 달 결근 횟수 */
    private Integer absentCount;

    /** 이번 달 조퇴 횟수 */
    private Integer earlyCount;
}
