package com.c4.hero.domain.payroll.report.controller;


import com.c4.hero.domain.payroll.report.dto.MyPaySummaryDTO;
import com.c4.hero.domain.payroll.report.dto.PayHistoryResponseDTO;
import com.c4.hero.domain.payroll.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.security.Principal;

/**
 * 사원 급여 리포트 조회 컨트롤러
 *
 * <pre>
 * Class Name: ReportController
 * Description: 사원의 급여 요약 및 급여 이력 조회 API를 제공한다.
 *              - 월별 급여 요약 조회
 *              - 최근 12개월 급여 이력 및 통계 조회
 *
 * History
 * 2025/12/08 동근 최초 작성
 * 2025/12/14 동근 payslip 조회 API 분리 (명세서 관련 API 제거)
 * </pre>
 *
 * @author 동근
 * @version 1.1
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
