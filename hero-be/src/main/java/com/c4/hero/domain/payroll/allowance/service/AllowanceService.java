package com.c4.hero.domain.payroll.allowance.service;


import com.c4.hero.domain.auth.security.PayrollAdminOnly;
import com.c4.hero.domain.payroll.allowance.entity.Allowance;
import com.c4.hero.domain.payroll.allowance.repository.AllowanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <pre>
 * Service Name : AllowanceService
 * Description  : 수당(Allowance) 마스터 관리 서비스
 *
 * History
 *  2025/12/22 - 동근 최초 작성
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
public class AllowanceService {

    private final AllowanceRepository allowanceRepository;

    /**
     * 전체 수당 목록 조회
     *
     * @param activeYn Y/N 필터 (null이면 전체 조회)
     * @return 수당 목록
     */
    public List<Allowance> getAll(String activeYn) {
        if (activeYn == null) {
            return allowanceRepository.findAll();
        }
        return allowanceRepository.findAllByActiveYn(activeYn);
    }

    /**
     * 수당 단건 조회
     *
     * @param allowanceId 수당 코드
     * @return Allowance 엔티티
     *
     * @throws IllegalArgumentException 존재하지 않을 경우
     */
    public Allowance getOne(String allowanceId) {
        return allowanceRepository.findById(allowanceId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수당 코드입니다. id=" + allowanceId));
    }

    /**
     * 수당 생성
     *
     * @param allowance 생성할 Allowance 엔티티(allowanceId 포함)
     * @return 저장된 Allowance 엔티티
     *
     * @throws IllegalStateException allowanceId 중복일 경우
     */
    @Transactional
    public Allowance create(Allowance allowance) {
        if (allowanceRepository.existsByAllowanceId(allowance.getAllowanceId())) {
            throw new IllegalStateException("이미 존재하는 수당 코드입니다. id=" + allowance.getAllowanceId());
        }
        return allowanceRepository.save(allowance);
    }

    /**
     * 수당 정보 수정
     *
     * @param allowanceId   대상 수당 코드
     * @param name          수당명
     * @param desc          설명
     * @param defaultAmount 기본 금액
     * @param taxableYn     과세 여부(Y/N)
     */
    @Transactional
    public void update(String allowanceId, String name, String desc, Integer defaultAmount, String taxableYn) {
        Allowance allowance = getOne(allowanceId);
        allowance.update(name, desc, defaultAmount, taxableYn);
    }

    /**
     * 수당 비활성화 처리
     *
     * @param allowanceId 대상 수당 코드
     */
    @Transactional
    public void deactivate(String allowanceId) {
        Allowance allowance = getOne(allowanceId);
        allowance.deactivate();
    }

    /**
     * 수당 활성화 처리
     *
     * @param allowanceId 대상 수당 코드
     */
    @Transactional
    public void activate(String allowanceId) {
        Allowance allowance = getOne(allowanceId);
        allowance.activate();
    }
}
