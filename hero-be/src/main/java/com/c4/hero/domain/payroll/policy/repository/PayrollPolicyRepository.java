package com.c4.hero.domain.payroll.policy.repository;

import com.c4.hero.domain.payroll.common.type.PolicyStatus;
import com.c4.hero.domain.payroll.policy.entity.PayrollPolicy;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import java.util.Optional;

/**
 * <pre>
 * Class Name : PayrollPolicyRepository
 * Description : 급여 정책(PayrollPolicy) 관리 Repository
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public interface PayrollPolicyRepository extends JpaRepository<PayrollPolicy, Integer> {

    /**
     * 정책 ID 기준 단건 조회
     *
     * @param policyId 급여 정책 ID
     * @return 급여 정책 엔티티 (없을 수 있음)
     */
    Optional<PayrollPolicy> findById(Integer policyId);

    /**
     * 특정 상태의 정책 존재 여부 확인
     *
     * 용도
     *  - ACTIVE 정책 존재 여부 사전 체크
     *  - 정책 생성/활성화 시 비즈니스 규칙 검증
     *
     * @param status 확인할 정책 상태
     * @return 존재 여부
     */
    boolean existsByStatus(PolicyStatus status);

    /**
     * 특정 상태를 가진 가장 최근 정책 조회 (비관적 락 적용)
     *  - 정책 활성화/전환 시 동시성 문제를 방지하기 위한 용도
     *
     * @param status 조회할 정책 상태
     * @return 해당 상태의 최신 정책 (없을 수 있음)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PayrollPolicy> findTop1ByStatusOrderByPolicyIdDesc(PolicyStatus status);

}
