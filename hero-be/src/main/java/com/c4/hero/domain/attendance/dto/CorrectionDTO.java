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
    private int correction_request_id;
    private String target_date;
    private String corrected_start;
    private String corrected_end;
    private String reason;
    private String startTime;
    private String endTime;
}
