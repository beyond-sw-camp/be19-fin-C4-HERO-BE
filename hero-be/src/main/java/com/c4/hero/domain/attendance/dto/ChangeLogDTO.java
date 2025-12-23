package com.c4.hero.domain.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

/**
 * <pre>
 * Class Name: ChangeLogDTO
 * Description: 근무제 변경 이력 정보를 담는 DTO
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
public class ChangeLogDTO {

    /** 근무제 변경 이력 PK (식별자) */
    private Integer workSystemChangeLogId;

    /** 근무제 적용 시작일(또는 변경일) (yyyy-MM-dd) */
    private String date;

    /** 근무제 변경 사유 */
    private String changeReason;

    /** 변경된 근무제의 출근 시간 (HH:mm 또는 HH:mm:ss) */
    private LocalTime startTime;

    /** 변경된 근무제의 퇴근 시간 (HH:mm 또는 HH:mm:ss) */
    private LocalTime endTime;

    /** 적용된 근무제 이름 (예: 시차출퇴근제, 선택적 근로제 등) */
    private String workSystemName;
}
