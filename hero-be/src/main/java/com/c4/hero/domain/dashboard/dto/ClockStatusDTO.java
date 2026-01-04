package com.c4.hero.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * <pre>
 * Class Name  : ClockStatusDTO
 * Description : 현재 출퇴근 상태 응답 DTO
 *
 * History
 * 2025/12/26 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClockStatusDTO {

    /** 근태 ID */
    private Integer attendanceId;

    /** 근무 일자 */
    private LocalDate workDate;

    /** 출근 시각 */
    private LocalTime startTime;

    /** 퇴근 시각 */
    private LocalTime endTime;

    /** 근태 상태 (정상/지각/조퇴 등) */
    private String state;

    /** 출근 여부 */
    private Boolean isClockedIn;

    /** 퇴근 여부 */
    private Boolean isClockedOut;

    /** 근무시간(분) - 퇴근 후 DB에 저장된 값 */
    private Integer workDuration;
}