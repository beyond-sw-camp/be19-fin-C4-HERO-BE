package com.c4.hero.domain.payroll.report.controller;

import com.c4.hero.domain.payroll.report.dto.*;
import com.c4.hero.domain.payroll.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * 사원 개인 급여 조회용 컨트롤러
 * <pre>
 * Class Name: ReportController
 * Description: 내 급여, 명세서, 급여 이력 조회 API 컨트롤러
 *
 * History
 * 2025/12/08 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@RestController
@RequestMapping("/api/me/payroll")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService service;

    /**
     * @param principal 사용자 인증 정보 (테스트용으로 1번 고정)
     */
    private Integer getEmployeeId(Principal principal) {
        // 테스트용으로 1번 사원 고정 (연동 후 변경 예정)
        return 1;
    }

    /**
     * 내 급여 요약 조회
     * @param month 조회 할 급여 월(YYYY-MM)
     * @param principal 사용자 인증 정보
     * @return 내 급여 요약 정보
     */
    @GetMapping
    public ResponseEntity<MyPaySummaryDTO> getMyPayroll(
            @RequestParam(required = false) String month,
            Principal principal
    ) {
        Integer employeeId = getEmployeeId(principal);
        return ResponseEntity.ok(service.getMyPayroll(employeeId, month));
    }


    /**
     * 급여명세서 모달
     * @param month 조회 할 급여 월(YYYY-MM)
     * @param principal 사용자 인증 정보
     * @return 명세서 상세 정보
     */
    @GetMapping("/payslip")
    public ResponseEntity<PayslipDetailDTO> getPayslip(
            @RequestParam String month,
            Principal principal
    ) {
        Integer employeeId = getEmployeeId(principal);
        return ResponseEntity.ok(service.getPayslipDetail(employeeId, month));
    }


    /**
     * 급여 이력 (최근 12개월)
     * @param principal 사용자 인증 정보
     * @return 급여 이력 및 통계 정보
     */
    @GetMapping("/history")
    public ResponseEntity<PayHistoryResponseDTO> getHistory(Principal principal) {
        Integer employeeId = getEmployeeId(principal);
        return ResponseEntity.ok(service.getPayHistory(employeeId));
    }
}
