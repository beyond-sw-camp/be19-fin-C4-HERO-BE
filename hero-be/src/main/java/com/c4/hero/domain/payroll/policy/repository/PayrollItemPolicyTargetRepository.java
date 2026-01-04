package com.c4.hero.domain.payroll.policy.repository;

import com.c4.hero.domain.payroll.policy.entity.PayrollItemPolicyTarget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <pre>
 * Class Name : PayrollItemPolicyTargetRepository
 * Description : 급여 항목 정책 대상(PayrollItemPolicyTarget) 관리 Repository
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public interface PayrollItemPolicyTargetRepository extends JpaRepository<PayrollItemPolicyTarget, Integer> {

    /**
     * 항목 정책 ID 기준 적용 대상 목록 조회
     *
     * @param itemPolicyId 항목 정책 식별자
     * @return 해당 항목 정책에 연결된 대상 목록
     */
    List<PayrollItemPolicyTarget> findAllByItemPolicyId(Integer itemPolicyId);

    /**
     * 항목 정책 ID 기준 적용 대상 전체 삭제
     *  - 항목 정책 수정 시 대상 조건을 재구성하기 위해 사용
     *
     * @param itemPolicyId 항목 정책 식별자
     */
    void deleteAllByItemPolicyId(Integer itemPolicyId);
}
