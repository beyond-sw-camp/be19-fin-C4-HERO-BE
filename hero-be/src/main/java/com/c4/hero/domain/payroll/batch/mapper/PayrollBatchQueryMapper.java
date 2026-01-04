package com.c4.hero.domain.payroll.batch.mapper;

import com.c4.hero.domain.payroll.batch.dto.PayrollBatchDetailResponseDTO;
import com.c4.hero.domain.payroll.batch.dto.PayrollBatchListResponseDTO;
import com.c4.hero.domain.payroll.batch.dto.PayrollBatchTargetEmployeeResponseDTO;
import com.c4.hero.domain.payroll.batch.dto.PayrollEmployeeResultResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <pre>
 * Mapper Name : PayrollBatchQueryMapper
 * Description : 급여 배치 조회 전용 MyBatis 매퍼
 *
 * 역할
 *  - 급여 배치 목록 조회 (월/상태 필터)
 *  - 급여 배치 상세 조회 및 처리 현황 집계
 *  - 배치별 사원 급여 계산 결과 조회
 *  - 배치 생성 시 대상 사원 목록 조회
 *
 * History
 *  2025/12/15 - 동근 최초 작성
 * </pre>
 *
 *  @author 동근
 *  @version 1.0
 */
@Mapper
public interface PayrollBatchQueryMapper {

    /**
     * 급여 배치 목록 조회
     *
     * @param month  급여월 (YYYY-MM), 선택 조건
     * @param status 배치 상태 (READY / CALCULATED / CONFIRMED / PAID), 선택 조건
     * @return 급여 배치 목록
     */
    List<PayrollBatchListResponseDTO> selectBatchList(
            @Param("month") String month,
            @Param("status") String status
    );

    /**
     * 급여 배치 상세 조회 + 처리 현황 집계
     *
     * @param batchId 급여 배치 ID
     * @return 급여 배치 상세 정보 및 사원별 처리 상태 집계 결과
     */
    PayrollBatchDetailResponseDTO selectBatchDetail(
            @Param("batchId") Integer batchId
    );

    /**
     * 배치별 사원 급여 계산 결과 목록 조회
     *
     * @param batchId 급여 배치 ID
     * @return 배치에 포함된 사원 급여 계산 결과 목록
     */
    List<PayrollEmployeeResultResponseDTO> selectPayrollEmployees(
            @Param("batchId") Integer batchId
    );

    /**
     * 급여 배치 대상 사원 목록 조회
     *
     * @return 급여 배치 대상 사원 목록
     */
    List<PayrollBatchTargetEmployeeResponseDTO> selectBatchTargetEmployees();
}

