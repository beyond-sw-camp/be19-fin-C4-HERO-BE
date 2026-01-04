package com.c4.hero.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * <pre>
 * Class Name  : ClockOutRequestDTO
 * Description : 퇴근 요청 DTO
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
public class ClockOutRequestDTO {

    /** 근무 일자 */
    private LocalDate workDate;

    /** 퇴근 시각 */
    private LocalTime endTime;

    /** 근태 ID (출근 기록의 ID) */
    private Integer attendanceId;
}