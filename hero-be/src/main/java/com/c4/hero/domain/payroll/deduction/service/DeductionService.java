package com.c4.hero.domain.payroll.deduction.service;

import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.deduction.entity.Deduction;
import com.c4.hero.domain.payroll.deduction.repository.DeductionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <pre>
 * Service Name : DeductionService
 * Description  : 공제(Deduction) 마스터 관리 서비스
 *
 * History
 *  2025/12/22 - 동근 최초 작성
 *  2026/01/03 - 동근 권한 인가 정책 추가
 * </pre>
 *
 * @version 1.1
 * @author 동근
 */
@PayrollAdminOnly
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeductionService {

    private final DeductionRepository deductionRepository;

    /**
     * 공제 목록 조회
     *
     * @param activeYn Y/N 필터(null이면 전체 조회)
     * @return 공제 목록
     */
    public List<Deduction> getAll(String activeYn) {
        if (activeYn == null) {
            return deductionRepository.findAll();
        }
        return deductionRepository.findAllByActiveYn(activeYn);
    }

    /**
     * 공제 단건 조회
     *
     * @param deductionId 공제 코드
     * @return Deduction 엔티티
     * @throws IllegalArgumentException 존재하지 않는 경우
     */
    public Deduction getOne(String deductionId) {
        return deductionRepository.findById(deductionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공제 코드입니다. id=" + deductionId));
    }

    /**
     * 공제 생성
     *
     * @param deduction 생성할 공제 엔티티
     * @return 저장된 Deduction
     * @throws IllegalStateException deductionId가 이미 존재할 경우
     */
    @Transactional
    public Deduction create(Deduction deduction) {
        if (deductionRepository.existsByDeductionId(deduction.getDeductionId())) {
            throw new IllegalStateException("이미 존재하는 공제 코드입니다. id=" + deduction.getDeductionId());
        }
        return deductionRepository.save(deduction);
    }

    /**
     * 공제 정보 수정
     *
     * @param deductionId     공제 코드
     * @param name            공제명
     * @param desc            설명
     * @param deductionType   TAX/INSURANCE/ETC
     * @param calculationType FIXED/RATE
     * @param rate            RATE 방식일 경우 비율(%)
     * @param fixedAmount     FIXED 방식일 경우 정액
     */
    @Transactional
    public void update(
            String deductionId,
            String name,
            String desc,
            String deductionType,
            String calculationType,
            Double rate,
            Integer fixedAmount
    ) {
        Deduction deduction = getOne(deductionId);
        deduction.update(name, desc, deductionType, calculationType, rate, fixedAmount);
    }

    /**
     * 공제 비활성화
     *
     * @param deductionId 공제 코드
     */
    @Transactional
    public void deactivate(String deductionId) {
        Deduction deduction = getOne(deductionId);
        deduction.deactivate();
    }

    /**
     * 공제 활성화
     *
     * @param deductionId 공제 코드
     */
    @Transactional
    public void activate(String deductionId) {
        Deduction deduction = getOne(deductionId);
        deduction.activate();
    }
}