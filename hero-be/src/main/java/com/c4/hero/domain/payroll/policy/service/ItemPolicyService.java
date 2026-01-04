package com.c4.hero.domain.payroll.policy.service;

import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.common.type.BaseAmountType;
import com.c4.hero.domain.payroll.common.type.ItemType;
import com.c4.hero.domain.payroll.common.type.RoundingModeType;
import com.c4.hero.domain.payroll.policy.dto.response.ItemPolicyResponseDTO;
import com.c4.hero.domain.payroll.policy.dto.request.ItemPolicyTargetRequestDTO;
import com.c4.hero.domain.payroll.policy.dto.request.ItemPolicyUpsertRequestDTO;
import com.c4.hero.domain.payroll.policy.entity.PayrollItemPolicy;
import com.c4.hero.domain.payroll.policy.entity.PayrollItemPolicyTarget;
import com.c4.hero.domain.payroll.policy.repository.PayrollItemPolicyRepository;
import com.c4.hero.domain.payroll.policy.repository.PayrollItemPolicyTargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <pre>
 * Class Name : ItemPolicyService
 * Description : 급여 항목 정책(Item Policy) 관리 서비스
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
public class ItemPolicyService {
    private final PayrollItemPolicyRepository itemPolicyRepository;
    private final PayrollItemPolicyTargetRepository targetRepository;

