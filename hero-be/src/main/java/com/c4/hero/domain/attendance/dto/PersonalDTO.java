package com.c4.hero.domain.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * <pre>
 * Class Name: PersonalDTO
 * Description: 개인 근태 기록 조회 페이지에서 사용하는 DTO
 *
 * History
 * 2025/12/09 (이지윤) 최초 작성 및 컨벤션 적용
 * </pre>
 *
 * @author 이지윤
 * @version 1.0
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PersonalDTO {

    /** 근태 기록 고유 ID(PK) */
    private Integer attendanceId;

    /** 근무 날짜 (yyyy-MM-dd) */
    private LocalDate workDate;

    /** 근무 상태 (정상, 지각, 결근 등) */
    private String state;

    /** 근무 시작 시간 (HH:mm) */
    private LocalTime startTime;

    /** 근무 종료 시간 (HH:mm) */
    private LocalTime endTime;

    /** 총 근무 시간(분 단위 또는 계산된 시간) */
    private String workDuration;

    /** 적용 근무제 이름 */
    private String workSystemName;
}
