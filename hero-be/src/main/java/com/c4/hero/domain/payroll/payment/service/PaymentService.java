package com.c4.hero.domain.payroll.payment.service;

import com.c4.hero.common.pagination.PageCalculator;
import com.c4.hero.common.pagination.PageInfo;
import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.payment.dto.PayrollPaymentDetailResponseDTO;
import com.c4.hero.domain.payroll.payment.dto.PayrollPaymentDetailSummaryResponseDTO;
import com.c4.hero.domain.payroll.payment.dto.PayrollPaymentSearchRequestDTO;
import com.c4.hero.domain.payroll.payment.dto.PayrollPaymentSearchRowResponseDTO;
import com.c4.hero.domain.payroll.payment.mapper.PaymentHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <pre>
 * Class Name : PaymentService
 * Description : 관리자(Admin) 급여 지급/조회 이력 조회 서비스
 *
 * History
 *  2025/12/28 - 동근 최초 작성
 *  2026/01/03 - 동근 권한 인가 정책 추가
 * </pre>
 *
 * @author 동근
 * @version 1.1
 */
@PayrollAdminOnly
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentHistoryMapper mapper;

    /**
     * 급여 조회 목록을 검색 조건 기반으로 페이징 조회한다.
     *
     * @param req 급여 조회 검색 조건
     * @param page 페이지 번호 (1-based)
     * @param size 페이지당 조회 건수
     * @return 급여 조회 목록(페이징)
     * @throws IllegalArgumentException salaryMonth가 누락/공백인 경우
     */
    public PageResponse<PayrollPaymentSearchRowResponseDTO> search(
            PayrollPaymentSearchRequestDTO req,
            int page,
            int size
    ) {
        if (req.salaryMonth() == null || req.salaryMonth().isBlank()) {
            throw new IllegalArgumentException("salaryMonth(YYYY-MM)는 필수입니다.");
        }

        int totalCount = mapper.countPayrollSearch(req);

        PageInfo pageInfo = PageCalculator.calculate(page, size, totalCount);

        List<PayrollPaymentSearchRowResponseDTO> items = (totalCount == 0)
                ? List.of()
                : mapper.selectPayrollSearch(req, pageInfo.getOffset(), pageInfo.getSize());

        return PageResponse.of(items, pageInfo.getPage() - 1, pageInfo.getSize(), totalCount);
    }


    /**
     * 급여 조회 상세 정보를 조회한다.
     *
     * @param payrollId 급여 ID
     * @return 급여 상세 정보 (요약 + breakdown 항목)
     * @throws IllegalArgumentException payrollId에 해당하는 급여 정보가 없는 경우
     */
    public PayrollPaymentDetailResponseDTO getDetail(Integer payrollId) {
        PayrollPaymentDetailSummaryResponseDTO summary = mapper.selectPayrollDetail(payrollId);
        if (summary == null) {
            throw new IllegalArgumentException("해당 payrollId의 급여 정보가 없습니다. payrollId=" + payrollId);
        }

        List<PayrollPaymentDetailResponseDTO.PayrollItemRow> items = mapper.selectPayrollItems(payrollId);

        return new PayrollPaymentDetailResponseDTO(
                summary.payrollId(),
                summary.salaryMonth(),
                summary.payrollStatus(),
                summary.employeeId(),
                summary.employeeNumber(),
                summary.employeeName(),
                summary.departmentName(),
                summary.jobTitleName(),
                summary.baseSalary(),
                summary.allowanceTotal(),
                summary.deductionTotal(),
                summary.totalPay(),
                summary.netPay(),
                items
        );
    }
}

