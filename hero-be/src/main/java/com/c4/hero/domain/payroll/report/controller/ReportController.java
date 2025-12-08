package com.c4.hero.domain.payroll.report.controller;

import com.c4.hero.domain.payroll.report.dto.*;
import com.c4.hero.domain.payroll.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/me/payroll")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService service;


    private Integer getEmployeeId(Principal principal) {
        // 테스트용으로 1번 사원 고정 (연동 후 변경 예정)
        return 1;
    }

    // 내 급여 조회
    @GetMapping
    public ResponseEntity<MyPaySummaryDTO> getMyPayroll(
            @RequestParam(required = false) String month,
            Principal principal
    ) {
        Integer employeeId = getEmployeeId(principal);
        return ResponseEntity.ok(service.getMyPayroll(employeeId, month));
    }

    // 명세서 모달
    @GetMapping("/payslip")
    public ResponseEntity<PayslipDetailDTO> getPayslip(
            @RequestParam String month,
            Principal principal
    ) {
        Integer employeeId = getEmployeeId(principal);
        return ResponseEntity.ok(service.getPayslipDetail(employeeId, month));
    }

    // 급여 이력 (최근 12개월)
    @GetMapping("/history")
    public ResponseEntity<PayHistoryResponseDTO> getHistory(Principal principal) {
        Integer employeeId = getEmployeeId(principal);
        return ResponseEntity.ok(service.getPayHistory(employeeId));
    }
}
