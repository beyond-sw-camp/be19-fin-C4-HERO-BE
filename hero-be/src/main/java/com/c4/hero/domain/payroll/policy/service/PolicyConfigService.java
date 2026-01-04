package com.c4.hero.domain.payroll.policy.service;

import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.common.type.PayrollConfigKey;
import com.c4.hero.domain.payroll.policy.dto.response.PolicyConfigResponseDTO;
import com.c4.hero.domain.payroll.policy.dto.request.PolicyConfigUpsertRequestDTO;
import com.c4.hero.domain.payroll.policy.entity.PolicyConfig;
import com.c4.hero.domain.payroll.policy.repository.PolicyConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <pre>
 * Class Name : PolicyConfigService
 * Description : 급여 정책 공통 설정(PolicyConfig) 관리 서비스
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
@Transactional(readOnly = true)
public class PolicyConfigService {

    private final PolicyConfigRepository configRepository;

    /**
     * 특정 급여 정책의 공통 설정 목록 조회
     *
     * @param policyId 급여 정책 ID
     * @return 정책에 속한 공통 설정 목록(응답 DTO)
     */
    public List<PolicyConfigResponseDTO> getConfigs(Integer policyId) {
        return configRepository.findAllByPolicyId(policyId).stream()
                .map(c -> new PolicyConfigResponseDTO(
                        c.getConfigKey(),
                        c.getValueType(),
                        c.getConfigValue(),
                        c.getDescription(),
                        c.getActiveYn()
                ))
                .toList();
    }

    /**
     * 정책 공통 설정 일괄 생성/수정(upsert)
     *  - configKey 기준으로 기존 설정이 있으면 수정, 없으면 신규 생성
     *  - 전달된 설정 목록을 순회하며 각각 독립적으로 처리
     *
     * @param policyId 급여 정책 ID
     * @param reqs 설정 생성/수정 요청 목록
     */
    @Transactional
    public void upsertConfigs(Integer policyId, List<PolicyConfigUpsertRequestDTO> reqs) {
        if (reqs == null) return;

        for (PolicyConfigUpsertRequestDTO req : reqs) {

            if (req.configKey() == null || req.configKey().isBlank())
                throw new IllegalArgumentException("configKey는 필수입니다.");
            if (!PayrollConfigKey.ALLOWED.contains(req.configKey()))
                throw new IllegalArgumentException("지원하지 않는 configKey입니다. key=" + req.configKey());

            if (req.valueType() == null || req.valueType().isBlank())
                throw new IllegalArgumentException("valueType은 필수입니다.");
            if (req.configValue() == null)
                throw new IllegalArgumentException("configValue는 필수입니다.");

            String value = req.configValue().trim();
            if (value.isEmpty()) {
                throw new IllegalArgumentException("configValue는 빈 값일 수 없습니다. key=" + req.configKey());
            }

            validateConfigValue(req.configKey(), value);

            PolicyConfig config = configRepository.findByPolicyIdAndConfigKey(policyId, req.configKey())
                    .orElseGet(() -> PolicyConfig.builder()
                            .policyId(policyId)
                            .configKey(req.configKey())
                            .valueType(req.valueType())
                            .configValue(value)
                            .description(req.description())
                            .activeYn(req.activeYn() == null ? "Y" : req.activeYn())
                            .build());

            config.apply(req.valueType(), value, req.description(), req.activeYn());
            configRepository.save(config);
        }
    }

    /**
     * configKey별 최소 검증
     * - 추후 키 늘어나면 여기 switch에 케이스 추가
     */
    private void validateConfigValue(String key, String value) {
        switch (key) {
            case "PAYDAY_DAY", "CLOSE_DAY", "PAYSLIP_SEND_DAY"-> {
                int day;
                try {
                    day = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(key + "는 숫자여야 합니다. value=" + value);
                }
                if (day < 1 || day > 31) {
                    throw new IllegalArgumentException(key + "는 1~31 범위여야 합니다. value=" + value);
                }
            }
            case "ROUNDING_MODE" -> {
                if (!(value.equals("HALF_UP") || value.equals("FLOOR") || value.equals("CEIL"))) {
                    throw new IllegalArgumentException("ROUNDING_MODE는 HALF_UP/FLOOR/CEIL 중 하나여야 합니다. value=" + value);
                }
            }
            case "ROUNDING_UNIT" -> {
                int unit;
                try {
                    unit = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("ROUNDING_UNIT은 숫자여야 합니다. value=" + value);
                }
                if (unit <= 0) {
                    throw new IllegalArgumentException("ROUNDING_UNIT은 0보다 커야 합니다. value=" + value);
                }
            }
            case "HOLIDAY_RULE" -> {
                if (!(value.equals("PREV_BUSINESS_DAY")
                        || value.equals("NEXT_BUSINESS_DAY")
                        || value.equals("NONE"))) {

                    throw new IllegalArgumentException(
                            "HOLIDAY_RULE은 PREV_BUSINESS_DAY/NEXT_BUSINESS_DAY/NONE 중 하나여야 합니다. value=" + value
                    );
                }
            }
            default -> {
            }
        }
    }
}
