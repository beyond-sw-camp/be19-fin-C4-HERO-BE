package com.c4.hero.domain.payroll.batch.service;

import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.batch.entity.PayrollBatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <pre>
 * Class Name : PayrollCalculationService
 * Description : 월별 급여 배치 계산 서비스
 *
 *  급여 배치 시스템 마무리 되면 수당+공제 기능 추가 할 예정입니당
 *
 * History
 *  2025/12/15 - 동근 최초 작성
 *  2025/12/18 - 동근 사원 단위 계산/Tx를 PayrollEmployeeCalculateTxService 로 분리
 *             - 클래스 레벨 트랜잭션 제거
 *  2026/01/03 - 동근 권한 인가 정책 추가
 * </pre>
 *
 *  @author 동근
 *  @version 1.2
 */
@PayrollAdminOnly
@Service
@RequiredArgsConstructor
public class PayrollCalculationService {

    private final PayrollEmployeeCalculateTxService employeeTxService;


    /**
     * 배치 내 사원 리스트 대상 급여 계산 실행
     *
     * @param batch       급여 배치 엔티티
     * @param employeeIds 계산 대상 사원 ID 목록
     */
    public void calculateEmployees(PayrollBatch batch, List<Integer> employeeIds) {
        for (Integer empId : employeeIds) {
            employeeTxService.calculateOne(batch, empId); // 사원 단위 분리 트랜잭션 처리
        }
    }
}

