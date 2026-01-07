package com.c4.hero.domain.dashboard.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;

/**
 * <pre>
 * Class Name  : WorkSystemTemplateDTO
 * Description : 근무제 템플릿 정보 DTO
 *
 * History
 * 2026/01/06 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Getter
@Setter
@ToString
public class WorkSystemTemplateDTO {

    private Integer workSystemTemplateId;  // 근무제 템플릿 ID
    private LocalTime startTime;           // 출근 시간
    private LocalTime endTime;             // 퇴근 시간
    private Integer breakMinMinutes;       // 휴게 시간 (분)
    private String reason;                 // 사유
    private Integer workSystemTypeId;      // 근무제 타입 ID
}