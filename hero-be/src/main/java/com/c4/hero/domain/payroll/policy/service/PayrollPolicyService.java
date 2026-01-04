package com.c4.hero.domain.payroll.policy.service;

import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.common.type.PolicyStatus;
import com.c4.hero.domain.payroll.policy.dto.request.PolicyCreateRequestDTO;
import com.c4.hero.domain.payroll.policy.dto.request.PolicyUpdateRequestDTO;
import com.c4.hero.domain.payroll.policy.dto.response.PolicyResponseDTO;
import com.c4.hero.domain.payroll.policy.entity.PayrollItemPolicy;
import com.c4.hero.domain.payroll.policy.entity.PayrollPolicy;
import com.c4.hero.domain.payroll.policy.repository.PayrollItemPolicyRepository;
import com.c4.hero.domain.payroll.policy.repository.PayrollItemPolicyTargetRepository;
import com.c4.hero.domain.payroll.policy.repository.PayrollPolicyRepository;
import com.c4.hero.domain.payroll.policy.repository.PolicyConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

/**
 * <pre>
 * Class Name : PayrollPolicyService
 * Description : 급여 정책(PayrollPolicy) 생성/조회 서비스
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
public class PayrollPolicyService {

    private final PayrollPolicyRepository policyRepository;
    private final PolicyConfigRepository configRepository;
    private final PayrollItemPolicyRepository itemPolicyRepository;
    private final PayrollItemPolicyTargetRepository targetRepository;

    /** 급여월 포맷(YYYY-MM) 검증용 정규식 */
    private static final Pattern YM = Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])$");

    /**
     * 급여 정책 생성
     *  - 요청값 검증 후 정책을 DRAFT 상태로 저장
     *  - 적용 기간은 급여월(YYYY-MM) 기준으로만 관리
     *
     * @param req 정책 생성 요청 DTO
     * @return 생성된 정책 응답 DTO
     */
    @Transactional
    public PolicyResponseDTO createPolicy(PolicyCreateRequestDTO req) {
        if (req.policyName() == null || req.policyName().isBlank())
            throw new IllegalArgumentException("policyName은 필수입니다.");
        validatePeriod(req.salaryMonthFrom(), req.salaryMonthTo());

        PayrollPolicy saved = policyRepository.save(PayrollPolicy.builder()
                .policyName(req.policyName())
                .status(PolicyStatus.DRAFT)
                .salaryMonthFrom(req.salaryMonthFrom())
                .salaryMonthTo(req.salaryMonthTo())
                .activeYn("Y")
                .build());

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
     * 급여 정책 목록 조회
     *
     * @return 정책 목록(응답 DTO)
     */
    public List<PolicyResponseDTO> getPolicies() {
        return policyRepository.findAll().stream()
                .map(p -> new PolicyResponseDTO(
                        p.getPolicyId(),
                        p.getPolicyName(),
                        p.getStatus(),
                        p.getSalaryMonthFrom(),
                        p.getSalaryMonthTo(),
                        p.getActiveYn()
                ))
                .toList();
    }
    /**
     * 현재 ACTIVE 상태의 정책 조회
     *  - 활성 정책이 없으면 예외를 발생
     *
     * @return 활성 정책(응답 DTO)
     */
    public PolicyResponseDTO getActivePolicy() {
        PayrollPolicy p = policyRepository.findTop1ByStatusOrderByPolicyIdDesc(PolicyStatus.ACTIVE)
                .orElseThrow(() -> new IllegalStateException("ACTIVE 정책이 없습니다."));

        return new PolicyResponseDTO(
                p.getPolicyId(),
                p.getPolicyName(),
                p.getStatus(),
                p.getSalaryMonthFrom(),
                p.getSalaryMonthTo(),
                p.getActiveYn()
        );
    }

    /**
     * 적용 기간(급여월) 검증
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

    /**
     *  급여 정책 수정
     */
    @Transactional
    public PolicyResponseDTO updatePolicy(Integer policyId, PolicyUpdateRequestDTO req) {
        PayrollPolicy p = policyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("정책이 존재하지 않습니다."));

        if (req.policyName() != null && !req.policyName().isBlank()) {
            p.update(req.policyName(), null, null, null);
        }

        boolean fromProvided = req.salaryMonthFrom() != null && !req.salaryMonthFrom().isBlank();
        boolean toProvided = req.salaryMonthTo() != null;

        if (fromProvided || toProvided) {
            if (p.getStatus() != PolicyStatus.DRAFT) {
                throw new IllegalStateException("기간 수정은 DRAFT 정책에서만 가능합니다.");
            }

            String from = fromProvided ? req.salaryMonthFrom() : p.getSalaryMonthFrom();

            String to = (req.salaryMonthTo() != null && req.salaryMonthTo().isBlank()) ? null : req.salaryMonthTo();

            validatePeriod(from, to);
            p.update(null, from, to, null);
        }

        if (req.activeYn() != null && !req.activeYn().isBlank()) {
            p.update(null, null, null, req.activeYn());
        }
        PayrollPolicy saved = policyRepository.save(p);

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
     * DRAFT 정책 삭제
     *  - DRAFT 상태의 정책만 삭제 가능
     *  - 삭제 시 하위 데이터까지 함께 정리
     *    1) ItemPolicyTarget
     *    2) ItemPolicy
     *    3) PolicyConfig
     *    4) PayrollPolicy
     */
    @Transactional
    public void deleteDraftPolicy(Integer policyId) {
        PayrollPolicy p = policyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("정책이 존재하지 않습니다."));

        if (p.getStatus() != PolicyStatus.DRAFT) {
            throw new IllegalStateException("DRAFT 정책만 삭제할 수 있습니다.");
        }

        List<PayrollItemPolicy> items = itemPolicyRepository.findAllByPolicyId(policyId);

        for (PayrollItemPolicy item : items) {
            targetRepository.deleteAllByItemPolicyId(item.getItemPolicyId());
        }

        itemPolicyRepository.deleteAllByPolicyId(policyId);

        configRepository.deleteAllByPolicyId(policyId);

        policyRepository.delete(p);
    }

    /**
     * ACTIVE 정책 만료 처리
     *   ACTIVE 상태의 정책만 만료(EXPIRED) 처리 가능
     *   만료되면 급여 계산에 더 이상 사용되지 않음
     */
    @Transactional
    public PolicyResponseDTO expirePolicy(Integer policyId) {
        PayrollPolicy p = policyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("정책이 존재하지 않습니다."));

        if (p.getStatus() != PolicyStatus.ACTIVE) {
            throw new IllegalStateException("ACTIVE 정책만 만료 처리할 수 있습니다.");
        }

        p.expire();
        PayrollPolicy saved = policyRepository.save(p);

        return new PolicyResponseDTO(
                saved.getPolicyId(),
                saved.getPolicyName(),
                saved.getStatus(),
                saved.getSalaryMonthFrom(),
                saved.getSalaryMonthTo(),
                saved.getActiveYn()
        );
    }
}
