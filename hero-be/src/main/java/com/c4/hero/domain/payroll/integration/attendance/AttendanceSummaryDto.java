package com.c4.hero.domain.payroll.integration.attendance;

public record AttendanceSummaryDto(
        int workDays,
        int workHours,
        int overtimeHours
) {}
