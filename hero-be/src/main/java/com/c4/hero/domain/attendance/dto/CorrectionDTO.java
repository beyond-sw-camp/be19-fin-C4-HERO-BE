package com.c4.hero.domain.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * <pre>
 * Class Name: CorrectionDTO
 * Description: 근태 정정(출퇴근 시간 수정) 요청 정보를 담는 DTO
 *
 * History
 * 2025/12/10 (이지윤) 최초 작성 및 컨벤션 적용
 * </pre>
 *
 * @author 이지윤
 * @version 1.1
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CorrectionDTO {

    /** 근태 정정 요청 PK (식별자) */
    private Integer correctionId;

    /** 정정 대상 근무 일자 (yyyy-MM-dd) */
    private LocalDate date;

    /** 정정 전 출근 시간 (HH:mm 또는 HH:mm:ss) */
    private LocalTime prevStartTime;

    /** 정정 전 퇴근 시간 (HH:mm 또는 HH:mm:ss) */
    private LocalTime prevEndTime;

    /** 정정 사유 */
    private String reason;

    /** 정정 후 출근 시간 (HH:mm 또는 HH:mm:ss) */
    private LocalTime newStartTime;

    /** 정정 후 퇴근 시간 (HH:mm 또는 HH:mm:ss) */
    private LocalTime newEndTime;
}
