package com.c4.hero.domain.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * <pre>
 * Class Name: OvertimeDTO
 * Description: 개인 초과 근무(연장 근무) 기록 정보를 담는 DTO
 *
 * History
 * 2025/12/10 (이지윤) 최초 작성 및 컨벤션 적용
 * </pre>
 *
 * @author 이지윤
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OvertimeDTO {

    /** 초과 근무 PK (식별자) */
    private Integer overtimeId;

    /** 초과 근무 일자 (yyyy-MM-dd) */
    private LocalDate date;

    /** 초과 근무 시작 시간 (HH:mm 또는 HH:mm:ss) */
    private LocalTime startTime;

    /** 초과 근무 종료 시간 (HH:mm 또는 HH:mm:ss) */
    private LocalTime endTime;

    /** 초과 근무 시간(단위: 시간, 예: 1.5 = 1시간 30분) */
    private float overtimeHours;

    /** 초과 근무 사유 */
    private String reason;
}
