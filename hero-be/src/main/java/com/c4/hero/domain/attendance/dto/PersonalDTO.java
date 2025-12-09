/**
 * 개인 근태 기록 페이지에 관한 DTO
 */

package com.c4.hero.domain.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PersonalDTO {
    private int attendanceId;             // PK 값
    private String workDate;              // 날짜
    private String state;                 // 상태
    private String startTime;             // 근무 시작 시간
    private String endTime;               // 근무 종료 시간
    private Integer  workDuration;          // 하루 근무 시간
    private String workSystemName;        // 근무제 이름
}
