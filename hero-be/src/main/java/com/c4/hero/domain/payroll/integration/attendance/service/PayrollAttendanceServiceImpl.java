package com.c4.hero.domain.payroll.integration.attendance.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.domain.payroll.integration.attendance.mapper.PayrollAttendanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

/**
 * <pre>
 * Class Name : PayrollAttendanceServiceImpl
 * Description : 급여 계산을 위한 근태 연계 서비스 구현체
 *
 * 역할
 *  - 사원의 기본급 조회
 *  - 특정 월 기준 근태 데이터를 기반으로 초과근무 수당 계산
 *
 * 적용 범위 (MVP)
 *  - 월 기준 근태 집계
 *  - 기준 근무 시간: 209시간
 *  - 초과근무 수당 배율: 1.5배
 *
 * History
 *  2025/12/15 - 동근 최초 작성
 * </pre>
 *
 *  @author 동근
 *  @version 1.0
 */

@Service
@RequiredArgsConstructor
public class PayrollAttendanceServiceImpl implements PayrollAttendanceService {

    /**
     * 근태 데이터 조회용 매퍼
     */
    private final PayrollAttendanceMapper mapper;

    /**
     * 사원의 기본급 조회
     *
     * @param employeeId 사원 ID
     * @return 기본급 금액
     *
     * 처리 정책
     *  - 기본급 정보가 없는 경우 0 반환
     *
     * 사용 목적
     *  - 시급 계산 및 초과근무 수당 산출 기준 값
     */
    @Override
    public int getBaseSalary(Integer employeeId) {
        Integer base = mapper.selectBaseSalary(employeeId);
        return base == null ? 0 : base;
    }

    /**
     * 특정 월의 초과근무 수당 계산
     *
     * @param salaryMonth 급여월 (YYYY-MM)
     * @param employeeId  사원 ID
     * @return 초과근무 수당 금액
     *
     * 계산 흐름
     *  1. 급여월 기준 조회 기간 산정 (YYYY-MM-01 ~ YYYY-MM-31)
     *  2. 근태 기록 존재 여부 검증
     *  3. 월 총 근무 시간(분) 집계
     *  4. 기준 근무 시간(209시간) 초과분 계산
     *  5. 시급 계산 (기본급 / 209시간)
     *  6. 초과근무 수당 = 초과근무시간 × 시급 × 1.5
     *
     * 예외 처리
     *  - 해당 월에 근태 기록이 없는 경우 예외 발생
     *
     * MVP 개발(추후에 심화 개발 후 변경 할 예정입니당)
     *  - 기준 근무 시간: 209시간
     *  - 초과근무 배율: 1.5
     *  - 시급은 소수점 절삭
     *
     * @throws BusinessException PAYROLL_ATTENDANCE_LOG_NOT_FOUND
     *         해당 월에 근태 기록이 존재하지 않는 경우
     */
    @Override
    public int calculateOvertime(String salaryMonth, Integer employeeId) {
        YearMonth ym = YearMonth.parse(salaryMonth); // "YYYY-MM"
        String start = ym.atDay(1).toString();       // YYYY-MM-01
        String end = ym.atEndOfMonth().toString();   // YYYY-MM-28/29/30/31

        int cnt = mapper.countAttendanceInMonth(employeeId, start, end);
        // 근태 기록이 없는 경우 (입사/퇴사/휴직 등 정상 케이스)
        if (cnt == 0) return 0;
        // 월 총 근무 시간(분 단위)
        Integer workedMinObj = mapper.sumWorkedMinutesInMonth(employeeId, start, end);
        int workedMin = workedMinObj == null ? 0 : workedMinObj;

        // 기준 근무 시간 (MVP 기준: 209시간)
        int standardMin = 209 * 60;

        // 초과근무 시간(분)
        int overtimeMin = Math.max(0, workedMin - standardMin);

        // 시급계산 = baseSalary / 209시간 (MVP)
        int hourly = (int) Math.floor((double)getBaseSalary(employeeId) / 209.0);

        // 초과근무 OT수당 계산 = OT시간 * 시급 * 1.5 (MVP  1.5배 가산)
        double overtimeHours = overtimeMin / 60.0;
        return (int) Math.round(overtimeHours * hourly * 1.5);
    }
}
