package com.c4.hero.domain.settings.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 근무제 템플릿(WorkSystemTemplate) 조회 응답 DTO.
 *
 * <p>
 * 설정 화면에서 근무제 템플릿 리스트 또는 단건을 조회할 때
 * 출퇴근 시간, 휴게 시간, 사유, 근무제 타입 정보를 전달하기 위한 모델입니다.
 * </p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SettingWorkSystemResponseDTO {

    /** 근무제 템플릿 ID */
    private Integer workSystemTemplateId;

    /** 출근 시간 (HH:mm:ss 포맷) */
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    /** 퇴근 시간 (HH:mm:ss 포맷) */
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    /** 휴게 시간(분 단위, 예: 60 → 60분 휴게) */
    private Integer breakMinMinutes;

    /** 템플릿 설명 또는 사유 */
    private String reason;

    /** 근무제 유형 ID (WorkSystemType FK) */
    private Integer workSystemTypeId;
}
