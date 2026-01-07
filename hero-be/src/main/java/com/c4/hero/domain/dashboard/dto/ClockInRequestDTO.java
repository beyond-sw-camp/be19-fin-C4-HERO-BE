package com.c4.hero.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * <pre>
 * Class Name  : ClockInRequestDTO
 * Description : 출근 요청 DTO
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
public class ClockInRequestDTO {

    /** 근무 일자 */
    private LocalDate workDate;

    /** 출근 시각 */
    private LocalTime startTime;

    /** 근무제 타입 ID (기본값: 1 - 일반근무제) */
    private Integer workSystemTypeId = 1;

    /** 근무제 템플릿 ID (기본값: 1 - 기본 템플릿) */
    private Integer workSystemTemplateId = 1;

}