package com.c4.hero.domain.payroll.policy.service;

import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.common.type.PolicyStatus;
import com.c4.hero.domain.payroll.policy.dto.request.PolicyActivateRequestDTO;
import com.c4.hero.domain.payroll.policy.dto.response.PolicyResponseDTO;
import com.c4.hero.domain.payroll.policy.entity.PayrollPolicy;
import com.c4.hero.domain.payroll.policy.entity.PolicyConfig;
import com.c4.hero.domain.payroll.policy.repository.PayrollPolicyRepository;
import com.c4.hero.domain.payroll.policy.repository.PolicyConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;
/**
 * <pre>
 * Class Name : PayrollPolicyTxService
 * Description : 급여 정책(PayrollPolicy) 상태 전이 트랜잭션 서비스
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 *  2026/01/03 - 동근 권한 인가 정책 추가
 * </pre>
 *
 * @author 동근
 * @version 1.1
 */
@PayrollAdminOnly
@Service
@RequiredArgsConstructor
public class PayrollPolicyTxService {

    private final PayrollPolicyRepository policyRepository;
    private final PolicyConfigRepository configRepository;

    /** 급여월 포맷(YYYY-MM) 검증용 정규식 */
    private static final Pattern YM = Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])$");

    /**
     * 급여 정책 활성화 처리
     *  - 기존 ACTIVE 정책이 존재하면 필요 시 만료(EXPIRED) 처리
     *  - 지정된 정책을 ACTIVE로 전환하고 적용 기간을 확정
     *
     * @param policyId 활성화할 정책 ID
     * @param req 활성화 요청(적용 시작/종료 급여월)
     * @return 활성화된 정책 응답 DTO
     */
    @Transactional
    public PolicyResponseDTO activate(Integer policyId, PolicyActivateRequestDTO req) {
        validatePeriod(req.salaryMonthFrom(), req.salaryMonthTo());

        List<PolicyConfig> configs = configRepository.findAllByPolicyId(policyId);

        String payday = findConfigValue(configs, "PAYDAY_DAY");
        String closeDay = findConfigValue(configs, "CLOSE_DAY");

        if (payday == null || payday.isBlank())
            throw new IllegalStateException("정책 활성화 전 PAYDAY_DAY(급여일) 설정이 필요합니다.");
        if (closeDay == null || closeDay.isBlank())
            throw new IllegalStateException("정책 활성화 전 CLOSE_DAY(마감일) 설정이 필요합니다.");

        validateDay("PAYDAY_DAY", payday);
        validateDay("CLOSE_DAY", closeDay);

        PayrollPolicy target = policyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("정책이 존재하지 않습니다. policyId=" + policyId));

        policyRepository.findTop1ByStatusOrderByPolicyIdDesc(PolicyStatus.ACTIVE)
                .ifPresent(active -> {
                    if (!active.getPolicyId().equals(target.getPolicyId())) active.expire();
                });

        target.activate(req.salaryMonthFrom(), req.salaryMonthTo());

        PayrollPolicy saved = policyRepository.save(target);

        return new PolicyResponseDTO(
                saved.getPolicyId(),
                saved.getPolicyName(),
                saved.getStatus(),
                saved.getSalaryMonthFrom(),
                saved.getSalaryMonthTo(),
                saved.getActiveYn()
        );
    }

    /**
     * 활성 상태의 공통 설정 값 조회
     *
     * @param configs 정책 공통 설정 목록
     * @param key 조회할 설정 키
     * @return 설정 값 (없으면 null)
     */
    private String findConfigValue(List<PolicyConfig> configs, String key) {
        return configs.stream()
                .filter(c -> key.equals(c.getConfigKey()) && "Y".equalsIgnoreCase(c.getActiveYn()))
                .findFirst()
                .map(PolicyConfig::getConfigValue)
                .orElse(null);
    }

    /**
     * 일(day) 값 검증 (1~31)
     *
     * @param key 설정 키명
     * @param value 설정 값
     */
    private void validateDay(String key, String value) {
        int day;
        try {
            day = Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalStateException(key + "는 숫자여야 합니다.");
        }
        if (day < 1 || day > 31) {
            throw new IllegalStateException(key + "는 1~31 범위여야 합니다.");
        }
    }

    /**
     * 정책 적용 기간(급여월) 검증
     *
     * @param from : 필수, YYYY-MM 형식
     * @param to : 선택, YYYY-MM 형식이며 from <= to
     */
    private void validatePeriod(String from, String to) {
        if (from == null || from.isBlank()) {
            throw new IllegalArgumentException("salaryMonthFrom은 필수입니다.");
        }
        if (!YM.matcher(from).matches()) {
            throw new IllegalArgumentException("salaryMonthFrom 형식이 올바르지 않습니다. (YYYY-MM)");
        }

        if (to != null && !to.isBlank()) {
            if (!YM.matcher(to).matches()) {
                throw new IllegalArgumentException("salaryMonthTo 형식이 올바르지 않습니다. (YYYY-MM)");
            }
            if (from.compareTo(to) > 0) {
                throw new IllegalArgumentException("salaryMonthFrom은 salaryMonthTo보다 클 수 없습니다.");
            }
        }
    }
}
