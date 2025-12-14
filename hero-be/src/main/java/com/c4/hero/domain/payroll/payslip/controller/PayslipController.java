package com.c4.hero.domain.payroll.payslip.controller;

import com.c4.hero.domain.payroll.payslip.dto.PayslipDetailDTO;
import com.c4.hero.domain.payroll.payslip.service.PayslipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * 사원 급여 명세서 조회 컨트롤러
 *
 * <pre>
 * Class Name: PayslipController
 * Description: 사원의 급여명세서 상세 조회 API를 제공한다.
 *              - 급여월 기준 명세서 상세 조회
 *              - PDF 다운로드를 위한 URL 제공
 *
 * History
 * 2025/12/14 동근 report 도메인에서 payslip 도메인으로 분리
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */

@RestController
@RequestMapping("/api/me/payroll")
@RequiredArgsConstructor
public class PayslipController {
    private final PayslipService payslipService;

    private Integer getEmployeeId(Principal principal) {
        return 1; // TODO: JWT 연동 후 교체
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
        return ResponseEntity.ok(payslipService.getPayslipDetail(employeeId, month));
    }
}
