package com.c4.hero.domain.payroll.policy.service;

import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.common.type.PolicyStatus;
import com.c4.hero.domain.payroll.policy.dto.request.PolicyCopyRequestDTO;
import com.c4.hero.domain.payroll.policy.dto.response.PolicyResponseDTO;
import com.c4.hero.domain.payroll.policy.entity.PayrollItemPolicy;
import com.c4.hero.domain.payroll.policy.entity.PayrollItemPolicyTarget;
import com.c4.hero.domain.payroll.policy.entity.PayrollPolicy;
import com.c4.hero.domain.payroll.policy.entity.PolicyConfig;
import com.c4.hero.domain.payroll.policy.repository.PayrollItemPolicyRepository;
import com.c4.hero.domain.payroll.policy.repository.PayrollItemPolicyTargetRepository;
import com.c4.hero.domain.payroll.policy.repository.PayrollPolicyRepository;
import com.c4.hero.domain.payroll.policy.repository.PolicyConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <pre>
 * Class Name : PayrollPolicyCopyTxService
 * Description : 급여 정책 복사 전용 트랜잭션 서비스
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
@Transactional
public class PayrollPolicyCopyTxService {
    private final PayrollPolicyRepository policyRepository;
    private final PolicyConfigRepository configRepository;
    private final PayrollItemPolicyRepository itemPolicyRepository;
    private final PayrollItemPolicyTargetRepository targetRepository;

    /**
     * 급여 정책 복사
     *
     * @param sourcePolicyId 복사 대상 원본 정책 ID
     * @param req 정책 복사 옵션 요청 DTO (null 가능)
     * @return 복사된 신규 정책 정보
     */
    public PolicyResponseDTO copy(Integer sourcePolicyId, PolicyCopyRequestDTO req) {

        PayrollPolicy src = policyRepository.findById(sourcePolicyId)
                .orElseThrow(() -> new IllegalArgumentException("원본 정책이 존재하지 않습니다."));

        String newName = (req != null && req.policyName() != null && !req.policyName().isBlank())
                ? req.policyName()
                : src.getPolicyName() + " (copy)";

        String newFrom = (req != null && req.salaryMonthFrom() != null && !req.salaryMonthFrom().isBlank())
                ? req.salaryMonthFrom()
                : src.getSalaryMonthFrom();

        String newTo = (req != null) ? req.salaryMonthTo() : src.getSalaryMonthTo();

        PayrollPolicy copied = policyRepository.save(PayrollPolicy.builder()
                .policyName(newName)
                .status(PolicyStatus.DRAFT)
                .salaryMonthFrom(newFrom)
                .salaryMonthTo(newTo)
                .activeYn("Y")
                .build());

        Integer newPolicyId = copied.getPolicyId();

        List<PolicyConfig> srcConfigs = configRepository.findAllByPolicyId(src.getPolicyId());
        for (PolicyConfig c : srcConfigs) {
            configRepository.save(PolicyConfig.builder()
                    .policyId(newPolicyId)
                    .configKey(c.getConfigKey())
                    .valueType(c.getValueType())
                    .configValue(c.getConfigValue())
                    .description(c.getDescription())
                    .activeYn(c.getActiveYn())
                    .build());
        }

        List<PayrollItemPolicy> srcItems = itemPolicyRepository.findAllByPolicyId(src.getPolicyId());

        for (PayrollItemPolicy item : srcItems) {
            PayrollItemPolicy newItem = itemPolicyRepository.save(PayrollItemPolicy.builder()
                    .policyId(newPolicyId)
                    .itemType(item.getItemType())
                    .itemCode(item.getItemCode())
                    .calcMethod(item.getCalcMethod())
                    .fixedAmount(item.getFixedAmount())
                    .rate(item.getRate())
                    .baseAmountType(item.getBaseAmountType())
                    .roundingUnit(item.getRoundingUnit())
                    .roundingMode(item.getRoundingMode())
                    .salaryMonthFrom(item.getSalaryMonthFrom())
                    .salaryMonthTo(item.getSalaryMonthTo())
                    .priority(item.getPriority())
                    .activeYn(item.getActiveYn())
                    .build());

            Integer newItemPolicyId = newItem.getItemPolicyId();

            List<PayrollItemPolicyTarget> srcTargets = targetRepository.findAllByItemPolicyId(item.getItemPolicyId());
            for (PayrollItemPolicyTarget t : srcTargets) {
                targetRepository.save(PayrollItemPolicyTarget.builder()
                        .itemPolicyId(newItemPolicyId)
                        .payrollTargetType(t.getPayrollTargetType())
                        .targetValue(t.getTargetValue())
                        .build());
            }
        }

        return new PolicyResponseDTO(
                copied.getPolicyId(),
                copied.getPolicyName(),
                copied.getStatus(),
                copied.getSalaryMonthFrom(),
                copied.getSalaryMonthTo(),
                copied.getActiveYn()
        );
    }
}