    /**
     * 특정 정책의 항목 정책 목록 조회
     *  - 기본은 전체 조회(활성만 보고 싶으면 activeYn 필터 메서드로 변경 가능)
     *
     * @param policyId 급여 정책 ID
     * @param type 항목 유형(수당/공제 등)
     * @return 항목 정책 목록(응답 DTO)
     */
    public List<ItemPolicyResponseDTO> getItems(Integer policyId, ItemType type) {
        return itemPolicyRepository.findAllByPolicyIdAndItemType(policyId, type).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 항목 정책 생성
     *  - 요청값 검증 후 엔티티 생성 및 기본값(반올림/기준금액 등) 보정 적용
     *
     * @param policyId 소속 급여 정책 ID
     * @param req 항목 정책 생성/수정 요청 DTO
     * @return 생성된 항목 정책(응답 DTO)
     */
    @Transactional
    public ItemPolicyResponseDTO createItem(Integer policyId, ItemPolicyUpsertRequestDTO req) {
        validate(req);

        PayrollItemPolicy entity = PayrollItemPolicy.builder()
                .policyId(policyId)
                .itemType(req.itemType())
                .itemCode(req.itemCode())
                .calcMethod(req.calcMethod())
                .build();

        entity.applyAll(
                req.calcMethod(), req.fixedAmount(), req.rate(),
                defaultBase(req.baseAmountType()),
                defaultRoundingUnit(req.roundingUnit()),
                defaultRoundingMode(req.roundingMode()),
                req.salaryMonthFrom(), req.salaryMonthTo(),
                req.priority(),
                req.activeYn()
        );

        PayrollItemPolicy saved = itemPolicyRepository.save(entity);
        return toResponse(saved);
    }

    /**
     * 항목 정책 수정
     *  - itemPolicyId 기준으로 엔티티를 조회한 뒤, 요청값을 일괄 반영(applyAll)
     *
     * @param itemPolicyId 수정 대상 항목 정책 ID
     * @param req 항목 정책 생성/수정 요청 DTO
     */
    @Transactional
    public void updateItem(Integer itemPolicyId, ItemPolicyUpsertRequestDTO req) {
        validate(req);

        PayrollItemPolicy item = itemPolicyRepository.findById(itemPolicyId)
                .orElseThrow(() -> new IllegalArgumentException("itemPolicyId가 존재하지 않습니다."));

        item.applyAll(
                req.calcMethod(), req.fixedAmount(), req.rate(),
                defaultBase(req.baseAmountType()),
                defaultRoundingUnit(req.roundingUnit()),
                defaultRoundingMode(req.roundingMode()),
                req.salaryMonthFrom(), req.salaryMonthTo(),
                req.priority(),
                req.activeYn()
        );

        itemPolicyRepository.save(item);
    }

    /**
     * 항목 정책 적용 대상(Target) 목록 교체
     *  - 기존 대상 전체 삭제 후, 전달받은 대상 목록을 재등록
     *  - 대상 타입이 ALL이 아닌 경우 targetValue는 필수로 요구
     *
     * @param itemPolicyId 대상이 연결될 항목 정책 ID
     * @param targets 적용 대상 요청 목록(null이면 빈 목록으로 처리)
     */
    @Transactional
    public void replaceTargets(Integer itemPolicyId, List<ItemPolicyTargetRequestDTO> targets) {
        if (targets == null) targets = List.of();
        targetRepository.deleteAllByItemPolicyId(itemPolicyId);

        for (ItemPolicyTargetRequestDTO t : targets) {
            if (t.payrollTargetType() == null) throw new IllegalArgumentException("targetType은 필수입니다.");
            if (t.payrollTargetType().name().equals("ALL") == false) {
                if (t.targetValue() == null || t.targetValue().isBlank()) {
                    throw new IllegalArgumentException("targetValue는 필수입니다. (ALL 제외)");
                }
            }
            targetRepository.save(PayrollItemPolicyTarget.builder()
                    .itemPolicyId(itemPolicyId)
                    .payrollTargetType(t.payrollTargetType())
                    .targetValue(t.targetValue())
                    .build());
        }
    }

    /**
     * 엔티티 -> 응답 DTO 변환
     */
    private ItemPolicyResponseDTO toResponse(PayrollItemPolicy i) {
        return new ItemPolicyResponseDTO(
                i.getItemPolicyId(),
                i.getPolicyId(),
                i.getItemType(),
                i.getItemCode(),
                i.getCalcMethod(),
                i.getFixedAmount(),
                i.getRate(),
                i.getBaseAmountType(),
                i.getRoundingUnit(),
                i.getRoundingMode(),
                i.getSalaryMonthFrom(),
                i.getSalaryMonthTo(),
                i.getPriority(),
                i.getActiveYn()
        );
    }

    /**
     * 항목 정책 생성/수정 요청값 검증
     *  - calcMethod에 따른 필수값 및 값 범위를 검증하여 도메인 정합성을 보장
     */
    private void validate(ItemPolicyUpsertRequestDTO req) {
        if (req.itemType() == null) throw new IllegalArgumentException("itemType은 필수입니다.");
        if (req.itemCode() == null || req.itemCode().isBlank()) throw new IllegalArgumentException("itemCode는 필수입니다.");
        if (req.calcMethod() == null) throw new IllegalArgumentException("calcMethod는 필수입니다.");
        if (req.salaryMonthFrom() == null || req.salaryMonthFrom().isBlank())
            throw new IllegalArgumentException("salaryMonthFrom은 필수입니다.");

        switch (req.calcMethod()) {
            case FIXED -> {
                if (req.fixedAmount() == null || req.fixedAmount() < 0) {
                    throw new IllegalArgumentException(
                            "FIXED는 fixedAmount(0 이상)가 필요합니다."
                    );
                }
            }
            case RATE -> {
                if (req.rate() == null
                        || req.rate().compareTo(BigDecimal.ZERO) < 0) {

                    throw new IllegalArgumentException(
                            "RATE는 rate(0 이상)가 필요합니다."
                    );
                }
                if (req.baseAmountType() == null) {
                    throw new IllegalArgumentException("RATE는 baseAmountType이 필수입니다.");
                }
            }
            case FORMULA -> {
            }
        }
        if (req.roundingUnit() != null && req.roundingUnit() <= 0) {
            throw new IllegalArgumentException("roundingUnit은 1 이상이어야 합니다.");
        }
        if (req.activeYn() != null && !req.activeYn().equalsIgnoreCase("Y") && !req.activeYn().equalsIgnoreCase("N")) {
            throw new IllegalArgumentException("activeYn은 Y/N만 가능합니다.");
        }
    }

    /** baseAmountType 기본값 보정 */
    private BaseAmountType defaultBase(BaseAmountType t) {
        return (t == null) ? BaseAmountType.BASE_SALARY : t;
    }

    /** roundingUnit 기본값 보정 */
    private Integer defaultRoundingUnit(Integer u) {
        return (u == null) ? 1 : u;
    }

    /** roundingMode 기본값 보정 */
    private RoundingModeType defaultRoundingMode(RoundingModeType m) {
        return (m == null) ? RoundingModeType.HALF_UP : m;
    }

    @Transactional
    public void upsertItems(Integer policyId, ItemType type, List<ItemPolicyUpsertRequestDTO> reqs) {

        List<PayrollItemPolicy> existing =
                itemPolicyRepository.findAllByPolicyIdAndItemType(policyId, type);

        Map<Integer, PayrollItemPolicy> existingById = existing.stream()
                .collect(Collectors.toMap(PayrollItemPolicy::getItemPolicyId, it -> it));

        Set<Integer> survivedIds = new HashSet<>();

        for (ItemPolicyUpsertRequestDTO req : reqs) {

            PayrollItemPolicy item;

            if (req.itemPolicyId() != null && existingById.containsKey(req.itemPolicyId())) {
                item = existingById.get(req.itemPolicyId());
                item.applyAll(
                        req.calcMethod(),
                        req.fixedAmount(),
                        req.rate(),
                        req.baseAmountType(),
                        req.roundingUnit(),
                        req.roundingMode(),
                        req.salaryMonthFrom(),
                        req.salaryMonthTo(),
                        req.priority(),
                        req.activeYn()
                );
            } else {
                item = PayrollItemPolicy.builder()
                        .policyId(policyId)
                        .itemType(type)
                        .itemCode(req.itemCode())
                        .calcMethod(req.calcMethod())
                        .fixedAmount(req.fixedAmount())
                        .rate(req.rate())
                        .baseAmountType(req.baseAmountType())
                        .roundingUnit(req.roundingUnit())
                        .roundingMode(req.roundingMode())
                        .salaryMonthFrom(req.salaryMonthFrom())
                        .salaryMonthTo(req.salaryMonthTo())
                        .priority(req.priority())
                        .activeYn(req.activeYn())
                        .build();

                itemPolicyRepository.save(item);
            }
            survivedIds.add(item.getItemPolicyId());
            targetRepository.deleteAllByItemPolicyId(item.getItemPolicyId());
            if (req.targets() != null) {
                for (ItemPolicyUpsertRequestDTO.ItemPolicyTargetRequest t : req.targets()) {
                    targetRepository.save(
                            PayrollItemPolicyTarget.builder()
                                    .itemPolicyId(item.getItemPolicyId())
                                    .payrollTargetType(t.payrollTargetType())
                                    .targetValue(t.targetValue())
                                    .build()
                    );
                }
            }
        }
        for (PayrollItemPolicy old : existing) {
            if (!survivedIds.contains(old.getItemPolicyId())) {
                targetRepository.deleteAllByItemPolicyId(old.getItemPolicyId());
                itemPolicyRepository.delete(old);
            }
        }
    }

    @Transactional
    public void deleteItem(Integer policyId, Integer itemPolicyId) {
        PayrollItemPolicy item = itemPolicyRepository.findById(itemPolicyId)
                .orElseThrow(() -> new IllegalArgumentException("itemPolicyId가 존재하지 않습니다."));

        if (!Objects.equals(item.getPolicyId(), policyId)) {
            throw new IllegalArgumentException("해당 정책의 항목이 아닙니다.");
        }
        targetRepository.deleteAllByItemPolicyId(itemPolicyId);
        itemPolicyRepository.delete(item);
    }
}