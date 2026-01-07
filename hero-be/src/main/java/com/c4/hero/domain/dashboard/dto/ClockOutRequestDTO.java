package com.c4.hero.domain.dashboard.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * <pre>
 * Class Name  : ClockOutRequestDTO
 * Description : 퇴근 요청 DTO
 *
 * History
 * 2025/12/26 (혜원) 최초 작성
 * 2026/01/06 (혜원) 휴게시간 포함 여부 및 근무시간 필드 추가
 * </pre>
 *
 * @author 혜원
 * @version 1.1
 */
@Getter
@Setter
@ToString
public class ClockOutRequestDTO {

    private Integer attendanceId;      // 근태 기록 ID (서비스에서 설정)
    private LocalDate workDate;        // 근무 일자
    private LocalTime endTime;         // 퇴근 시각
    private Boolean includeBreakTime;  // 휴게시간 포함 여부 (오후 1시 이후 true)
    private Integer workDuration;      // 근무시간 (분) - Service에서 계산 후 설정
}