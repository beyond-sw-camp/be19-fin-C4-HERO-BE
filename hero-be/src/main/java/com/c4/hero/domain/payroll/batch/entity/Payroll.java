package com.c4.hero.domain.payroll.batch.entity;

import com.c4.hero.domain.payroll.common.type.PayrollStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Entity Name : Payroll
 * Description : 사원 단위 급여 엔티티
 *
 * 상태 흐름
 *  READY -> CALCULATED -> CONFIRMED
 *              or
 *            FAILED
 *
 * 도메인 규칙
 *  - (employeeId + salaryMonth)는 유일해야 함
 *  - CONFIRMED 이후에는 급여 금액 수정 불가
 *  - FAILED 급여는 배치 내에서 사후 재처리 대상
 *
 * History
 *  2025/12/15 - 동근 최초 작성
 *  2025/12/18 - 동근 도메인 로직 확장
 * </pre>
 *
 *  @author 동근
 *  @version 1.0
 */

@Entity
@Table(name = "tbl_payroll")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer payrollId;

    private String salaryMonth;

    private Integer baseSalary;
    private Integer overtimePay;
    private Integer allowanceTotal;
    private Integer deductionTotal;

    /**
     * 실지급액
     *  - baseSalary + overtimePay + allowanceTotal - deductionTotal
     */
    private Integer totalPay;

    @Enumerated(EnumType.STRING)
    private PayrollStatus status;

    private Integer employeeId;
    private Integer batchId;
    private String errorMessage;
    /**
     * 급여 계산 성공 시 생성 팩토리 메서드
     *
     * @param employeeId 사원 ID
     * @param batchId    급여 배치 ID
     * @param month      급여월 (YYYY-MM)
     * @param base       기본급
     * @param overtime   초과근무 수당
     * @param allowance  수당 합계
     * @param deduction  공제 합계
     * @return 계산 완료 상태의 Payroll 엔티티
     */
    public static Payroll calculated(
            Integer employeeId,
            Integer batchId,
            String month,
            int base,
            int overtime,
            int allowance,
            int deduction
    ) {
        Payroll p = new Payroll();
        p.employeeId = employeeId;
        p.batchId = batchId;
        p.salaryMonth = month;
        p.baseSalary = base;
        p.overtimePay = overtime;
        p.allowanceTotal = allowance;
        p.deductionTotal = deduction;
        p.totalPay = base + overtime + allowance - deduction;
        p.status = PayrollStatus.CALCULATED;
        return p;
    }

    /**
     * 급여 계산 실패 시 생성 팩토리 메서드
     *
     * @param employeeId 사원 ID
     * @param batchId    급여 배치 ID
     * @param month      급여월 (YYYY-MM)
     * @return 계산 실패 상태의 Payroll 엔티티
     */
    public static Payroll failed(
            Integer employeeId,
            Integer batchId,
            String month
    ) {
        Payroll p = new Payroll();
        p.employeeId = employeeId;
        p.batchId = batchId;
        p.salaryMonth = month;
        p.baseSalary = 0;
        p.overtimePay = 0;
        p.allowanceTotal = 0;
        p.deductionTotal = 0;
        p.totalPay = 0;
        p.status = PayrollStatus.FAILED;
        return p;
    }

    /**
     * 급여 확정 처리
     */
    public void lock() {
        this.status = PayrollStatus.CONFIRMED;
    }

    /**
     * 급여가 CONFIRMED 상태인지 여부
     *
     * @return true = 확정됨 / false = 수정 가능
     */
    public boolean isLocked() {
        return this.status == PayrollStatus.CONFIRMED; // PAID 있으면 같이
    }

    /**
     * 기존 Payroll 엔티티에 계산 결과 적용
     * 제약
     *  - 확정(CONFIRMED) 이후에는 수정 불가
     *  - 성공 시 기존 errorMessage 제거
     *
     * @param batchId   배치 ID
     * @param base      기본급
     * @param overtime  초과근무 수당
     * @param allowance 수당 합계
     * @param deduction 공제 합계
     */
    public void applyCalculated(Integer batchId, int base, int overtime, int allowance, int deduction) {
        if (isLocked()) {
            throw new IllegalStateException("CONFIRMED 이후 급여는 수정할 수 없습니다.");
        }
        this.batchId = batchId;
        this.baseSalary = base;
        this.overtimePay = overtime;
        this.allowanceTotal = allowance;
        this.deductionTotal = deduction;
        this.totalPay = base + overtime + allowance - deduction;
        this.status = PayrollStatus.CALCULATED;
        this.errorMessage = null;
    }

    /**
     * 기존 Payroll 엔티티를 계산 실패 상태로 전환
     *
     * @param batchId 배치 ID
     * @param message 실패 사유(프론트 오류 모달에서 표시됨)
     */
    public void markFailed(Integer batchId, String message) {
        if (isLocked()) {
            throw new IllegalStateException("CONFIRMED 이후 급여는 수정할 수 없습니다.");
        }
        this.batchId = batchId;
        this.baseSalary = 0;
        this.overtimePay = 0;
        this.allowanceTotal = 0;
        this.deductionTotal = 0;
        this.totalPay = 0;
        this.status = PayrollStatus.FAILED;
        this.errorMessage = message;
    }

    /**
     * 급여 계산 전 READY 상태의 Payroll 엔티티 생성
     *
     * @param employeeId 사원 ID
     * @param batchId    배치 ID
     * @param month      급여월
     * @return READY 상태의 Payroll
     */
    public static Payroll ready(Integer employeeId, Integer batchId, String month) {
        Payroll p = new Payroll();
        p.employeeId = employeeId;
        p.batchId = batchId;
        p.salaryMonth = month;
        p.baseSalary = 0;
        p.overtimePay = 0;
        p.allowanceTotal = 0;
        p.deductionTotal = 0;
        p.totalPay = 0;
        p.status = PayrollStatus.READY;
        return p;
    }
}
