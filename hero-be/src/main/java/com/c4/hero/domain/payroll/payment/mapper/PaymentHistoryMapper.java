package com.c4.hero.domain.payroll.payment.mapper;

import com.c4.hero.domain.payroll.payment.dto.PayrollPaymentDetailResponseDTO;
import com.c4.hero.domain.payroll.payment.dto.PayrollPaymentDetailSummaryResponseDTO;
import com.c4.hero.domain.payroll.payment.dto.PayrollPaymentSearchRequestDTO;
import com.c4.hero.domain.payroll.payment.dto.PayrollPaymentSearchRowResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <pre>
 * Interface Name : PaymentHistoryMapper
 * Description : 급여 지급/조회 이력 관련 MyBatis Mapper
 *
 * History
 *  2025/12/28 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Mapper
public interface PaymentHistoryMapper {

    /**
     * 특정 bankAccountId를 참조하는 지급 이력이 존재하는지 확인
     * @return 존재하면 1, 없으면 0
     */
    int existsByBankAccountId(@Param("bankAccountId") Integer bankAccountId);

    /**
     * 급여 조회 검색 조건에 따른 전체 건수를 조회한다.
     *
     * @param req 급여 조회 검색 조건
     * @return 전체 조회 건수
     */
    int countPayrollSearch(@Param("req") PayrollPaymentSearchRequestDTO req);

    /**
     * 급여 조회 목록을 페이징 조회한다.
     *
     * @param req 급여 조회 검색 조건
     * @param offset 조회 시작 offset
     * @param limit 조회 건수
     * @return 급여 조회 목록(행) 데이터
     */
    List<PayrollPaymentSearchRowResponseDTO> selectPayrollSearch(
            @Param("req") PayrollPaymentSearchRequestDTO req,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    /**
     * 급여 조회 상세 요약 정보를 조회한다.
     *
     * @param payrollId 급여 ID
     * @return 급여 상세 요약 정보
     */
    PayrollPaymentDetailSummaryResponseDTO selectPayrollDetail(@Param("payrollId") Integer payrollId);

    /**
     * 급여 상세 화면에 표시할 수당/공제 항목 목록을 조회한다.
     *
     * @param payrollId 급여 ID
     * @return 급여 항목(수당/공제) 목록
     */
    List<PayrollPaymentDetailResponseDTO.PayrollItemRow> selectPayrollItems(@Param("payrollId") Integer payrollId);
}

