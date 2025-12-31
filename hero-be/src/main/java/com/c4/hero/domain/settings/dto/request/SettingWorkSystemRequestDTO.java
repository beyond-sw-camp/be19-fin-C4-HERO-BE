package com.c4.hero.domain.settings.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 근무제 템플릿(WorkSystemTemplate) 설정/수정 요청 DTO.
 *
 * <p>
 * 프론트엔드에서 근무제 템플릿의 출퇴근 시간, 휴게 시간, 사유, 근무제 타입 등을
 * 저장·수정할 때 사용하는 요청 바디 모델입니다.
 * </p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SettingWorkSystemRequestDTO {

    /** 수정 대상 근무제 템플릿 ID (null이면 신규 생성으로 해석 가능) */
    private Integer workSystemTemplateId;

    /** 출근 시간 (HH:mm:ss 포맷) */
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    /** 퇴근 시간 (HH:mm:ss 포맷) */
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    /** 휴게 시간(분 단위, 예: 60 → 60분 휴게) */
    private Integer breakMinMinutes;

    /** 템플릿에 대한 설명 또는 변경 사유 */
    private String reason;

    /** 연결할 근무제 유형 ID (WorkSystemType FK) */
    private Integer workSystemTypeId;
}
