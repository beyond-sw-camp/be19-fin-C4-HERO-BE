package com.c4.hero.domain.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CorrectionDTO {
    private int correctionId;
    private String date;
    private String prevStartTime;
    private String prevEndTime;
    private String reason;
    private String newStartTime;
    private String newEndTime;
}
